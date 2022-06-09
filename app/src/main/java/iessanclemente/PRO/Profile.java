package iessanclemente.PRO;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import iessanclemente.PRO.onboarding.EnterUtilities;

public class Profile extends AppCompatActivity {

    private static final String TAG = Profile.class.getSimpleName();

    private EnterUtilities eu;
    private StorageReference stRef;
    private FirebaseFirestore ffStore;
    private FirebaseUser fUser;

    // Some visual components
    private DrawerLayout dwLayout;
    private NavigationView navView;
    private ImageView ivProfileImage;
    private TextView tvProfileTag;
    private TextView tvProfileUsername;
    private TextView tvProfileAboutMe;

    private ProgressDialog pDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        eu = new EnterUtilities(getApplicationContext());
        stRef = FirebaseStorage.getInstance().getReference();
        ffStore = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        ivProfileImage = findViewById(R.id.ivProfileIcon);
        tvProfileTag = findViewById(R.id.tvProfileTag);
        tvProfileUsername = findViewById(R.id.tvProfileUsername);
        tvProfileAboutMe = findViewById(R.id.tvProfileAboutMe);

        dwLayout = findViewById(R.id.navigation_layout_profile);
        navView = findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(item -> {
            navigationItemSelected(item);
            return false;
        });

        displayCurrentUser();

        ivProfileImage.setOnClickListener(view -> {
            Intent pickImage = new Intent();
            pickImage.setAction(Intent.ACTION_GET_CONTENT);
            pickImage.setType("image/*");
            startActivityForResult(pickImage, 100);
        });
    }

    private void displayCurrentUser() {
        pDialog = new ProgressDialog(Profile.this);
        pDialog.setContentView(R.layout.loading_view);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        setUserAccountOnHeader();

        ffStore.collection("users")
                    .document(fUser.getUid())
                    .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot ds = task.getResult();
                            ds.get("profileImage");

                            // ivProfile.setImageBitmap(BitmapFactory.decodeFile(currUser.getProfileImage()));
                            displayProfileImage();
                            tvProfileTag.setText(ds.get("tag").toString());
                            tvProfileUsername.setText(ds.get("username").toString());
                            tvProfileAboutMe.setText(ds.get("about").toString());
                            pDialog.dismiss();
                            Log.i(TAG, "fetchCurrentUser: success");
                        }else
                            Log.e(TAG, "fetchCurrentUser: failed -> "+task.getException().getMessage());
                    });
    }

    private void setUserAccountOnHeader() {
        View navHeaderView = navView.inflateHeaderView(R.layout.nav_header);
        ImageView ivProfileImageHeader = navHeaderView.findViewById(R.id.ivProfileImageHeader);
        TextView tvUserEmail = navHeaderView.findViewById(R.id.tvUserEmail);

        ivProfileImageHeader.setImageDrawable(ivProfileImage.getDrawable());
        tvUserEmail.setText(fUser.getEmail());
    }

    private void displayProfileImage() {
        ffStore.collection("users")
                .document(fUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        String stReference = task.getResult().get("profileImage")+"";
                        stReference = stReference.substring(stReference.lastIndexOf("/")+1);

                        stRef.child("profile_images/"+stReference).getDownloadUrl().addOnCompleteListener(download -> {
                            if(download.isSuccessful()){
                                Picasso.with(getApplicationContext()).load(download.getResult()).into(ivProfileImage);
                                Log.d(TAG, "displayProfileImage: success("+download.getResult()+")");
                            }else {
                                Log.e(TAG, "displayProfileImage: failed -> " + download.getException());
                            }
                        });
                    }else {
                        Log.e(TAG, "displayProfileImage: failed -> " + task.getException());
                    }
                });
    }

    private void navigationItemSelected(@NonNull MenuItem item) {
        CharSequence title = item.getTitle();
        if (getResources().getString(R.string.itPosts_title).contentEquals(title)) {
            Intent home = new Intent(getApplicationContext(), PostRecyclerView.class);
            startActivity(home);
        } else if (getResources().getString(R.string.itMessages_title).contentEquals(title)) {
            //TODO : Direct Messages activity
        } else if (getResources().getString(R.string.itSettings_title).contentEquals(title)) {
            // TODO : Preferences xml
        } else if (getResources().getString(R.string.itLogout_title).contentEquals(title)) {
            // Remove last session credentials
            eu.logout();
            eu.intentLoginActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getTitle() == null)
            return false;

        if(item.getTitle().equals(getResources().getString(R.string.title_miLogout))){
            eu.logout();
            finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode==RESULT_OK){
            if(data != null){
                ivProfileImage.setImageURI(data.getData());

                Bitmap bitmap = ((BitmapDrawable) ivProfileImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                eu.uploadProfileImage(baos.toByteArray());
            }
        }
    }

}
