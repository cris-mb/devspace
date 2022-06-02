package iessanclemente.PRO;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import iessanclemente.PRO.adapters.PostAdapter;
import iessanclemente.PRO.chat.ChatActivity;

public class PostRecyclerView extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private PostAdapter adapter;
    private DatabaseOperations op;

    // Some visual components
    private DrawerLayout dwLayout;
    private NavigationView navView;

    private String currUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_activity);
        op = new DatabaseOperations(PostRecyclerView.this);
        currUserUid = getIntent().getStringExtra("currUserUid");

        // PostRecyclerView components
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);

        dwLayout = findViewById(R.id.navigation_layout_recycler);
        navView = findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(item -> {
            navigationItemSelected(item);
            return false;
        });

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PostAdapter(this);
        recyclerView.setAdapter(adapter);
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
            Intent chat = new Intent(getApplicationContext(), ChatActivity.class);
            startActivity(chat);
        } else if (getResources().getString(R.string.itSettings_title).contentEquals(title)) {
            // TODO : Preferences xml
        } else if (getResources().getString(R.string.itLogout_title).contentEquals(title)) {
            // Remove last session credentials
            op.logout();
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
            profile.putExtra("currUserUid", currUserUid);
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