package iessanclemente.PRO.chat.listmessages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import iessanclemente.PRO.R;
import iessanclemente.PRO.model.Chat;
import iessanclemente.PRO.recycler.PostRecyclerView;


public class ChatRecyclerView extends AppCompatActivity {

    private static final String TAG = ChatRecyclerView.class.getSimpleName();

    private FirebaseFirestore ffStore;
    private String userUid;

    private RecyclerView recyclerMessages;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_messages_activity);
        ffStore = FirebaseFirestore.getInstance();
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerMessages = findViewById(R.id.recyclerMessages);
        prepareRecyclerView();
        bindAdapterToRecycler();
    }

    private void prepareRecyclerView() {
        recyclerMessages.setHasFixedSize(true);
        LinearLayoutManager llManager = new LinearLayoutManager(this);
            llManager.setReverseLayout(true);
            llManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(llManager);
    }

    private void bindAdapterToRecycler() {
        ffStore.collection("chat")
                .whereArrayContains("users", userUid)
                .limit(100)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.e(TAG, "bindAdapterToRecycler: failed -> "+error.getMessage());
                        return;
                    }
                    Log.i(TAG, "bindAdapterToRecycler: success("+value.getDocuments()+")");
                    adapter = new ChatAdapter(ChatRecyclerView.this, value.toObjects(Chat.class));
                    recyclerMessages.setAdapter(adapter);
                });
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        ImageView ivChatUserIcon;
        TextView tvChatUsername;
        ImageView ivMessageType;
        TextView tvLastMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChatUserIcon = itemView.findViewById(R.id.ivChatUserIcon);
            tvChatUsername = itemView.findViewById(R.id.tvChatUsername);
            ivMessageType = itemView.findViewById(R.id.ivMessageType);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent chatList = new Intent(getApplicationContext(), PostRecyclerView.class);
        startActivity(chatList);
        super.onBackPressed();
    }

}
