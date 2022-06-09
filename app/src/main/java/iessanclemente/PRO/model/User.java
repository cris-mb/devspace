package iessanclemente.PRO.model;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class User {

    private String uid;
    private String tag;
    private String username;
    private String profileImage;
    private String email;
    private String about;

    public User(){
    }

    public void setUserData(@NonNull HashMap<String, Object> userMap) {
        this.uid = userMap.get("uid")+"";
        this.tag = userMap.get("tag")+"";
        this.username = userMap.get("username")+"";
        this.email = userMap.get("email")+"";
        this.profileImage = userMap.get("profileImage")+"";
        this.about = userMap.get("about")+"";
    }

    public String getTag() {
        return tag;
    }


    public String getUsername() {
        return username;
    }


    public String getProfileImage() {
        return profileImage;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getAbout() {
        return about;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> userMap =  new HashMap<>();
        userMap.put("uid", this.getUid());
        userMap.put("tagId", this.getTag());
        userMap.put("username", this.getUsername());
        userMap.put("email", this.getEmail());
        userMap.put("profileImage", this.getProfileImage());
        userMap.put("about", this.getAbout());
        
        return userMap;
    }

}
