package iessanclemente.PRO.model;

import com.google.firebase.Timestamp;

public class Message {
    private String author;
    private String message;
    private Timestamp time;

    public Message() {
    }

    public Message(String author, String message, Timestamp time) {
        this.author = author;
        this.message = message;
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
