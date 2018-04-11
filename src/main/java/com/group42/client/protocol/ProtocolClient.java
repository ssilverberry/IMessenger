package com.group42.client.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.group42.client.model.converter.ChatConverter;
import com.group42.client.model.Chat;
import com.group42.client.protocol.encryption.StringCrypter;

public class ProtocolClient {

    private static final ProtocolClient INSTANCE = new ProtocolClient();
    private StringCrypter crypter = new StringCrypter(new byte[]{1,4,5,6,8,9,7,8});
    private Gson gson;

    public static ProtocolClient getInstance() {
        return INSTANCE;
    }

    public ProtocolClient() {
        gson  = new Gson();
    }

    public String transform (Object outputClientMessage) {
        Gson out = new GsonBuilder().create();
        return crypter.encrypt(out.toJson(outputClientMessage));
    }

    public IncomingServerMessage transformOut(String response) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Chat.class, new ChatConverter());
        Gson gsonForChats = builder.create();

        JsonElement jsonElement =  gsonForChats.fromJson(response, JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement jsonActionId = jsonObject.get("actionId");
        Integer actionId = gson.fromJson(jsonActionId, Integer.class);
        if (actionId == 43) {
            JsonElement jsonChat = jsonObject.get("localUsrChatList");
            Chat[] chatList = gsonForChats.fromJson(jsonChat, Chat[].class);
            return new IncomingServerMessage(43, chatList);
        } else return gson.fromJson(response, IncomingServerMessage.class);
    }

}