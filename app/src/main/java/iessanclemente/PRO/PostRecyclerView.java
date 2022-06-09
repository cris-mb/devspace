package iessanclemente.PRO;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import iessanclemente.PRO.adapters.PostAdapter;
import iessanclemente.PRO.chat.ChatActivity;
import iessanclemente.PRO.onboarding.EnterUtilities;

public class PostRecyclerView extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static String TAG = PostRecyclerView.class.getSimpleName();
    private PostAdapter adapter;
    private EnterUtilities eu;

    // Some visual components
    private DrawerLayout dwLayout;
    private NavigationView navView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_activity);
        eu = new EnterUtilities(PostRecyclerView.this);

        // PostRecyclerView components
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);

        dwLayout = findViewById(R.id.navigationLayoutRecycler);
        navView = findViewById(R.id.navigation_view);
        setUserAccountOnHeader();
        navView.setNavigationItemSelectedListener(item -> {
            navigationItemSelected(item);
            return false;
        });

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        adapter = new PostAdapter(this);
//        recyclerView.setAdapter(adapter);
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

    private void setUserAccountOnHeader() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        View navHeaderView = navView.inflateHeaderView(R.layout.nav_header);
        TextView tvAccount = navHeaderView.findViewById(R.id.tvUserEmail);
        tvAccount.setText(fUser.getEmail());
        // TODO : search for the user profile image
    }

    private void navigationItemSelected(@NonNull MenuItem item) {
        CharSequence title = item.getTitle();
        if (getResources().getString(R.string.itPosts_title).contentEquals(title)) {
            dwLayout.closeDrawer(GravityCompat.END);
        } else if (getResources().getString(R.string.itProfile_title).contentEquals(title)) {
            Intent profile = new Intent(getApplicationContext(), Profile.class);
            profile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(profile);
        } else if (getResources().getString(R.string.itMessages_title).contentEquals(title)) {
            Intent chat = new Intent(getApplicationContext(), ChatActivity.class);
            chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(chat);
        } else if (getResources().getString(R.string.itSettings_title).contentEquals(title)) {
            // TODO : Preferences xml
        } else if (getResources().getString(R.string.itLogout_title).contentEquals(title)) {
            // Remove last session credentials
            eu.logout();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(getResources().getString(R.string.itProfile_title))){
            Intent profile = new Intent(getApplicationContext(), Profile.class);
            startActivity(profile);
        }else if(item.getTitle().equals(getResources().getString(R.string.itSettings_title))){
            if(dwLayout.isDrawerOpen(navView))
                dwLayout.closeDrawer(GravityCompat.END);
            else
                dwLayout.openDrawer(GravityCompat.END);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filter(s);
        return false;
    }
}