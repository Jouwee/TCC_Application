package com.github.jouwee.tcc_projeto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect.Type;

/**
 * Json helper for messages
 */
public class JsonHelper {

    /** Singleton instance */
    public static JsonHelper instance;
    /** GSON instance */
    public final Gson gson;

    /**
     * Creates the Json helper
     */
    private JsonHelper() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Chromossome.class, new ChromossomeAdapter())
                .create();
    }

    /**
     * Returns the singleton instance
     *
     * @return JsonHelper
     */
    public static JsonHelper get() {
        if (instance == null) {
            instance = new JsonHelper();
        }
        return instance;
    }

    /**
     * Converts anything to Json
     *
     * @param object
     * @return String
     */
    public String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * Converts a json into anything
     * 
     * @param <T>
     * @param json
     * @param type
     * @return T
     */
    public <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    /**
     * Type adapter for Chromossomes
     */
    public class ChromossomeAdapter extends TypeAdapter<Chromossome> {

        @Override
        public Chromossome read(JsonReader in) throws IOException {
            /*final Book book = new Book();

            in.beginObject();
            while (in.hasNext()) {
                switch (in.nextName()) {
                    case "isbn":
                        book.setIsbn(in.nextString());
                        break;
                    case "title":
                        book.setTitle(in.nextString());
                        break;
                    case "authors":
                        book.setAuthors(in.nextString().split(";"));
                        break;
                }
            }
            in.endObject();

            return book;*/
            return ChromossomeFactory.random();
        }

        @Override
        public void write(JsonWriter out, Chromossome chromossome) throws IOException {
            out.beginArray();
            for (Gene gene : chromossome.getGenes()) {
                if (gene.value() == null) {
                    out.nullValue();
                } else {
                    if (gene instanceof ProcessTypeGene) {
                        out.value(((ProcessTypeGene)gene).value().getCanonicalName());
                    } else {
                        out.value(gene.value().toString());
                    }
                }
            }
            out.endArray();
        }
    }

}
