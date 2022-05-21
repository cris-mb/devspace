package iessanclemente.PRO.onboarding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import iessanclemente.PRO.DatabaseOperations;
import iessanclemente.PRO.PostRecyclerView;
import iessanclemente.PRO.model.User;

public class EnterUtilities {

    private final Context context;
    private DatabaseOperations op;
    private FirebaseAuth fAuth;

    public EnterUtilities(Context ctx){
        this.context = ctx;
    }

    public void start(){
        fAuth = FirebaseAuth.getInstance();
        op = new DatabaseOperations(context);
        op.sqlLiteDB = op.getWritableDatabase();
    }

    public void stop(){
        FirebaseAuth.getInstance().signOut();
        op.close();
    }

    public boolean checkPassword(String tagId, String pass) {
        User us = getUser(tagId);
        return pass.equals(us.getPassword());
    }

    public User getUser(String tagId) {
        op = new DatabaseOperations(context);
        op.sqlLiteDB = op.getWritableDatabase();

        User us = op.getUser(tagId);

        op.close();
        return us;
    }

    public boolean addUser(String newUserTag, String email, String password){
        User us = getUser(newUserTag);
        if (us == null) {
            if(!(newUserTag).equals("") ||
                    !(email).equals("") ||
                    !(password).equals(""))
                successfulRegistration(new User(newUserTag, email, password));
            return true;
        }

        return false;
    }

    private void successfulRegistration(User u) {
        fAuth = FirebaseAuth.getInstance();
        fAuth.createUserWithEmailAndPassword(u.getEmail(), u.getPassword());
        intentRecyclerView();
        saveCurrentUserSession(u.getUserTag(), true);
    }

    public void intentRecyclerView() {
        Intent recyclerView = new Intent(context, PostRecyclerView.class);
        recyclerView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(recyclerView);
    }

    public void saveCurrentUserSession(String tagIdValue, boolean staySigned) {
        SharedPreferences.Editor editor = context.getSharedPreferences("current_user", Activity.MODE_PRIVATE).edit();
        editor
                .putString("tag", tagIdValue)
                .putBoolean("staySigned", staySigned)
                .apply();
    }

}
