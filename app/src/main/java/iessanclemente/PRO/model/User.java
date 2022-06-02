package iessanclemente.PRO.model;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

public class User {

    private String tagId;
    private String username;
    private String profileImagePath;
    private Uri profileImageUri;
    private String email;
    private String password;
    private String about;

    public User(){
    }

    public User(String tagId, String email, String password){
        this.tagId = tagId;
        this.email = email;
        this.password = password;
    }

    public User(String tagId, String username, String profileImagePath, String email, String password, String about) {
        this.tagId = tagId;
        this.username = username;
        this.profileImagePath = profileImagePath;
        this.email = email;
        this.password = password;
        this.about = about;
    }

    public String getUserTag() {
        return tagId;
    }

    public void setUserTag(String tagId) {
        this.tagId = tagId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }


    public Uri getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(Uri profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

}
