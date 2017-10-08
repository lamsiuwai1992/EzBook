package com.ezbook.lamsiuwai.ezbook;
/**
 * Created by lamsiuwai on 28/9/2017.
 */

public class ShowChatListObject {
    private String lastMessage;

    public ShowChatListObject(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    ShowChatListObject(){

    }
}
