package iessanclemente.PRO.onboarding;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import iessanclemente.PRO.R;

public class RegisterActivity extends Activity{

    private static final int FILE_CHOOSER = 1;
    private EnterUtilities eu;

    // Declare the visual components
    private CircleImageView ivAccountIconRegister;
    private TextInputEditText tietUserTag;
    private TextInputLayout tilUserTag;
    private TextInputEditText tietEmail;
    private TextInputLayout tilEmail;
    private TextInputEditText tietPassword;
    private TextInputLayout tilPassword;
    private TextInputEditText tietConfirmPassword;
    private TextInputLayout tilConfirmPassword;
    private TextView tvGoLogin;
    private Button btnRegister;

    private boolean selectedCustomImage = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        eu = new EnterUtilities(getApplicationContext());

        ivAccountIconRegister = findViewById(R.id.ivAccountIconRegister);

        tietUserTag = findViewById(R.id.tietUserTag);
        tilUserTag = findViewById(R.id.tilUserTag);
        tietUserTag.setOnClickListener(view -> {clearErrors();});

        tietEmail = findViewById(R.id.tietEmail);
        tilEmail = findViewById(R.id.tilEmail);

        tietPassword = findViewById(R.id.tietPassword);
        tilPassword = findViewById(R.id.tilPassword);
        tietPassword.setOnClickListener(view -> {clearErrors();});

        tietConfirmPassword = findViewById(R.id.tietConfirmPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tietConfirmPassword.setOnClickListener(view -> {clearErrors();});

        ivAccountIconRegister.setOnClickListener(view -> {
            openImageChooser();
        });

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view -> {
            eu.start();

            String userTag = tietUserTag.getText()+"";
            String email = tietEmail.getText()+"";
            String password = tietPassword.getText()+"";
            String confirmPassword = tietConfirmPassword.getText()+"";
            String profileImageRef = selectedCustomImage?
                            eu.searchForProfileImageRef()
                            :"gs://devspace-b93f2.appspot.com/profile_images/anonymous.png";

            clearErrors();
            if(!confirmPassword.equals(password)) {
                setErrorLayoutOn(tilPassword,"");
                setErrorLayoutOn(tilConfirmPassword, getResources().getString(R.string.err_PasswordDoNotMatch));
            }
            if(!eu.registerNewUserAuthentication(userTag, email, password, profileImageRef)){
                setErrorLayoutOn(tilUserTag,"");
                setErrorLayoutOn(tilUserTag, getResources().getString(R.string.err_UserAlreadyExists));
            }else{
                eu.intentPostRecyclerActivity();
            }

            eu.stop();
        });

        tvGoLogin = findViewById(R.id.tvGoLogin);
        tvGoLogin.setOnClickListener(view ->  {
            eu.intentLoginActivity();
        });
    }

    public void openImageChooser(){
        Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
        chooser.setType("image/*");
        startActivityForResult(chooser, FILE_CHOOSER);
    }

    private void clearErrors() {
        tilUserTag.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private void setErrorLayoutOn(TextInputLayout til, String errorMessage) {
        til.setError(errorMessage);
        til.setBoxStrokeColor(getResources().getColor(R.color.warning));
        til.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.warning)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FILE_CHOOSER){
            if(resultCode == RESULT_OK){
                ivAccountIconRegister.setImageURI(data.getData());
                eu.uploadProfileImageOnStorage(data.getData());
                selectedCustomImage = true;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(eu.checkUserAuthentication()){
            eu.intentPostRecyclerActivity();
        }
    }
}
