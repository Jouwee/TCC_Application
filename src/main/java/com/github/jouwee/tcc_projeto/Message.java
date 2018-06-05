package com.github.jouwee.tcc_projeto;

/**
 * Web Socket message
 */
public class Message {
    
    /** Message ID */
    private final String message;
    /** Payload */
    private final Object payload;

    /**
     * Creates a new message
     * 
     * @param message
     * @param payload 
     */
    public Message(String message, Object payload) {
        this.message = message;
        this.payload = payload;
    }

    /**
     * Returns the message ID
     * 
     * @return String
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the payload
     * 
     * @return Object
     */
    public Object getPayload() {
        return payload;
    }
    
}
