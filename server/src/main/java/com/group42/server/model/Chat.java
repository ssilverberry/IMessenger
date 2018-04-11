package com.group42.server.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Chat {

    private IntegerProperty chatId;
    private StringProperty chatName;
    private StringProperty chatType;
    private Boolean historyFlag;

    public Chat(Integer chatId, String chatName, String chatType) {
        this.chatId = new SimpleIntegerProperty(chatId);
        this.chatName = new SimpleStringProperty(chatName);
        this.chatType = new SimpleStringProperty(chatType);
        this.historyFlag = false;
    }
    public Chat() {}
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

    public String getChatType() {
        return chatType.get();
    }

    public StringProperty chatTypeProperty() {
        return chatType;
    }

    public int getChatId() { return chatId.get(); }

    public IntegerProperty chatIdProperty() {
        return chatId;
    }

    @Override
    public String toString() {
        return  chatName.getValue() + " " + chatType.getValue() + "\n";
    }
}
