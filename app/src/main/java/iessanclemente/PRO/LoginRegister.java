package iessanclemente.PRO;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import iessanclemente.PRO.model.User;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class LoginRegister extends Activity {

    private static final int FILE_CHOOSER = 1;
    private static final int READ_CODE = 2;

    private DatabaseOperations op;

    private String profileImagePath;
    private Uri profileImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_login_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForReadPermission();
        }
        createDatabaseSimulation();
        uploadImagesFromAssets();

        // Layout 'choose_login_register'
        Button btnChooseRegister = findViewById(R.id.btnChooseRegister);
        Button btnChooseLogin = findViewById(R.id.btnChooseLogin);

        View.OnClickListener chooseListener = view -> {
            if(view.getId() == R.id.btnChooseRegister)
                launchRegisterActivity();
            else if(view.getId() == R.id.btnChooseLogin)
                launchLoginActivity();

        };

        btnChooseRegister.setOnClickListener(chooseListener);
        btnChooseLogin.setOnClickListener(chooseListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForReadPermission() {
        int permission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permission == PackageManager.PERMISSION_GRANTED){

        }else{
            this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_CODE);
        }
    }

    private void uploadImagesFromAssets() {
        new Thread(){
            @Override
            public void run() {
                try{

                    String[] postsImages = getAssets().list("postsImages");
                    for(int i=0;i<postsImages.length;i++){
                        serializeImage("postsImages", "postsImages/"+postsImages[i], postsImages[i]);
                    }
                    String[] profileImages = getAssets().list("profileImages");
                    for(int i=0;i<profileImages.length;i++){
                        serializeImage("profileImages", "profileImages/"+profileImages[i], profileImages[i]);
                    }

                }catch(Exception e){
                    Log.e("Error", e.getMessage()+" "+e.getCause());
                }
            }


        }.start();
    }

    private void serializeImage(String folderName, String fileName, String newName) throws Exception {
        File outputFile = new File(getFilesDir()+File.separator+folderName);
        outputFile.mkdirs();
        InputStream is = getAssets().open(fileName);
        OutputStream os = new FileOutputStream(getFilesDir()+File.separator+folderName+File.separator+newName);

        int tamRead;
        byte[] buffer = new byte[2048];
        while((tamRead = is.read(buffer))>0){
            os.write(buffer, 0, tamRead);
        }
        os.flush();
        os.close();
        is.close();

    }

    private void createDatabaseSimulation() {
        File directory = new File("/data/data/"+getPackageName()+"/databases");
        if(directory.listFiles() == null || directory.listFiles().length == 0) {
            new Thread() {
                @Override
                public void run() {
                    op = new DatabaseOperations(getApplicationContext());
                    op.sqlLiteDB = op.getWritableDatabase();

                    op.tablesCreation();
                    op.addAllPost();

                    op.close();
                }
            }.start();
        }
    }

    private void launchLoginActivity() {
        // Set the login layout
        setContentView(R.layout.login_activity);

        // Declare the layout Buttons and EditTexts
        EditText etTag = findViewById(R.id.etLoginTag);
        EditText etPassword = findViewById(R.id.etLoginPassword);
        TextView tvGoBack = findViewById(R.id.tvGoBack);
        tvGoBack.setOnClickListener(view ->  {
            onCreate(null);
        });

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> {
            User us = getUser(etTag.getText() + "");
            if(us != null) {
                if(checkPassword(us.getTagId(), etPassword.getText()+"")){
                    intentRecyclerView();
                    saveCurrentUserSession(etTag.getText()+"");
                }else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_WrongCredentials), Toast.LENGTH_SHORT).show();
                }
            }else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_UserDoesNotExists), Toast.LENGTH_SHORT).show();

        });
    }

    private boolean checkPassword(String tagId, String pass) {
        User us = getUser(tagId);
        if(pass.equals(us.getPassword())){
            return true;
        }

        return false;
    }

    private void launchRegisterActivity() {
        // Set the register layout
        setContentView(R.layout.register_activity);

        // Declare the layout Buttons and EditTexts
        EditText etTag = findViewById(R.id.etRegisterTag);
        EditText etUsername = findViewById(R.id.etRegisterUsername);
        Button btnFileChooser = findViewById(R.id.btnFileChooser);
        EditText etEmail = findViewById(R.id.etRegisterEmail);
        EditText etPassword = findViewById(R.id.etRegisterPassword);
        EditText etAbout = findViewById(R.id.etRegisterAboutMe);
        TextView tvGoBack = findViewById(R.id.tvGoBack);
        tvGoBack.setOnClickListener(view ->  {
            onCreate(null);
        });

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view -> {
            User us = getUser(etTag.getText() + "");
            if (us == null) {
                if(!(etUsername.getText()+"").equals("") ||
                    profileImagePath != null ||
                    !(etEmail.getText()+"").equals("") ||
                    !(etPassword.getText()+"").equals("") ||
                    !(etAbout.getText()+"").equals("")){

                    op = new DatabaseOperations(getApplicationContext());
                    op.sqlLiteDB = op.getWritableDatabase();

                    op.addUser(new User(etTag.getText() + "", etUsername.getText() + "", profileImagePath, profileImageUri, etEmail.getText() + "", etPassword.getText() + "", etAbout.getText() + ""));

                    op.close();
                    intentRecyclerView();
                    saveCurrentUserSession(etTag.getText()+"");

                }else{
                    Toast.makeText(this, getResources().getString(R.string.err_IncompleteRegistration), Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, getResources().getString(R.string.err_UserAlreadyExists), Toast.LENGTH_SHORT).show();
            }
        });

        btnFileChooser.setOnClickListener(view -> {
            openFileChooser();
        });
    }

    private User getUser(String tagId) {
        op = new DatabaseOperations(this);
        op.sqlLiteDB = op.getWritableDatabase();

        User us = op.getUser(tagId);

        op.close();
        return us;
    }

    private void intentRecyclerView() {
        Intent recyclerView = new Intent(this, PostRecyclerView.class);
        startActivity(recyclerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FILE_CHOOSER && resultCode == Activity.RESULT_OK){
            if(data == null)return;

            Uri path = data.getData();
            profileImageUri = path;
            profileImagePath = path.getPath();
        }
    }

    public void openFileChooser(){
        Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
        chooser.setType("image /*");
        startActivityForResult(chooser, FILE_CHOOSER);
    }

    private void saveCurrentUserSession(String tagIdValue) {
        // TODO : with SharedPreferences
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == READ_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_CODE);
            }
        }
    }
}
