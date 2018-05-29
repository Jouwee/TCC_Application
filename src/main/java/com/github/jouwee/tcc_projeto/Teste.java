/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import java.io.IOException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/teste")
public class Teste {

    @OnOpen
    public void abrir(Session s) {
        try {
            GeneticAlgorithmController.get().onMessage((msg) -> {
                try {
                    s.getBasicRemote().sendText(msg);
                } catch (IOException ex) {
                    //. . .
                }
            });
            GeneticAlgorithmController.get().sendWelcomeMessage();
        } catch (Throwable t) {
        }
    }

    @OnMessage
    public String recebeMensagem(String mensagem) {
        if (mensagem.equals("{\"message\":\"start\"}")) {
            GeneticAlgorithmController.get().startUp();
            return "{\"message\": \"ok\"}";
        }
        return "{\"message\": \"unknown\"}";
    }

}
