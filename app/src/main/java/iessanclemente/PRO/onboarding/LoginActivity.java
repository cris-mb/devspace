package iessanclemente.PRO.onboarding;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.sql.Driver;
import java.util.HashMap;

import iessanclemente.PRO.PostRecyclerView;
import iessanclemente.PRO.R;
import iessanclemente.PRO.model.User;

public class LoginActivity extends Activity{

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int READ_CODE = 2;
    private static final int GOOGLE_LOGIN = 3;

    private EnterUtilities eu;
    private FirebaseAuth fAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;


    // Declare the visual components of the layout
    private TextInputEditText tietUserEmail;
    private TextInputLayout tilUserEmail;
    private TextInputEditText tietPassword;
    private TextInputLayout tilPassword;
    private CheckBox chkRememberMe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1) Set the login layout
        setContentView(R.layout.login_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForReadPermission();
        }

        // 2) Setup communication with the database
        eu = new EnterUtilities(LoginActivity.this);
        fAuth = FirebaseAuth.getInstance();

        // 3) Instance visual components
        tietUserEmail = findViewById(R.id.tietUserEmail);
        tilUserEmail = findViewById(R.id.tilUserEmail);

        tietPassword = findViewById(R.id.tietPassword);
        tilPassword = findViewById(R.id.tilPassword);

        chkRememberMe = findViewById(R.id.chkRememberMe);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoRegister = findViewById(R.id.tvGoRegister);
        Button btnGoogleLogin = findViewById(R.id.btnGoogleLogin);

        // 4) Google Authentication
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this,gso);
        btnGoogleLogin.setOnClickListener(view -> {
            googleSignIn();
        });

        // 5) Instance its listeners

        addEditTextsListener();

        btnLogin.setOnClickListener(view -> {
            String email = tietUserEmail.getText()+"";
            String password = tietPassword.getText()+"";

            if(checkCorrectFieldsState(email, password))
                loginUserWithCredentials(email, password, chkRememberMe.isChecked());
        });

        tvGoRegister.setPaintFlags(tvGoRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvGoRegister.setOnClickListener(view ->  {
            eu.intentRegisterActivity();
            finish();
        });
    }

    private void googleSignIn() {
        Intent googleIntent = gsc.getSignInIntent();
        startActivityForResult(googleIntent, GOOGLE_LOGIN);
    }

    public void loginUserWithCredentials(String email, String pass, boolean maintainSession){

        fAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Log.d(TAG, "loginUserWithCredentials : "+task.getResult());
                eu.intentPostRecyclerActivity();
            }else {
                Log.e(TAG, "loginUserWithCredentials : "+task.getException());
                setError(task.getException().getMessage());
            }
        });
    }

    private boolean checkCorrectFieldsState(String email, String password) {

        if(email.isEmpty()){
            setError(getResources().getString(R.string.err_IncompleteRegistration));
            return false;
        }else if(password.isEmpty()){
            setError(getResources().getString(R.string.err_IncompleteRegistration));
            return false;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean maintainLastSession = eu.checkUserAuthentication();
        if(maintainLastSession){
            eu.intentPostRecyclerActivity();
        }
    }

    private void setError(String errorMessage) {
        setErrorOn(tilUserEmail);
        setErrorOn(tilPassword);

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
        tilUserEmail.setBoxStrokeColor(Color.GRAY);
        tilUserEmail.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));

        tilPassword.setBoxStrokeColor(Color.GRAY);
        tilPassword.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));
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
        tietUserEmail.addTextChangedListener(textWatcher);
        tietPassword.addTextChangedListener(textWatcher);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_LOGIN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result != null && result.getSignInAccount() != null && result.isSuccess()){
                eu.firebaseGoogleAuth(result.getSignInAccount());
            }else
                Log.e(TAG, "GoogleSignInResult failed (may be null)");
        }
    }

    public void saveUserInfoToDatabase(String email) {
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("about", "Hey there! I'm a new user of DevSpace");
        userData.put("email", email);
        userData.put("profileImage", "gs://devspace-b93f2.appspot.com/profile_images/anonymous.png");
        userData.put("tag", "@devUser"+eu.generateRandomUser());
        userData.put("username", "Anonymous");

        String currUserUid = fAuth.getCurrentUser().getUid();

        eu.registerNewUser(currUserUid, userData);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForReadPermission() {
        int permission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_CODE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == READ_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_CODE);
        }
    }
}
