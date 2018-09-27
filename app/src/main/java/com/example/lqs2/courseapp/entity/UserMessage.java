package com.example.lqs2.courseapp.entity;

public class UserMessage {
    private String id;
    private String msgFrom;
    private String msgTo;
    private String msgContent;
    private int msgType;
    private int msgState;
    private String postTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    public String getMsgTo() {
        return msgTo;
    }

    public void setMsgTo(String msgTo) {
        this.msgTo = msgTo;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getMsgState() {
        return msgState;
    }

    public void setMsgState(int msgState) {
        this.msgState = msgState;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "id='" + id + '\'' +
                ", msgFrom='" + msgFrom + '\'' +
                ", msgTo='" + msgTo + '\'' +
                ", msgContent='" + msgContent + '\'' +
                ", msgType=" + msgType +
                ", msgState=" + msgState +
                ", postTime='" + postTime + '\'' +
                '}';
    }
}
