package io.github.gatimus.hooftuner.pvl;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

public class URLDeserializer implements JsonDeserializer<URL> {
    @Override
    public URL deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        URL url = null;
        String stringUrl = json.getAsString();
        if(!stringUrl.isEmpty()){
            try {
                url = new URL(json.getAsString());
            } catch (MalformedURLException e) {
                Log.e(getClass().getSimpleName(), e.toString());
            }
        }
        return url;
    }
}
