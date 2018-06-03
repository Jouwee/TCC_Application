/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jouwee.tcc_projeto;

/**
 *
 * @author Pichau
 */
public class Message {
    
    private String message;
    private Object payload;

    public Message(String message, Object payload) {
        this.message = message;
        this.payload = payload;
    }
    
}
