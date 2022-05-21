package iessanclemente.PRO.onboarding;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import iessanclemente.PRO.DatabaseOperations;
import iessanclemente.PRO.PostRecyclerView;
import iessanclemente.PRO.R;
import iessanclemente.PRO.model.User;

public class OnBoardingActivity extends Activity {

    private static final int FILE_CHOOSER = 1;
    private static final int READ_CODE = 2;

    private DatabaseOperations op;
    private FirebaseAuth fAuth;

    private String profileImagePath;
    private Uri profileImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_login_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForReadPermission();
        }
        fAuth = FirebaseAuth.getInstance();
        createDatabaseSimulation();
        uploadImagesFromAssets();

        checkLastSession();
        // Layout 'choose_login_register'
        Button btnChooseRegister = findViewById(R.id.btnChooseRegister);
        Button btnChooseLogin = findViewById(R.id.btnChooseLogin);

        View.OnClickListener chooseListener = view -> {
            if(view.getId() == R.id.btnChooseRegister) {
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
            }else if(view.getId() == R.id.btnChooseLogin) {
                Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentLogin);
            }
        };

        btnChooseRegister.setOnClickListener(chooseListener);
        btnChooseLogin.setOnClickListener(chooseListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(fAuth.getCurrentUser() == null){
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login);
        }
    }

    private void checkLastSession() {
        SharedPreferences shPref = getSharedPreferences("current_user", MODE_PRIVATE);
        boolean maintainLastSession = shPref.getBoolean("staySigned", false);
        if(maintainLastSession){
            Intent recycler = new Intent(getApplicationContext(), PostRecyclerView.class);
            startActivity(recycler);
            finish();
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
