package iessanclemente.PRO.onboarding;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
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

    private EnterUtilities eu;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;


    // Declare the visual components of the layout
    private TextInputEditText tietUserTag;
    private TextInputLayout tilUserTag;
    private TextInputEditText tietPassword;
    private TextInputLayout tilPassword;
    private CheckBox chkRememberMe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1) Set the login layout
        setContentView(R.layout.login_activity);

        // 2) Setup communication with the database
        eu = new EnterUtilities(getApplicationContext());

        // 3) Instance visual components
        tietUserTag = findViewById(R.id.tietUserTag);
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
        tietUserTag.setOnFocusChangeListener((view, b) -> {
            String input = tietUserTag.getText() + "";
            if(!input.equals(""))
                checkUser(input);
        });

        btnLogin.setOnClickListener(view -> {
            User us = eu.getUser(tietUserTag.getText() + "");
            if(us != null)
                checkUserCredentials(us.getUserTag(), tietPassword.getText()+"");
        });

        tvGoRegister.setOnClickListener(view ->  {
            Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(register);
            finish();
        });
    }

    private void googleSignIn() {
        Intent googleIntent = gsc.getSignInIntent();
        startActivityForResult(googleIntent, 100);
    }

    private void checkUser(String input) {
        if (eu.getUser(input) != null) {
            tilUserTag.setError(null);
            tilUserTag.setBoxStrokeColor(Color.GREEN);
            tilUserTag.setStartIconDrawable(R.drawable.check);
            tilUserTag.setStartIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
        } else{
            tilUserTag.setError(getResources().getString(R.string.err_UserDoesNotExists));
            tilUserTag.setBoxStrokeColor(Color.GRAY);
            tilUserTag.setStartIconDrawable(R.drawable.ic_user);
            tilUserTag.setStartIconTintList(ColorStateList.valueOf(Color.GRAY));
        }
    }

    private void checkUserCredentials(String tagId, String pass) {
        if(eu.checkPassword(tagId, pass)){
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

    private void successfulLogin() {
        eu.intentRecyclerView();
        eu.saveCurrentUserSession(tietUserTag.getText()+"", chkRememberMe.isChecked());
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                successfulLogin();
            } catch (ApiException e) {e.printStackTrace();}
        }
    }
}
