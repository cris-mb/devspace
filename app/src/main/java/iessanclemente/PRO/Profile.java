package iessanclemente.PRO;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import iessanclemente.PRO.chat.ContactsListActivity;
import iessanclemente.PRO.onboarding.EnterUtilities;

public class Profile extends AppCompatActivity {

    private static final String TAG = Profile.class.getSimpleName();
    private static final int CHOOSE_PROFILE_IMAGE = 100;

    private EnterUtilities eu;
    private StorageReference stRef;
    private FirebaseFirestore ffStore;
    private FirebaseUser fUser;

    // Some visual components
    private DrawerLayout dwLayout;
    private NavigationView navView;
    private ImageView ivProfileImage;
    private EditText etProfileTag;
    private EditText etProfileUsername;
    private EditText etProfileAboutMe;

    private Dialog pDialog;
    private boolean profileWasModified = false;

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
        etProfileTag = findViewById(R.id.etProfileTag);
        etProfileUsername = findViewById(R.id.etProfileUsername);
        etProfileAboutMe = findViewById(R.id.etProfileAboutMe);

        etProfileTag.setOnLongClickListener(listener);
        etProfileUsername.setOnLongClickListener(listener);
        etProfileAboutMe.setOnLongClickListener(listener);

        dwLayout = findViewById(R.id.navigation_layout_profile);
        navView = findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(item -> {
            navigationItemSelected(item);
            return false;
        });

        displayCurrentUser();

        ivProfileImage.setOnLongClickListener(view -> {
            Intent pickImage = new Intent();
            pickImage.setAction(Intent.ACTION_GET_CONTENT);
            pickImage.setType("image/*");
            startActivityForResult(pickImage, CHOOSE_PROFILE_IMAGE);

            return false;
        });
    }

    private void displayCurrentUser() {
        pDialog = new Dialog(Profile.this);
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

                            displayProfileImage();
                            etProfileTag.setText(ds.get("tag").toString());
                            etProfileUsername.setText(ds.get("username").toString());
                            etProfileAboutMe.setText(ds.get("about").toString());
                            pDialog.dismiss();
                            Log.i(TAG, "fetchCurrentUser: success");
                        }else
                            Log.e(TAG, "fetchCurrentUser: failed -> "+task.getException().getMessage());
                    });
    }

    private void setUserAccountOnHeader() {
        View navHeaderView = navView.inflateHeaderView(R.layout.nav_header);

        TextView tvAccount = navHeaderView.findViewById(R.id.tvUserEmail);
        ImageView ivProfileImageHeader = navHeaderView.findViewById(R.id.ivProfileImageHeader);

        ffStore.collection("users")
                .document(fUser.getUid())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        String imageURL = task.getResult().get("profileImage")+"";
                        String userTag = task.getResult().get("tag")+"";
                        Picasso.with(getApplicationContext()).load(imageURL).into(ivProfileImageHeader);
                        tvAccount.setText("@"+userTag);
                    }

                });
    }

    private void displayProfileImage() {
        ffStore.collection("users")
                .document(fUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        String stReference = task.getResult().get("profileImage")+"";
                        Picasso.with(getApplicationContext()).load(stReference).into(ivProfileImage);
                        Log.d(TAG, "displayProfileImage: success");
                    }else {
                        Log.e(TAG, "displayProfileImage: failed -> " + task.getException());
                    }
                });
    }

    private void navigationItemSelected(@NonNull MenuItem item) {
        CharSequence title = item.getTitle();
        if (getResources().getString(R.string.itPosts_title).contentEquals(title)) {
            Intent home = new Intent(getApplicationContext(), PostRecyclerView.class);
            home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(home);
        } else if (getResources().getString(R.string.itMessages_title).contentEquals(title)) {
            Intent chat = new Intent(getApplicationContext(), ContactsListActivity.class);
            chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(chat);
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
            Intent addPost = new Intent(getApplicationContext(), AddPostActivity.class);
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
        if(requestCode == CHOOSE_PROFILE_IMAGE && resultCode==RESULT_OK){
            if(data != null){
                ivProfileImage.setImageURI(data.getData());

                Bitmap bitmap = ((BitmapDrawable) ivProfileImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                eu.uploadProfileImage(baos.toByteArray());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(!profileWasModified){
            AlertDialog dialog = new AlertDialog.Builder(getApplicationContext())
                    .setTitle(R.string.confirm_profile_changes_title)
                    .setMessage(R.string.confirm_profile_changes_message)
                    .setPositiveButton("OK", (dialog1, which) -> {
                        eu.updateUsersProfile(etProfileTag.getText()+"",
                                etProfileUsername.getText()+"",
                                etProfileAboutMe.getText()+"");
                    })
                    .setNeutralButton("NO", (dialog1, which) -> {
                        //do nothing
                    })
                    .create();
            dialog.show();
        }
        super.onBackPressed();
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            profileWasModified = true;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private View.OnLongClickListener listener = v -> {
        ((EditText) v).setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        ((EditText) v).setFocusable(true);
        ((EditText) v).setFocusableInTouchMode(true);
        ((EditText) v).addTextChangedListener(watcher);

        return false;
    };
}
