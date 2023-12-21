package me.andreasmelone.chatsocket;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import me.andreasmelone.chatsocket.models.BasicModel;
import me.andreasmelone.chatsocket.models.IDAssignModel;
import me.andreasmelone.chatsocket.models.MessageModel;
import me.andreasmelone.chatsocket.models.RegisterUsernameModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

public class ChatSocket {
    public static void main(String[] args) {
        new ChatSocket().run(args);
    }

    ModelsRegister models = new ModelsRegister();
    Logger logger = LoggerFactory.getLogger(ChatSocket.class);
    public void run(String[] args) {
        int port = 7000;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        models.register(0, MessageModel.class);
        models.register(1, IDAssignModel.class);
        models.register(2, RegisterUsernameModel.class);

        Javalin app = Javalin.create(
                config -> {
                    config.showJavalinBanner = false;
                }
        ).start(port);

        LinkedHashMap<UUID, WsContext> sessions = new LinkedHashMap<>();
        LinkedHashMap<UUID, User> users = new LinkedHashMap<>();

        Gson gson = new Gson();
        app.ws("/", (ws) -> {
            ws.onConnect(client -> {
                logger.info("[{}] Connected", client.session.getRemoteAddress());
                UUID sessionUUID = UUID.randomUUID();
                sessions.put(sessionUUID, client);
                models.send(client, new IDAssignModel(sessionUUID));
            });
            ws.onMessage(client -> {
                try {
                    BasicModel message = gson.fromJson(client.message(), BasicModel.class);

                    if (models.get(message.id) == null) {
                        logger.error("[{}] Unknown message type: {}", client.session.getRemoteAddress(), message.id);
                        return;
                    }

                    if(models.get(message.id) == RegisterUsernameModel.class) {
                        RegisterUsernameModel registerUsernameModel = gson.fromJson(client.message(), RegisterUsernameModel.class);
                        users.put(registerUsernameModel.getSessionUUID(),
                                new User(registerUsernameModel.username, message.getSessionUUID()));
                        logger.info("[{}] Registered username: {}", client.session.getRemoteAddress(), registerUsernameModel.username);
                    }
                    else if(models.get(message.id) == MessageModel.class) {
                        MessageModel messageModel = gson.fromJson(client.message(), MessageModel.class);
                        if(!users.containsKey(messageModel.getSessionUUID())) {
                            return;
                        }
                        User user = users.get(messageModel.getSessionUUID());
                        String msg = user.name + ": " + messageModel.message;
                        sessions.forEach((sessionUUID, session) -> {
                            if(!session.session.isOpen())
                                return;
                            models.send(session, new MessageModel(messageModel.getSessionUUID(), msg));
                        });
                        logger.info("[{}] {}", client.session.getRemoteAddress(), msg);
                    }
                } catch(JsonParseException e) {
                    logger.error("[{}] Invalid JSON: {}", client.session.getRemoteAddress(), client.message());
                    e.printStackTrace();
                    return;
                }
            });

            ws.onClose(client -> {
                logger.info("[{}] Disconnected", client.session.getRemoteAddress());
                sessions.remove(client);
            });
        });
    }
}