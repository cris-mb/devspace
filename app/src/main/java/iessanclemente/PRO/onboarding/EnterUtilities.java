package iessanclemente.PRO.onboarding;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;
import iessanclemente.PRO.PostRecyclerView;

public class EnterUtilities {

    private static final String TAG = EnterUtilities.class.getSimpleName();
    private final Context context;

    private final StorageReference stRef;
    private final DatabaseReference usersRef, postsRef;
    private final FirebaseAuth fAuth;
    private final FirebaseFirestore ffStore;
    private final FirebaseUser fUser;

    public EnterUtilities(Context ctx){
        this.context = ctx;
        stRef = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");

        fAuth = FirebaseAuth.getInstance();
        ffStore = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public boolean checkUserAuthentication() {
        FirebaseUser check = FirebaseAuth.getInstance().getCurrentUser();
        return check != null;
    }

    public void registerNewUser(String uid, HashMap<String, Object> userData){
        ffStore.collection("users")
            .document(uid)
            .set(userData).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Log.d(TAG, "registerNewUser: success");
                }else{
                    Log.d(TAG, "registerNewUser: failed -> "+task.getException());
                }
            });
    }

    public void uploadProfileImage(byte[] data) {
        StorageMetadata stMeta = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        String newProfileImagePath = "profile_images/"+generateTimeStamp()+".jpeg";

        stRef.child(newProfileImagePath).putBytes(data, stMeta).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                String imageUrl = task.getResult().getStorage().getDownloadUrl()+"";
                modifyCurrentUserProfileImage(imageUrl);
            }else {
                Log.d(TAG, "uploadProfileImage: failed -> " + task.getException());
            }
        });
    }

    public void modifyCurrentUserProfileImage(String profileImagePath) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        ffStore.collection("users")
                .document(fUser.getUid())
                .update("profileImage", profileImagePath)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "modifyCurrentUserProfileImage: success");
                    }else {
                        Log.d(TAG, "modifyCurrentUserProfileImage: failed -> " + task.getException());
                    }
                });
    }

    public void firebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        fAuth.signInWithCredential(credential)
            .addOnCompleteListener(task -> {
               if(task.isSuccessful()){
                   intentLoginActivity();
                   checkUserInFirestore(task.getResult().getUser().getUid(), task.getResult().getUser().getEmail());
                   Log.d(TAG, "signInWithCredential: result -> "+task.getResult().getUser().getUid());
               }else {
                   intentRegisterActivity();
                   Log.e(TAG, "signInWithCredential: failed -> " + task.getException());
               }
            });
    }

    public void checkUserInFirestore(String uid, String email) {

        ffStore.collection("users")
                .document(uid)
                .get().addOnCompleteListener(task -> {
                   if(task.isSuccessful() && !task.getResult().exists()){
                       HashMap<String, Object> userData = new HashMap<>();
                       userData.put("about", "Hey there! I'm a new user of DevSpace");
                       userData.put("email", email);
                       userData.put("profileImage", "gs://devspace-b93f2.appspot.com/profile_images/anonymous.png");
                       userData.put("tag", "devUser"+generateRandomUser());
                       userData.put("username", "Anonymous");

                       registerNewUser(uid, userData);
                   }
                });
    }

    public void updateUsersProfile(String tag, String username, String about) {
        ffStore.collection("users")
                .document(fUser.getUid())
                .update("tag", tag, "username", username, "about", about)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "updateUsersProfile: success");
                    }else {
                        Log.e(TAG, "updateUsersProfile: failed -> " + task.getException());
                    }
                });
    }

    public void logout(){
        intentLoginActivity();
        fAuth.signOut();
    }

    public void intentPostRecyclerActivity(){
        Intent recycler = new Intent(context.getApplicationContext(), PostRecyclerView.class);
        recycler.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(recycler);
    }

    public void intentRegisterActivity(){
        Intent register = new Intent(context.getApplicationContext(), RegisterActivity.class);
        register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(register);
    }

    public void intentLoginActivity() {
        Intent login = new Intent(context.getApplicationContext(), LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(login);
    }

    public String generateRandomUser() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi"
                +"jklmnopqrstuvwxyz!_-@#$%&";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    public String generateTimeStamp(){
        return new SimpleDateFormat("ddMMyy_mmss").format(new java.util.Date());
    }

}
