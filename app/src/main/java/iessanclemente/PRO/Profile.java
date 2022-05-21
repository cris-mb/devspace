package iessanclemente.PRO;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import iessanclemente.PRO.model.User;
import iessanclemente.PRO.onboarding.OnBoardingActivity;

public class Profile extends AppCompatActivity {

    private DatabaseOperations op;

    // Some visual components
    private DrawerLayout dwLayout;
    private NavigationView navView;

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

        dwLayout = findViewById(R.id.navigation_layout_profile);
        navView = findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(item -> {
            navigationItemSelected(item);
            return false;
        });

        User us = getUser(getCurrentUser());

        if(us != null && !us.getProfileImagePath().equals("")){
            ivProfile.setImageBitmap(BitmapFactory.decodeFile(us.getProfileImagePath()));
        }else{
            ivProfile.setImageDrawable(getDrawable(R.drawable.account_36));
        }

        tvProfileTag.setText(us.getUserTag());
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

    private void navigationItemSelected(@NonNull MenuItem item) {
        CharSequence title = item.getTitle();
        if (getResources().getString(R.string.itPosts_title).contentEquals(title)) {
            Intent home = new Intent(getApplicationContext(), PostRecyclerView.class);
            startActivity(home);
        } else if (getResources().getString(R.string.itProfile_title).contentEquals(title)) {
            Intent profile = new Intent(getApplicationContext(), Profile.class);
            startActivity(profile);
        } else if (getResources().getString(R.string.itMessages_title).contentEquals(title)) {
            //TODO : Direct Messages activity
        } else if (getResources().getString(R.string.itSettings_title).contentEquals(title)) {
            // TODO : Preferences xml
        } else if (getResources().getString(R.string.itLogout_title).contentEquals(title)) {
            // Remove last session credentials
            SharedPreferences shPref = getSharedPreferences("current_user",MODE_PRIVATE);
            shPref.edit()
                    .remove("tag")
                    .putBoolean("staySigned", false)
                    .apply();

            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preferences_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getTitle() == null )
            finish();

        if(item.getTitle().equals(getResources().getString(R.string.title_miLogout))){
            Intent logout = new Intent(getApplicationContext(), OnBoardingActivity.class);
            startActivity(logout);
        }else if(item.getTitle().equals(getResources().getString(R.string.title_miAddPost))){
            Intent addPost = new Intent(getApplicationContext(), AddPost.class);
            startActivity(addPost);
        }else if(item.getTitle().equals(getResources().getString(R.string.itSettings_title))){
            if(dwLayout.isDrawerOpen(navView))
                dwLayout.closeDrawer(GravityCompat.END);
            else
                dwLayout.openDrawer(GravityCompat.END);
        }

        return super.onOptionsItemSelected(item);
    }

    private String getCurrentUser() {
        SharedPreferences shPreferences = getSharedPreferences("current_user", MODE_PRIVATE);

        return shPreferences.getString("tag", null);
    }
}
