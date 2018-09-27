package com.example.lqs2.courseapp.entity;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/6/21, Thu
 */

public class File {

    private int id;
    private String file_name;
    private String file_size;
    private String file_bring_id;

    private String file_bucket_id;

    private int file_save_days;
    private String file_attach;

    private String file_post_author;
    private String file_destination;
    private String file_post_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getFile_bring_id() {
        return file_bring_id;
    }

    public void setFile_bring_id(String file_bring_id) {
        this.file_bring_id = file_bring_id;
    }

    public String getFile_bucket_id() {
        return file_bucket_id;
    }

    public void setFile_bucket_id(String file_bucket_id) {
        this.file_bucket_id = file_bucket_id;
    }

    public int getFile_save_days() {
        return file_save_days;
    }

    public void setFile_save_days(int file_save_days) {
        this.file_save_days = file_save_days;
    }

    public String getFile_attach() {
        return file_attach;
    }

    public void setFile_attach(String file_attach) {
        this.file_attach = file_attach;
    }

    public String getFile_post_author() {
        return file_post_author;
    }

    public void setFile_post_author(String file_post_author) {
        this.file_post_author = file_post_author;
    }

    public String getFile_destination() {
        return file_destination;
    }

    public void setFile_destination(String file_destination) {
        this.file_destination = file_destination;
    }

    public String getFile_post_date() {
        return file_post_date;
    }

    public void setFile_post_date(String file_post_date) {
        this.file_post_date = file_post_date;
    }
}
