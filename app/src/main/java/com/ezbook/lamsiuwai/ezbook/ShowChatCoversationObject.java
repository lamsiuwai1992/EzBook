package com.ezbook.lamsiuwai.ezbook;

/**
 * Created by lamsiuwai on 28/9/2017.
 */

public class ShowChatCoversationObject {
    private String message;
    private String sender;

    ShowChatCoversationObject(){

    }
    public ShowChatCoversationObject(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
