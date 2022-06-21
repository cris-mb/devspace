package iessanclemente.PRO.model;

import com.google.firebase.Timestamp;
import java.util.ArrayList;

public class Chat {
    private String postUid;
    private Timestamp date;
    private ArrayList<Message> messages;
    private ArrayList<String> users;

    public Chat() {
    }

    public Timestamp getDate() {
        return date;
    }

    public String getPostUid() {
        return postUid;
    }

    public void setPostUid(String postUid) {
        this.postUid = postUid;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }
}
