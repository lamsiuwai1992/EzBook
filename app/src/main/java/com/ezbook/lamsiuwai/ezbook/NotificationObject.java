package com.ezbook.lamsiuwai.ezbook;

/**
 * Created by lamsiuwai on 25/10/2017.
 */

public class NotificationObject {
    private String body;
    private String from;
    private String to;

    public NotificationObject(String body, String from, String to) {
        this.body = body;
        this.from = from;
        this.to = to;
    }

    public String getBody() {
        return body;
    }


    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

}
