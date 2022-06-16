package iessanclemente.PRO.onboarding;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import iessanclemente.PRO.R;
import iessanclemente.PRO.model.User;

public class RegisterActivity extends Activity{

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EnterUtilities eu;
    private FirebaseAuth fAuth;

    // Declare the visual components
    private TextInputEditText tietEmail;
    private TextInputLayout tilEmail;
    private TextInputEditText tietPassword;
    private TextInputLayout tilPassword;
    private TextInputEditText tietConfirmPassword;
    private TextInputLayout tilConfirmPassword;
    private TextView tvGoLogin;
    private Button btnRegister;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        eu = new EnterUtilities(getApplicationContext());
        fAuth = FirebaseAuth.getInstance();

        tietEmail = findViewById(R.id.tietEmail);
        tilEmail = findViewById(R.id.tilEmail);

        tietPassword = findViewById(R.id.tietPassword);
        tilPassword = findViewById(R.id.tilPassword);
        tietPassword.setOnClickListener(view -> {clearErrors();});

        tietConfirmPassword = findViewById(R.id.tietConfirmPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tietConfirmPassword.setOnClickListener(view -> {clearErrors();});

        // Adding listener to visual components
        addEditTextsListener();

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view -> {
            String email = tietEmail.getText()+"";
            String password = tietPassword.getText()+"";
            String confirmPassword = tietConfirmPassword.getText()+"";

            if(checkCorrectFieldsState(email, password, confirmPassword)) {
                registerNewUserAuthentication(email, password);
            }
        });

        tvGoLogin = findViewById(R.id.tvGoLogin);
        tvGoLogin.setPaintFlags(tvGoLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvGoLogin.setOnClickListener(view ->  {
            eu.intentLoginActivity();
        });
    }

    private boolean checkCorrectFieldsState(String email, String password, String confirmPassword) {

        if(email.equals("") || password.equals("") || confirmPassword.equals("")){
            setError(getResources().getString(R.string.err_IncompleteRegistration));
            return false;
        }else if(!confirmPassword.equals(password)) {
            setError(getResources().getString(R.string.err_PasswordDoNotMatch));
            return false;
        }

        return true;
    }

    private void registerNewUserAuthentication(String email, String pass){
        fAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Log.d(TAG, "registerNewUserAuthentication was successful? = " + task.isSuccessful());
                saveUserInfoToDatabase(email);
                eu.intentPostRecyclerActivity();
            }else {
                Log.e(TAG, "registerNewUserAuthentication: " + task.getException());
                setError(task.getException().getMessage());
            }
        });
    }

    public void saveUserInfoToDatabase(String email) {
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("about", "Hey there! I'm a new user of DevSpace");
        userData.put("email", email);
        userData.put("profileImage", "https://firebasestorage.googleapis.com/v0/b/devspace-b93f2.appspot.com/o/profile_images%2Fanonymous.png?alt=media&token=9d838688-6f99-4e6a-88f5-91ee892bbe89");
        userData.put("tag", "devUser"+eu.generateRandomHash(12));
        userData.put("username", "Anonymous");

        String currUserUid = fAuth.getCurrentUser().getUid();

        eu.registerNewUser(currUserUid, userData);
    }

    private void setError(String errorMessage) {
        setErrorOn(tilEmail);
        setErrorOn(tilPassword);
        setErrorOn(tilConfirmPassword);

        AlertDialog errorDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(errorMessage)
                .setNeutralButton("OK", (dialog, which) -> {
                    // do nothing
                })
                .show();
    }

    private void setErrorOn(TextInputLayout til){
        til.setError(null);
        til.setBoxStrokeColor(Color.RED);
        til.setStartIconTintList(ColorStateList.valueOf(Color.RED));
    }

    private void addEditTextsListener() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearErrors();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        tietEmail.addTextChangedListener(textWatcher);
        tietPassword.addTextChangedListener(textWatcher);
        tietConfirmPassword.addTextChangedListener(textWatcher);
    }

    private void clearErrors(){
        tilEmail.setBoxStrokeColor(Color.GRAY);
        tilEmail.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));

        tilPassword.setBoxStrokeColor(Color.GRAY);
        tilPassword.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));

        tilConfirmPassword.setBoxStrokeColor(Color.GRAY);
        tilConfirmPassword.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));
    }

}
