package com.github.shell88.bddvideoannotator.annotationfile.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Used to escape special characters in GSON.
 * 
 * @author Hell
 *
 */

public class GsonEscapeStringSerializer implements JsonSerializer<String> {

  @Override
  public JsonElement serialize(String src, Type typeOfSrc,
      JsonSerializationContext context) {
    return new JsonPrimitive(escapeJs(src));
  }

  /**
   * 
   * @param string
   *          - Json String for GSON.
   * @return escaped String for JavaScript.
   */
  public String escapeJs(String string) {
    String escapes[][] = { { "\\", "\\\\" }, { "\"", "\\\"" }, { "\n", "\\n" },
        { "\r", "\\r" }, { "\b", "\\b" }, { "\f", "\\f" }, { "\t", "\\t" } };
    for (String[] esc : escapes) {
      string = string.replace(esc[0], esc[1]);
    }
    return string;
  }

}
