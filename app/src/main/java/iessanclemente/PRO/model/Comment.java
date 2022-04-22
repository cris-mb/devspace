package iessanclemente.PRO.model;

import java.util.Date;

public class Comment {

    private int commentId;
    private String message;
    private Date date;

    public Comment(String message, Date date) {
        this.message = message;
        this.date = date;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
