package com.group42.client.model;

import java.time.LocalDate;

public class ChatMessages {

    private LocalDate date;
    private String author;
    private String message;

    public ChatMessages(String author, String message) {
        this.date = date;
        this.author = author;
        this.message = message;
    }

    public ChatMessages(String message) {
        this.message = message;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
