package com.group42.client.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class creates a chat room object with id, name, and chat type.
 */
public class Chat {

    private IntegerProperty chatId;
    private StringProperty chatName;
    private StringProperty isPrivate;
    private Boolean historyFlag;

    public Chat(Integer chatId, String chatName, String isPrivate) {
        this.chatId = new SimpleIntegerProperty(chatId);
        this.chatName = new SimpleStringProperty(chatName);
        this.isPrivate = new SimpleStringProperty(isPrivate);
        this.historyFlag = false;
    }

    public String getChatName() {
        return chatName.get();
    }

    public StringProperty chatNameProperty() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName.set(chatName);
    }

    public boolean isBanFlag() {
        return historyFlag;
    }

    public void setBanFlag(boolean banFlag) {
        this.historyFlag = banFlag;
    }

    public String getIsPrivate() {
        return isPrivate.get();
    }

    public StringProperty isPrivateProperty() {
        return isPrivate;
    }

    public int getChatId() { return chatId.get(); }

    public IntegerProperty chatIdProperty() {
        return chatId;
    }

    @Override
    public String toString() {
        return  chatName.getValue() + " " + isPrivate.getValue() + "\n";
    }
}
