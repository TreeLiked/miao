package com.example.lqs2.courseapp.entity;


/**
 * darkme account
 *
 * @author lqs2
 */
public class User {

    private int id;
    private String username;
    private String password;
    private String email;
    private boolean isMan;
    private String createTime;
    private String profilePicStr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isMan() {
        return isMan;
    }

    public void setMan(boolean man) {
        isMan = man;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getProfilePicStr() {
        return profilePicStr;
    }

    public void setProfilePicStr(String profilePicStr) {
        this.profilePicStr = profilePicStr;
    }
}
