package me.andreasmelone.chatsocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import me.andreasmelone.chatsocket.models.BasicModel;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModelsRegister {
    public Map<Integer, Class<? extends BasicModel>> models = new LinkedHashMap<>();
    public void register(int id, Class<? extends BasicModel> model) {
        if(models.containsKey(id)) throw new IllegalArgumentException("Model ID already registered");
        for(Map.Entry<Integer, Class<? extends BasicModel>> entry : models.entrySet()) {
            if(entry.getValue() == model) throw new IllegalArgumentException("Model already registered");
        }
        models.put(id, model);
    }
    public Class<? extends BasicModel> get(int id) {
        return models.get(id);
    }

    public void send(WsContext session, BasicModel model) {
        Gson gson = new Gson();
        int id = 0;
        for (Map.Entry<Integer, Class<? extends BasicModel>> entry : models.entrySet()) {
            if (entry.getValue() == model.getClass()) {
                id = entry.getKey();
                break;
            }
        }
        model.id = id;
        session.send(gson.toJson(model));
    }
}
