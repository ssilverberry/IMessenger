package com.group42.client.controllers;

/*
 * Class for manage history of private chats.
 */

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.group42.client.model.Chat;
import com.group42.client.model.ChatMessages;
import com.group42.client.model.Model;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.util.*;

public class ChatIO {

    /**
     * Logging for exception trace.
     */
    private final Logger logger = LogManager.getLogger(ChatIO.class);

    /**
     * Instance of class.
     */
    private static final ChatIO instance = new ChatIO();

    /**
     * Instances of Gson class for read/write objects.
     */
    private Gson gsonForChats;
    private Gson gsonForHistory;

    /**
     * Default constructor initialize <tt>gsonForHistory</tt>
     */
    private ChatIO() {
        gsonForHistory = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Returns instance of this class.
     * @return
     */
    public static ChatIO getInstance() {
        return instance;
    }

    /**
     * Init instance of gson builder for read chats in simple form.
     */
    private void initGsonForChatList(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Chat.class, new ChatConverter());
        gsonForChats = builder.setPrettyPrinting().create();
    }

    /**
     * setup file, which contains list of chats for current user.
     * @param file
     */
    private void setupChatListFile(File file){
        try {
            file.createNewFile();
            Set<Chat> temp = new HashSet<>();
            temp.add(new Chat("General chat", "general"));
            writeChatsToFile(temp);
        } catch (IOException e) {
            logger.error("File wasn't create!");
        }
    }

    /**
     * create file for history of private chat with <tt>toUser</tt>
     * @param toUser
     */
    public void createFileForPrivateHistory(String toUser){
        File file = new File("./Chats/ChatLogs/" + toUser + ".json");
        try {
            file.createNewFile();
        } catch (IOException e) {
            logger.error("File wasn't create!");
        }
    }

    /**
     * writes chat list to file.
     * @param chats
     */
    public void writeChatsToFile(Set<Chat> chats) {
        String currUser = Model.getInstance().getUser().getLogin();
        File file = new File("./Chats/" + currUser + " chat list.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            Chat[] tempChats = new Chat[chats.size()];
            int i = 0;
            for (Chat chat: chats) {
                tempChats[i] = chat;
                i++;
            }
            String jsonData = gsonForChats.toJson(tempChats);
            writer.write(jsonData);
            writer.flush();
        } catch (IOException ex) {
            logger.error("IN CHATS WRITE TO FILE: ", ex);
        }
    }

    /**
     * reads chat list from file.
     * @return
     */
    public Set<Chat> readChatsFromFile(){
        initGsonForChatList();
        Set<Chat> chatSet = new HashSet<>();
        Chat[] chats;
        String currUser = Model.getInstance().getUser().getLogin();
        File file = new File("./Chats/" + currUser + " chat list.json");
        if (!file.exists()) {
            setupChatListFile(file);
        }
        try (JsonReader reader = new JsonReader(new FileReader(file))){
            chats = gsonForChats.fromJson(reader, Chat[].class);
            chatSet.addAll(Arrays.asList(chats));
        } catch (IOException e) {
            logger.error("exception to read chats from file!");
        } return chatSet;
    }


    /**
     * reads private history from file.
     * @param chatName
     * @return
     */
    public List<Text> readChatHistoryFromFile(String chatName) {
        List<Text> chatHistory = new ArrayList<>();
        File file = new File("./Chats/ChatLogs/" + chatName + ".json");
        ChatMessages[] chatMessages;
        try (JsonReader reader = new JsonReader(new FileReader(file))){
            chatMessages = gsonForHistory.fromJson(reader, ChatMessages[].class);
            if (chatMessages != null) {
                for (ChatMessages chatMessage : chatMessages) {
                    Text text = new Text(chatMessage.toString());
                    text.setFont(Font.font(14));
                    if (text.getText().contains(Model.getInstance().getUser().getLogin())) {
                        text.setFill(Color.valueOf("#4357a3"));
                    }
                    chatHistory.add(text);
                }
            }
        } catch (IOException e) {
            logger.error("exception to read chats from file!", e);
        } return chatHistory;
    }

    /**
     * write private history to file.
     * @param privateName
     * @param history
     */
    public void writePrivateHistoryToFile(String privateName, ObservableList<Text> history) {
        initGsonForChatList();
        File file = new File ("./Chats/ChatLogs/" + privateName + ".json");
        ChatMessages[] messages = new ChatMessages[history.size()];
        int i = 0;
        for (Text text: history) {
            messages[i] = new ChatMessages(text.getText());
            i++;
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            String line = gsonForHistory.toJson(messages);
            fileWriter.write(String.valueOf(line));
            fileWriter.flush();
        } catch (IOException e) {
            logger.error (privateName + ".txt file could not be found or written");
        }
    }
}
