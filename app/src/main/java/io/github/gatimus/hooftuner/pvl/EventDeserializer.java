package io.github.gatimus.hooftuner.pvl;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class EventDeserializer implements JsonDeserializer<Event> {
    @Override
    public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Event event = null;
        if(json.isJsonObject()){
            event = new Gson().fromJson(json, Event.class);
        }
        if(json.isJsonArray()){
            if(json.getAsJsonArray().size()>0){
                event = new Gson().fromJson(json.getAsJsonArray().get(0), Event.class);
            }
        }
        return event;
    }
}
