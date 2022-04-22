package iessanclemente.PRO;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import iessanclemente.PRO.model.User;

public class Profile extends AppCompatActivity {

    private DatabaseOperations op;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ImageView ivProfile = findViewById(R.id.ivProfileIcon);
        TextView tvProfileTag = findViewById(R.id.tvProfileTag);
        TextView tvProfileUsername = findViewById(R.id.tvProfileUsername);
        TextView tvProfileAboutMe = findViewById(R.id.tvProfileAboutMe);

        User us = getUser(getCurrentUser());

        if(us != null && !us.getProfileImagePath().equals("")){
            ivProfile.setImageBitmap(BitmapFactory.decodeFile(us.getProfileImagePath()));
        }else{
            ivProfile.setImageDrawable(getDrawable(R.drawable.account_36));
        }

        tvProfileTag.setText(us.getTagId());
        tvProfileUsername.setText(us.getUsername());
        tvProfileAboutMe.setText(us.getAbout());
    }

    private User getUser(String tagId) {
        op = new DatabaseOperations(getApplicationContext());
        op.sqlLiteDB = op.getWritableDatabase();

        User us = op.getUser(tagId);

        op.close();
        return us;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preferences_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getTitle() != null && item.getTitle().equals(getResources().getString(R.string.title_miLogout))){
            Intent logout = new Intent(getApplicationContext(), LoginRegister.class);
            startActivity(logout);
        }else if(item.getTitle() != null && item.getTitle().equals(getResources().getString(R.string.title_miAddPost))){
            Intent addPost = new Intent(getApplicationContext(), AddPost.class);
            startActivity(addPost);
        }

        return super.onOptionsItemSelected(item);
    }

    private String getCurrentUser() {
        String tagId = "";

        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc=db.parse(new File(getFilesDir()+File.separator+"current_user.xml"));
            doc.getDocumentElement().normalize();

            NodeList el = doc.getElementsByTagName("tagId");
            tagId = el.item(0).getTextContent();

        }catch(Exception e){
            e.printStackTrace();
        }

        return tagId;
    }
}
