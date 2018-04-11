package com.group42.server.model;


import java.time.LocalDate;


public class ChatMessages {

    private LocalDate date;
    private String author;
    private String message;

    public ChatMessages(LocalDate date, String author, String message) {
        this.date = date;
        this.author = author;
        this.message = message;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return author + " > " + message + "\n";
    }
}
