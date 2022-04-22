package iessanclemente.PRO.model;

import java.sql.Date;

public class Post {

    private int postId;
    private String tagId_FK;
    private String multimediaPath;
    private String description;
    private long likes;
    private Date date;
    private String codeURL;

    public Post(int postId, String tagId_FK, String multimediaPath, String description, long likes, Date date, String codeURL) {
        this.postId = postId;
        this.tagId_FK = tagId_FK;
        this.multimediaPath = multimediaPath;
        this.description = description;
        this.likes = likes;
        this.date = date;
        this.codeURL = codeURL;
    }

    public Post(String currentUserTagId, String multimediaPath, String description, int likes, Date date, String codeURL) {
        this.tagId_FK = currentUserTagId;
        this.multimediaPath = multimediaPath;
        this.description = description;
        this.likes = likes;
        this.date = date;
        this.codeURL = codeURL;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getAuthor() {
        return tagId_FK;
    }

    public void setAuthor(String tagId_FK) {
        this.tagId_FK = tagId_FK;
    }

    public String getMultimediaPath() {
        return multimediaPath;
    }

    public void setMultimediaPath(String multimediaPath) {
        this.multimediaPath = multimediaPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCodeURL() {
        return codeURL;
    }

    public void setCodeURL(String codeURL) {
        this.codeURL = codeURL;
    }
}
