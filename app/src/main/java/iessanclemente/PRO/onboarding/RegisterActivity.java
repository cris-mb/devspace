package iessanclemente.PRO.onboarding;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import iessanclemente.PRO.R;
import iessanclemente.PRO.model.User;

public class RegisterActivity extends Activity{

    private static final String TAG = RegisterActivity.class.getSimpleName();
//    private static final int FILE_CHOOSER = 1;

    private EnterUtilities eu;
    private FirebaseAuth fAuth;
    private DatabaseReference usersRef;

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
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

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
            String profileImageRef = "gs://devspace-b93f2.appspot.com/profile_images/anonymous.png";

            if(checkCorrectFieldsState(email, password, confirmPassword, profileImageRef)) {
                User us = new User(email, password, profileImageRef);
                registerNewUserAuthentication(us);
            }
        });

        tvGoLogin = findViewById(R.id.tvGoLogin);
        tvGoLogin.setOnClickListener(view ->  {
            eu.intentLoginActivity();
        });
    }

    private boolean checkCorrectFieldsState(String email, String password, String confirmPassword, String profileImageRef) {

        if(email.equals("") || password.equals("") || confirmPassword.equals("")){
            setError(getResources().getString(R.string.err_IncompleteRegistration));
            return false;
        }else if(profileImageRef.isEmpty()){
            setError(getResources().getString(R.string.err_no_profile_image_provided));
            return false;
        }else if(!confirmPassword.equals(password)) {
            setError(getResources().getString(R.string.err_PasswordDoNotMatch));
            return false;
        }

        return true;
    }

    public void registerNewUserAuthentication(User us){
        fAuth.createUserWithEmailAndPassword(us.getEmail(), us.getPassword()).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Log.d(TAG, "registerNewUserAuthentication: " + task.getResult());
                saveUserInfoToDatabase(us);
                eu.intentPostRecyclerActivity();
            }
            Log.d(TAG, "registerNewUserAuthentication: " + task.getResult());
            setError(task.getException().getMessage());
        });
    }

    public void saveUserInfoToDatabase(User us) {
        HashMap<String, Object> newUserMap = new HashMap<>();
        newUserMap.put("about",us.getAbout());
        newUserMap.put("email", us.getEmail());
        newUserMap.put("password", us.getPassword());
        newUserMap.put("profileImage", us.getProfileImagePath());
        newUserMap.put("tag", us.getUserTag());
        newUserMap.put("username", us.getUsername());

        usersRef.updateChildren(newUserMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(context, "Account stored in database", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

//    public void openImageChooser(){
//        Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
//        chooser.setType("image/*");
//        startActivityForResult(chooser, FILE_CHOOSER);
//    }

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

    private void clearErrors(){
        tilEmail.setBoxStrokeColor(Color.GRAY);
        tilEmail.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));

        tilPassword.setBoxStrokeColor(Color.GRAY);
        tilPassword.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));

        tilConfirmPassword.setBoxStrokeColor(Color.GRAY);
        tilConfirmPassword.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == FILE_CHOOSER){
//            if(resultCode == RESULT_OK){
//                eu.uploadProfileImageOnStorage(data.getData());
//                selectedCustomImage = true;
//            }
//        }
//    }
}
