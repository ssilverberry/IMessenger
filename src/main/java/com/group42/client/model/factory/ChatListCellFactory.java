package com.group42.client.model.factory;

/*
    Class for dynamic views all changes in chat map
 */

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import com.group42.client.model.Chat;

public class ChatListCellFactory implements Callback<ListView<Chat>, ListCell<Chat>> {
    @Override
    public ListCell<Chat> call(ListView<Chat> param) {
        return new ListCell<Chat>(){
            @Override
            protected void updateItem(Chat item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getChatName());
                } else {
                    setText("");
                }
            }
        };
    };


}
