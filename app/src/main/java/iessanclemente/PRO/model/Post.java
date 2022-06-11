package iessanclemente.PRO.model;

import java.sql.Date;

public class Post {

    private int mUid;
    private String mAuthor;
    private String mMultimediaURL;
    private String mDescription;
    private long mLikes;
    private Date mDate;
    private String mCodeURL;

    public Post(){
    }

    public Post(int uid, String tagId_FK, String multimediaURL, String description, long likes, Date date, String codeURL) {
        this.mUid = uid;
        this.mAuthor = tagId_FK;
        this.mMultimediaURL = multimediaURL;
        this.mDescription = description;
        this.mLikes = likes;
        this.mDate = date;
        this.mCodeURL = codeURL;
    }

    public Post(String currentUserTagId, String multimediaURL, String description, int likes, Date date, String codeURL) {
        this.mAuthor = currentUserTagId;
        this.mMultimediaURL = multimediaURL;
        this.mDescription = description;
        this.mLikes = likes;
        this.mDate = date;
        this.mCodeURL = codeURL;
    }

    public int getUid() {
        return mUid;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getMultimediaURL() {
        return mMultimediaURL;
    }

    public String getDescription() {
        return mDescription;
    }

    public long getLikes() {
        return mLikes;
    }

    public Date getDate() {
        return mDate;
    }

    public String getCodeURL() {
        return mCodeURL;
    }
}
