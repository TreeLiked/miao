package com.example.lqs2.courseapp.entity;

import java.util.List;

public class Book {

    private String bookNameWithNo;
    private String author;
    private String publisher;
    private String blInfo;
    private String detailUrl;

    private String detailInfo;
    private String blDetail;

    private List<BookLoc> locs;

    public String getBookNameWithNo() {
        return bookNameWithNo;
    }

    public void setBookNameWithNo(String bookNameWithNo) {
        this.bookNameWithNo = bookNameWithNo;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getBlInfo() {
        return blInfo;
    }

    public void setBlInfo(String blInfo) {
        this.blInfo = blInfo;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    public String getBlDetail() {
        return blDetail;
    }

    public void setBlDetail(String blDetail) {
        this.blDetail = blDetail;
    }

    public List<BookLoc> getLocs() {
        return locs;
    }

    public void setLocs(List<BookLoc> locs) {
        this.locs = locs;
    }


    @Override
    public String toString() {
        return "Book{" +
                "bookNameWithNo='" + bookNameWithNo + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", blInfo='" + blInfo + '\'' +
                ", detailUrl='" + detailUrl + '\'' +
                ", detailInfo='" + detailInfo + '\'' +
                ", blDetail='" + blDetail + '\'' +
                ", locs=" + locs +
                '}';
    }
}
