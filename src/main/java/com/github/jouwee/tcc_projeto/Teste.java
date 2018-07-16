/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

import java.util.concurrent.Executors;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/teste")
public class Teste {

    @OnOpen
    public void abrir(Session s) {
        GeneticAlgorithmController.get().onMessage(new SafeMessageProcessor(s, (msg) -> {
            s.getBasicRemote().sendText(msg);
        }));
        GeneticAlgorithmController.get().sendWelcomeMessage();
    }

    @OnMessage
    public String recebeMensagem(String mensagem) {
        Message message = JsonHelper.get().fromJson(mensagem, Message.class);
        if (message.getMessage().equals("runSingle")) {
            Executors.newSingleThreadExecutor().submit(() -> {
                GeneticAlgorithmController.get().runGeneration();
            });
            return ok();
        }
        if (message.getMessage().equals("runForever")) {
            Executors.newSingleThreadExecutor().submit(() -> {
                GeneticAlgorithmController.get().keepRunning();
            });
            return ok();
        }
        if (message.getMessage().equals("interrupt")) {
            Executors.newSingleThreadExecutor().submit(() -> {
                GeneticAlgorithmController.get().interrupt();
            });
            return ok();
        }
        if (message.getMessage().equals("currentStatus")) {
            Executors.newSingleThreadExecutor().submit(() -> {
                GeneticAlgorithmController.get().sendWelcomeMessage();
            });
            return ok();
        }
        return "{\"message\": \"unknown command: " + mensagem + "\"}";
    }
    
    /**
     * Retorna a mensagem de Ok
     * 
     * @return ok
     */
    private String ok() {
        return "{\"message\": \"ok\"}";
    }
    
    /**
     * MessageProcessor that allows exceptions
     */
    public interface UnsafeMessageProcessor {

        /**
         * Process the message
         * 
         * @param message
         * @throws Exception 
         */
        public void process(String message) throws Exception;
    
    }
    
    /**
     * Message processor that receives and unsafe message processor and treat it safely
     */
    public class SafeMessageProcessor implements MessageProcessor {
        
        /** Session to close if error */
        private final Session session;
        /** Processor */
        private final UnsafeMessageProcessor processor;

        public SafeMessageProcessor(Session basicEndpoint, UnsafeMessageProcessor processor) {
            this.processor = processor;
            this.session = basicEndpoint;
        }

        @Override
        public void process(String message) {
            try {
                this.processor.process(message);
            } catch (IllegalStateException ex) {
                GeneticAlgorithmController.get().removeOnMessage(this);
                try {
                    session.close();
                } catch (Exception e) {};
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }

}
