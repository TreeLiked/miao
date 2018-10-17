package com.example.lqs2.courseapp.entity;


import com.google.gson.annotations.SerializedName;

/**
 * file model
 *
 * @author lqs2
 */
public class File {

    private int id;

    @SerializedName("file_name")
    private String fileName;

    @SerializedName("file_size")
    private String fileSize;

    @SerializedName("file_bring_id")
    private String fileBringId;

    @SerializedName("file_bucket_id")
    private String fileBucketId;

    @SerializedName("file_save_days")
    private int fileSaveDays;

    @SerializedName("file_attach")
    private String fileAttach;

    @SerializedName("file_post_author")
    private String filePostAuthor;

    @SerializedName("file_destination")
    private String fileDestination;

    @SerializedName("file_post_date")
    private String filePostDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileBringId() {
        return fileBringId;
    }

    public void setFileBringId(String fileBringId) {
        this.fileBringId = fileBringId;
    }

    public String getFileBucketId() {
        return fileBucketId;
    }

    public void setFileBucketId(String fileBucketId) {
        this.fileBucketId = fileBucketId;
    }

    public int getFileSaveDays() {
        return fileSaveDays;
    }

    public void setFileSaveDays(int fileSaveDays) {
        this.fileSaveDays = fileSaveDays;
    }

    public String getFileAttach() {
        return fileAttach;
    }

    public void setFileAttach(String fileAttach) {
        this.fileAttach = fileAttach;
    }

    public String getFilePostAuthor() {
        return filePostAuthor;
    }

    public void setFilePostAuthor(String filePostAuthor) {
        this.filePostAuthor = filePostAuthor;
    }

    public String getFileDestination() {
        return fileDestination;
    }

    public void setFileDestination(String fileDestination) {
        this.fileDestination = fileDestination;
    }

    public String getFilePostDate() {
        return filePostDate;
    }

    public void setFilePostDate(String filePostDate) {
        this.filePostDate = filePostDate;
    }
}
