package com.example.lqs2.courseapp.entity;


import com.google.gson.annotations.SerializedName;

/**
 * memo model
 *
 * @author lqs2
 */
public class Memo {
    private int id;

    @SerializedName("memo_author")
    private String memoAuthor;

    @SerializedName("memo_title")
    private String memoTitle;

    @SerializedName("memo_content")
    private String memoContent;

    @SerializedName("memo_type")
    private int memoType;

    @SerializedName("memo_state")
    private int memoState;

    @SerializedName("memo_post_date")
    private String memoPostDate;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMemoAuthor() {
        return memoAuthor;
    }

    public void setMemoAuthor(String memoAuthor) {
        this.memoAuthor = memoAuthor;
    }

    public String getMemoTitle() {
        return memoTitle;
    }

    public void setMemoTitle(String memoTitle) {
        this.memoTitle = memoTitle;
    }

    public String getMemoContent() {
        return memoContent;
    }

    public void setMemoContent(String memoContent) {
        this.memoContent = memoContent;
    }

    public int getMemoType() {
        return memoType;
    }

    public void setMemoType(int memoType) {
        this.memoType = memoType;
    }

    public int getMemoState() {
        return memoState;
    }

    public void setMemoState(int memoState) {
        this.memoState = memoState;
    }

    public String getMemoPostDate() {
        return memoPostDate;
    }

    public void setMemoPostDate(String memoPostDate) {
        this.memoPostDate = memoPostDate;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "id=" + id +
                ", memoAuthor='" + memoAuthor + '\'' +
                ", memoTitle='" + memoTitle + '\'' +
                ", memoContent='" + memoContent + '\'' +
                ", memoType=" + memoType +
                ", memoState=" + memoState +
                ", memoPostDate='" + memoPostDate + '\'' +
                '}';
    }
}
