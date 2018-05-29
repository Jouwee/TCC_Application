package com.github.jouwee.tcc_projeto;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import visnode.application.NodeNetwork;
import visnode.application.parser.NodeNetworkParser;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeneticAlgorithmModel {

    private final List<MessageProcessor> messageProcessors;
    private int currentGeneration;

    public GeneticAlgorithmModel() {
        this.messageProcessors = new ArrayList<>();
    }

    public void initialize() {
        setCurrentGeneration(0);
    }

    public synchronized void onMessage(MessageProcessor listener) {
        this.messageProcessors.add(listener);
    }

    public synchronized void sendMessage(String message) {
        for (MessageProcessor listener : messageProcessors) {
            listener.process(message);
        }
    }

    public void sendModelUpdate(String name, Object value) {
        if (!((value instanceof Number) || (value instanceof Boolean))) {
            value = "\"" + value.toString() + "\"";
        }
        sendMessage("{\"message\":\"updateModel\", \"payload\":{\""+name+"\": "+value+"}}");
    }

    public int getCurrentGeneration() {
        return this.currentGeneration;
    }

    public void setCurrentGeneration(int currentGeneration) {
        this.currentGeneration = currentGeneration;
        sendModelUpdate("currentGeneration", currentGeneration);
    }

    public void incrementCurrentGeneration() {
        setCurrentGeneration(getCurrentGeneration() + 1);
    }

}