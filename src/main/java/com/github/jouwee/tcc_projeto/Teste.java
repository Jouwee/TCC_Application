/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import visnode.application.NodeNetwork;
import visnode.application.parser.NodeNetworkParser;

@ServerEndpoint("/teste")
public class Teste {

    @OnOpen
    public void abrir(Session s) {
        try {
            GeneticAlgorithmController.get().onMessage((msg) -> {
                try {
                    s.getBasicRemote().sendText(msg);
                } catch (IOException | IllegalStateException ex) {
                    //. . .
                }
            });
            GeneticAlgorithmController.get().sendWelcomeMessage();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @OnMessage
    public String recebeMensagem(String mensagem) {
        Message message = JsonHelper.get().fromJson(mensagem, Message.class);
        if (message.getMessage().equals("start")) {
            Executors.newSingleThreadExecutor().submit(() -> {
                GeneticAlgorithmController.get().startUp();
            });
            return "{\"message\": \"ok\"}";
        }
        if (message.getMessage().equals("currentStatus")) {
            Executors.newSingleThreadExecutor().submit(() -> {
                GeneticAlgorithmController.get().sendWelcomeMessage();
            });
            return "{\"message\": \"ok\"}";
        }
        if (message.getMessage().equals("openChromossome")) {
            open(ChromossomeFactory.fromMessage((ArrayList) message.getPayload()));
            return "{\"message\": \"ok\"}";
        }
        return "{\"message\": \"unknown command: " + mensagem + "\"}";
    }

    /**
     * Open a project from VisNode
     * 
     * @param chromossome 
     */
    private void open(Chromossome chromossome) {
        try {
            NodeNetwork network = new ChromossomeNetworkConverter(true).convert(chromossome);
            NodeNetworkParser parser = new NodeNetworkParser();
            String file = "c:\\users\\pichau\\desktop\\visnode.vnp";
            try (PrintWriter writer = new PrintWriter(new File(file), "UTF-8")) {
                writer.print(parser.toJson(network));
            }
            Runtime rt = Runtime.getRuntime();
            rt.exec("C:\\Users\\Pichau\\Desktop\\VISNode-1.2.2\\visnode-windows.bat " + file, null, new File("C:\\Users\\Pichau\\Desktop\\VISNode-1.2.2\\"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
