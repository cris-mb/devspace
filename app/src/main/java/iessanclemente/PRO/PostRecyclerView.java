package iessanclemente.PRO;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PostRecyclerView extends AppCompatActivity {

    // RecyclerView components
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_activity);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        adapter = new PostAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preferences, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(getResources().getString(R.string.title_miLogout))){
            Intent logout = new Intent(getApplicationContext(), LoginRegister.class);
            startActivity(logout);
        }else if(item.getTitle().equals(getResources().getString(R.string.title_miProfile))){
            Intent profile = new Intent(getApplicationContext(), Profile.class);
            startActivity(profile);
        }else if(item.getTitle().equals(getResources().getString(R.string.title_miAddPost))){
            Intent addPost = new Intent(getApplicationContext(), AddPost.class);
            startActivity(addPost);
        }

        return super.onOptionsItemSelected(item);
    }
}