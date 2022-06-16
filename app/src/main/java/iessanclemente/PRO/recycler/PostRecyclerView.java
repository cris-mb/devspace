package iessanclemente.PRO.recycler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import iessanclemente.PRO.AddPostActivity;
import iessanclemente.PRO.Profile;
import iessanclemente.PRO.R;
import iessanclemente.PRO.settings.SettingActivity;
import iessanclemente.PRO.chat.listmessages.ChatRecyclerView;
import iessanclemente.PRO.model.Post;
import iessanclemente.PRO.onboarding.EnterUtilities;

public class PostRecyclerView extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static final String TAG = PostRecyclerView.class.getSimpleName();
    private long timeRemaining;

    private EnterUtilities eu;
    private FirebaseFirestore ffStore;
    private PostAdapter adapter;

    private TextView tvProfileEmailHeader;
    private ImageView ivProfileImageHeader;

    private DrawerLayout dwLayout;
    private NavigationView navView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout srl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Set the content view & instance the database classes
        setContentView(R.layout.recycler_view_activity);
        eu = new EnterUtilities(PostRecyclerView.this);
        ffStore = FirebaseFirestore.getInstance();

        // 2. Set the SearchView to filter posts
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);

        // 3. Preparing the NavigationView that will drive the user lead to the different functionalities
        dwLayout = findViewById(R.id.navigationLayoutRecycler);
        navView = findViewById(R.id.navigation_view);
        prepareNavigationView();

        // 4. Preparing the RecyclerView that will show the posts
        recyclerView = findViewById(R.id.recycler);
        prepareRecyclerView();
        // 5. Prepare the FirestoreRecyclerAdapter and bind it to the RecyclerView
        bindAdapterToRecycler();

        // 6. Instance the SwipeRefreshLayout
        srl = findViewById(R.id.swipeRefreshLayout);
        srl.setOnRefreshListener(this::refreshActivity);
    }

    private void prepareNavigationView() {
        setUserAccountOnHeader();
        navView.setNavigationItemSelectedListener(item -> {
            navigationItemSelected(item);
            return false;
        });
    }

    private void prepareRecyclerView() {
        recyclerView.setHasFixedSize(true);
            LinearLayoutManager llManager = new LinearLayoutManager(this);
            llManager.setReverseLayout(true);
            llManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(llManager);
    }

    private void bindAdapterToRecycler() {
        ffStore.collection("posts")
                .orderBy("date")
                .limit(100)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.e(TAG, "bindAdapterToRecycler: failed -> "+error.getMessage());
                        return;
                    }
                    Log.i(TAG, "bindAdapterToRecycler: success");
                    adapter = new PostAdapter(PostRecyclerView.this, value.toObjects(Post.class));
                    recyclerView.setAdapter(adapter);
                });
    }

    private void refreshActivity(){
        ffStore.collection("posts")
            .orderBy("date")
            .limit(100)
            .addSnapshotListener((value, error) -> {
                if (value != null){
                    adapter = new PostAdapter(getApplicationContext(), value.toObjects(Post.class));
                    recyclerView.setAdapter(adapter);
                    fillHeader();
                    srl.setRefreshing(false);
                }
            });
    }

    private void setUserAccountOnHeader() {
        View navHeaderView = navView.inflateHeaderView(R.layout.nav_header);

        tvProfileEmailHeader = navHeaderView.findViewById(R.id.tvUserEmail);
        ivProfileImageHeader = navHeaderView.findViewById(R.id.ivProfileImageHeader);

        fillHeader();

    }

    private void fillHeader() {
        if(tvProfileEmailHeader == null || ivProfileImageHeader == null){
           return;
        }
        ffStore.collection("users")
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    String imageURL = task.getResult().get("profileImage")+"";
                    String userTag = task.getResult().get("tag")+"";
                    Picasso.with(getApplicationContext()).load(imageURL).placeholder(R.drawable.account_40).into(ivProfileImageHeader);
                    tvProfileEmailHeader.setText("@"+userTag);
                }
            });
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean userAuthenticated = eu.checkUserAuthentication();
        if(!userAuthenticated){
            eu.intentLoginActivity();
        }else{
            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
            eu.checkUserInFirestore(fUser.getUid(), fUser.getEmail());
        }
    }

    @Override
    public void onBackPressed() {
        if(dwLayout.isDrawerOpen(navView)){
            dwLayout.closeDrawer(GravityCompat.END);
            return;
        }else if (System.currentTimeMillis() - timeRemaining > 1000) {
            timeRemaining = System.currentTimeMillis();
            Toast.makeText(PostRecyclerView.this,
                    getResources().getString(R.string.tap_twice_to_exit),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(getResources().getString(R.string.title_miAddPost))){
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

    private void navigationItemSelected(@NonNull MenuItem item) {
        CharSequence title = item.getTitle();
        if (getResources().getString(R.string.itPosts_title).contentEquals(title)) {
            dwLayout.closeDrawer(GravityCompat.END);
        } else if (getResources().getString(R.string.itProfile_title).contentEquals(title)) {
            Intent profile = new Intent(getApplicationContext(), Profile.class);
            startActivity(profile);
        } else if (getResources().getString(R.string.itMessages_title).contentEquals(title)) {
            Intent chatsRecycler = new Intent(getApplicationContext(), ChatRecyclerView.class);
            startActivity(chatsRecycler);
        } else if (getResources().getString(R.string.itSettings_title).contentEquals(title)) {
            Intent settings = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(settings);
            Toast.makeText(this, getResources().getString(R.string.itSettings_title), Toast.LENGTH_SHORT).show();
        } else if (getResources().getString(R.string.itLogout_title).contentEquals(title)) {
            eu.logout();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(adapter != null)
            adapter.filter(s);
        return false;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPostAuthorIcon;
        TextView tvPostAuthorName;
        TextView tvPostAuthorTag;
        ImageView ivMultimediaPost;
        TextView tvPostDescription;
        ImageView ivLike;
        ImageView ivPostSendMessage;
        TextView tvPostLikes;
        Button btnLearnMore;

        public PostViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            instanceComponents();
        }

        private void instanceComponents() {
            ivPostAuthorIcon = itemView.findViewById(R.id.ivPostAuthorIcon);
            tvPostAuthorName = itemView.findViewById(R.id.tvPostAuthorName);
            tvPostAuthorTag = itemView.findViewById(R.id.tvPostAuthorTag);
            ivMultimediaPost = itemView.findViewById(R.id.ivMultimediaPost);
            tvPostDescription = itemView.findViewById(R.id.tvPostDescription);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivPostSendMessage = itemView.findViewById(R.id.ivPostSendMessage);
            tvPostLikes = itemView.findViewById(R.id.tvPostLikes);
            btnLearnMore = itemView.findViewById(R.id.btnLearnMore);
        }
    }

}