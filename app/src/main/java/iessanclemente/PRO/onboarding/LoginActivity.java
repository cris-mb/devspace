package iessanclemente.PRO.onboarding;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import iessanclemente.PRO.PostRecyclerView;
import iessanclemente.PRO.R;
import iessanclemente.PRO.model.User;

public class LoginActivity extends Activity{

    private static final int READ_CODE = 2;
    private static final int GOOGLE_LOGIN = 3;

    private EnterUtilities eu;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;


    // Declare the visual components of the layout
    private TextInputEditText tietUserEmail;
    private TextInputLayout tilUserTag;
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
        eu = new EnterUtilities(getApplicationContext());
        checkLastSession();

        // 3) Instance visual components
        tietUserEmail = findViewById(R.id.tietUserEmail);
        tilUserTag = findViewById(R.id.tilUserTag);

        tietPassword = findViewById(R.id.tietPassword);
        tilPassword = findViewById(R.id.tilPassword);

        chkRememberMe = findViewById(R.id.chkRememberMe);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoRegister = findViewById(R.id.tvGoRegister);
        Button btnGoogleLogin = findViewById(R.id.btnGoogleLogin);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this,gso);
        btnGoogleLogin.setOnClickListener(view -> {
            googleSignIn();
        });

        // 4) Instance its listeners
        tietUserEmail.setOnFocusChangeListener((view, b) -> {
            String email = tietUserEmail.getText() + "";
            if(!email.equals(""))
                checkUserExistence(email);
        });

        btnLogin.setOnClickListener(view -> {
            User us = eu.checkUserExistence(tietUserEmail.getText() + "");
            if(us != null) {
                checkUserCredentials(us.getUserTag(), us.getPassword());
                eu.loginUser(tietUserEmail.getText()+"", tietPassword.getText()+"", chkRememberMe.isChecked());
            }else
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.err_UserDoesNotExists), Toast.LENGTH_SHORT).show();
        });

        tvGoRegister.setOnClickListener(view ->  {
            Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(register);
            finish();
        });
    }

    private void googleSignIn() {
        Intent googleIntent = gsc.getSignInIntent();
        startActivityForResult(googleIntent, GOOGLE_LOGIN);
    }

    private void checkLastSession() {
        boolean maintainLastSession = eu.checkExistingSession();
        if(maintainLastSession){
            intentPostRecyclerActivity();
        }
    }

    private void checkUserExistence(String email) {
//        if (eu.checkUserExistence(email) != null) {
//            tilUserTag.setError(null);
//            tilUserTag.setBoxStrokeColor(Color.GREEN);
//            tilUserTag.setStartIconDrawable(R.drawable.check);
//            tilUserTag.setStartIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
//        } else{
//            tilUserTag.setError(getResources().getString(R.string.err_UserDoesNotExists));
//            tilUserTag.setBoxStrokeColor(Color.GRAY);
//            tilUserTag.setStartIconDrawable(R.drawable.ic_user);
//            tilUserTag.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));
//        }
    }

    private void checkUserCredentials(String tagId, String pass) {
        if(eu.checkPassword(pass)){
            tilPassword.setError(null);
            tilPassword.setStartIconDrawable(R.drawable.check);
            tilPassword.setStartIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.dkblue)));
            successfulLogin();
        }else{
            tilPassword.setError(getResources().getString(R.string.err_wrong_password));
            tilPassword.setStartIconDrawable(R.drawable.ic_lock);
            tilPassword.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));            
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_LOGIN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                successfulLogin();
            } catch (ApiException e) {e.printStackTrace();}
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForReadPermission() {
        int permission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permission == PackageManager.PERMISSION_GRANTED){

        }else{
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

    private void successfulLogin() {
        // TODO : unify methods
        intentPostRecyclerActivity();
    }

    private void intentPostRecyclerActivity() {
        Intent recycler = new Intent(getApplicationContext(), PostRecyclerView.class);
        recycler.putExtra("currUserUid", eu.getCurrentUserUid());
        startActivity(recycler);
        finish();
    }
}
