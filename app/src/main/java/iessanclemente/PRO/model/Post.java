package iessanclemente.PRO.model;

import com.google.firebase.Timestamp;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.HashMap;

public class Post {

    @PropertyName("uid")
    public String uid;
    @PropertyName("author")
    public String author;
    @PropertyName("multimedia")
    public String multimedia;
    @PropertyName("description")
    public String description;
    @PropertyName("date")
    public Timestamp date;
    @PropertyName("url")
    public String url;

    public Post(){
    }

    public Post(String uid, String author, String multimedia, String description, Timestamp date, String url) {
        this.uid = uid;
        this.author = author;
        this.multimedia = multimedia;
        this.description = description;
        this.date = date;
        this.url = url;
    }

    public String getUid() {
        return uid;
    }

    public String getAuthor() {
        return author;
    }

    public String getMultimedia() {
        return multimedia;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setMultimedia(String multimedia) {
        this.multimedia = multimedia;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
