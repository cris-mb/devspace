package iessanclemente.PRO.onboarding;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Objects;

import iessanclemente.PRO.DatabaseOperations;
import iessanclemente.PRO.PostRecyclerView;
import iessanclemente.PRO.R;
import iessanclemente.PRO.model.User;

public class EnterUtilities {

    private static final String TAG = EnterUtilities.class.getSimpleName();
    private final Context context;

    private final StorageReference stRef;
    private final DatabaseReference usersRef, postsRef;
    private DatabaseOperations op;
    private final FirebaseAuth fAuth;

    private final String currUserUid;
    private final User currUser;

    public EnterUtilities(Context ctx){
        this.context = ctx;
        stRef = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");

        fAuth = FirebaseAuth.getInstance();
        currUserUid = fAuth.getCurrentUser() != null?fAuth.getCurrentUser().getUid():null;
        currUser = currUserUid != null?checkUserExistence(currUserUid):null;
        // TODO : Retrieve user data
    }

    public void start(){
        op = new DatabaseOperations(context);
        op.sqlLiteDB = op.getWritableDatabase();
    }

    public void stop(){
        op.close();
    }

    public boolean checkExistingSession() {
        FirebaseUser check = FirebaseAuth.getInstance().getCurrentUser();
        return check != null;
    }

    public User checkUserExistence(@NonNull String email) {
        final User[] us = {new User()};
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()
                        && snapshot.hasChild("email")
                        && Objects.equals(snapshot.child("email").getValue(), email)){
                    us[0].setAbout(snapshot.child("about").getValue()+"");
                    us[0].setEmail(email);
                    us[0].setPassword(snapshot.child("password").getValue()+"");
                    us[0].setProfileImagePath(snapshot.child("profileImage").getValue()+"");
                    us[0].setUserTag(snapshot.child("tag").getValue()+"");
                    us[0].setUsername(snapshot.child("username").getValue()+"");
                }else
                    us[0] = null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return us[0];
    }

    public boolean checkPassword(String pass) {
        return pass.equals(currUser.getPassword());
    }

    public String searchForProfileImageRef() {
        User us = checkUserExistence(currUserUid);
        usersRef.child(currUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    us.setProfileImagePath(snapshot.child("profileImage").getValue()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return us.getProfileImagePath();
    }

    public void loginUser(String email, String pass, boolean maintainSession){
        boolean loginResult = false;
        fAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(context, "Successful login", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(context, "Error while logging in", Toast.LENGTH_SHORT).show();
        });
        if(maintainSession){
            // TODO : save current session
        }
    }

    public boolean addUser(String newUserTag, String email, String password, String profileImageRef){
        User us = checkUserExistence(email);
        if (us == null) {
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                    Toast.makeText(context, "Account saved in auth", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            });
            saveNewUserToDatabase(newUserTag, email, password, profileImageRef);
            return true;
        }

        return false;
    }

    private void saveNewUserToDatabase(String newUserTag, String email, String password, String profileImageRef) {
        HashMap<String, Object> newUserMap = new HashMap<>();
        newUserMap.put("about","");
        newUserMap.put("email", email);
        newUserMap.put("password", password);
        newUserMap.put("profileImage", profileImageRef);
        newUserMap.put("tag", newUserTag);
        newUserMap.put("username", "");

        usersRef.updateChildren(newUserMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                intentPostRecyclerActivity();
                Toast.makeText(context, "Account stored in database", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void uploadProfileImageOnStorage(Uri data){
        String fileName = data.getLastPathSegment()+generateTimeStamp()+".jpg";
        StorageReference filePath = stRef.child("post_images").child(fileName);
        filePath.putFile(data).addOnCompleteListener(task -> {
//                downloadUrl = task.getResult().getUploadSessionUri().getPath(); Mirar tuto de nuevo
            if(task.isSuccessful()) {
                currUser.setProfileImagePath(filePath.getPath());
                Log.d("PATH","Profile Image Storage Reference : "+filePath.getPath());
                uploadUserOnDatabase();
                Toast.makeText(context, "Your profile image " + context.getResources().getString(R.string.successfulUpload), Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadUserOnDatabase() {
        usersRef.child(currUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("about", currUser.getAbout());
                    userMap.put("email", currUser.getEmail());
                    userMap.put("password", currUser.getPassword());
                    userMap.put("profileImage", currUser.getProfileImagePath());
                    userMap.put("tag", currUser.getUserTag());
                    userMap.put("username", currUser.getUsername());

                    usersRef.child(generateTimeStamp()).updateChildren(userMap).addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                            Toast.makeText(context, "Profile image successfully uploaded", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Error, profile image wasn't uploaded", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String generateTimeStamp(){
        return new SimpleDateFormat("ddMMyy_mmss").format(new java.util.Date());
    }

    public void intentPostRecyclerActivity(){
        Intent recycler = new Intent(context.getApplicationContext(), PostRecyclerView.class);
        context.startActivity(recycler);
    }

    public String getCurrentUserUid() {
        return currUserUid;
    }
}
