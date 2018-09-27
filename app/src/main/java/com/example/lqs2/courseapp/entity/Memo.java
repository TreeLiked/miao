package com.example.lqs2.courseapp.entity;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/7/25, Wed
 */
public class Memo {
    private int id;
    private String memo_author;
    private String memo_title;
    private String memo_content;
    private int memo_type;
    private int memo_state;
    private String memo_post_date;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMemo_author() {
        return memo_author;
    }

    public void setMemo_author(String memo_author) {
        this.memo_author = memo_author;
    }

    public String getMemo_title() {
        return memo_title;
    }

    public void setMemo_title(String memo_title) {
        this.memo_title = memo_title;
    }

    public String getMemo_content() {
        return memo_content;
    }

    public void setMemo_content(String memo_content) {
        this.memo_content = memo_content;
    }

    public int getMemo_type() {
        return memo_type;
    }

    public void setMemo_type(int memo_type) {
        this.memo_type = memo_type;
    }

    public int getMemo_state() {
        return memo_state;
    }

    public void setMemo_state(int memo_state) {
        this.memo_state = memo_state;
    }

    public String getMemo_post_date() {
        return memo_post_date;
    }

    public void setMemo_post_date(String memo_post_date) {
        this.memo_post_date = memo_post_date;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "id=" + id +
                ", memo_author='" + memo_author + '\'' +
                ", memo_title='" + memo_title + '\'' +
                ", memo_content='" + memo_content + '\'' +
                ", memo_type=" + memo_type +
                ", memo_state=" + memo_state +
                ", memo_post_date='" + memo_post_date + '\'' +
                '}';
    }
}
