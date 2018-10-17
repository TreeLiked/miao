package com.example.lqs2.courseapp.entity;

/**
 * chat message model
 *
 * @author lqs2
 */
public class ChatMsg {

    public static final int TYPE_RECEIVED = 0;

    public static final int TYPE_SENT = 1;


    private String fromId;
    private String toId;


    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String content;

    private int type;

    public ChatMsg(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

}
