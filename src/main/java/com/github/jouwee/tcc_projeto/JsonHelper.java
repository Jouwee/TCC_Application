package com.github.jouwee.tcc_projeto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            in.beginArray();
            List<Gene> genes = new ArrayList<>();
            while(in.hasNext()) {
                JsonToken token = in.peek();
                if (token.equals(JsonToken.NULL)) {
                    in.nextNull();
                    genes.add(new ProcessTypeGene(null));
                    continue;
                }
                String val = in.nextString();
                try {
                    genes.add(new NumericGene(Double.valueOf(val)));
                } catch (Exception e) {
                    try {
                        genes.add(new ProcessTypeGene(Class.forName(val)));
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
                
            }
            in.endArray();
            return new Chromossome(genes.toArray(new Gene[0]));
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
