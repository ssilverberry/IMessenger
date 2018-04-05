package com.group42.client.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Chat {

    private StringProperty chatName;
    private StringProperty chatType;
    private Boolean historyFlag;

    public Chat(String chatName, String chatType) {
        this.chatName = new SimpleStringProperty(chatName);
        this.chatType = new SimpleStringProperty(chatType);
        this.historyFlag = false;
    }

    public static void main(String[] args) {

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

    public String getChatType() {
        return chatType.get();
    }

    public StringProperty chatTypeProperty() {
        return chatType;
    }

    public Boolean getHistoryFlag() {
        return historyFlag;
    }

    public void setHistoryFlag(Boolean historyFlag) {
        this.historyFlag = historyFlag;
    }

    @Override
    public String toString() {
        return  chatName.getValue() + " " + chatType.getValue() + "\n";
    }
}
