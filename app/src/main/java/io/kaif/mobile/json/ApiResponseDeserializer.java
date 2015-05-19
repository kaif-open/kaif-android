package io.kaif.mobile.json;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class ApiResponseDeserializer implements JsonDeserializer<Object> {

  private Gson gson;

  public ApiResponseDeserializer(Gson gson) {
    this.gson = gson;
  }

  @Override
  public Object deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
      throws JsonParseException {
    JsonElement content = je.getAsJsonObject().get("data");
    return gson.fromJson(content, type);
  }
}