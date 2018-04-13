package com.group42.client.model;

/*
 * Class for constructs message to storage in file
 */

public class ChatMessages {

    private String message;

    /**
     * Constructs object with some message
     * @param message
     */
    public ChatMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
