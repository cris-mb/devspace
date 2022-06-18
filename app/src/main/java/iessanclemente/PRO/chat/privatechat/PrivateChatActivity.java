package iessanclemente.PRO.chat.privatechat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import iessanclemente.PRO.R;
import iessanclemente.PRO.chat.listmessages.ChatRecyclerView;
import iessanclemente.PRO.model.Message;
import kotlin.text.UStringsKt;

public class PrivateChatActivity extends AppCompatActivity {

    private static final String TAG = PrivateChatActivity.class.getSimpleName();
    private Toolbar chatToolbar;

    private RecyclerView rvMessages;
    private ImageView ivSendMessage;
    private EditText etMessage;
    private CircleImageView ivReceiverIcon;
    private TextView tvReceiverName;
    private Dialog pDialog;

    private FirebaseFirestore ffStore;
    private String currentUserUid;
    private String receiverUid;
    private String chatUidReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        setToolBar();
        ffStore = FirebaseFirestore.getInstance();
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        initializeComponents();

        initProgressDialog();
        fetchUserByTag(getIntent().getStringExtra("receiverTag"));

        prepareRecyclerView();

        ivSendMessage = findViewById(R.id.ivSendMessage);
        ivSendMessage.setOnClickListener(v -> {
            if(chatUidReference != null){
                sendMessage(chatUidReference);
                etMessage.setText("");
                displayMessages(chatUidReference);
            }
        });
    }

    private void setToolBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar == null)return;
        actionBar.setCustomView(R.layout.chat_toolbar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        /*chatToolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(chatToolbar);*/
    }

    private void initializeComponents() {
        ivReceiverIcon = findViewById(R.id.ivReceiverIcon);
        tvReceiverName = findViewById(R.id.tvReceiverName);

        rvMessages = findViewById(R.id.rvMessages);
            rvMessages.setHasFixedSize(true);
            LinearLayoutManager llManager = new LinearLayoutManager(this);
                llManager.setReverseLayout(true);
                llManager.setStackFromEnd(false);
                rvMessages.setLayoutManager(llManager);
        etMessage = findViewById(R.id.etMessage);
        ivSendMessage = findViewById(R.id.ivSendMessage);
    }

    private void initProgressDialog() {
        pDialog = new Dialog(PrivateChatActivity.this);
        pDialog.setContentView(R.layout.loading_view);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
    }

    private void fetchUserByTag(String receiverTag) {
        ffStore.collection("users")
                .whereEqualTo("tag", receiverTag)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.i(TAG, "fetchUserByTag: failed -> "+error.getMessage());
                        return;
                    }

                    receiverUid = value.getDocuments().get(0).getId();
                    setReceiverOnToolbar(receiverUid);
                });
    }

    private void setReceiverOnToolbar(String receiverUid) {
        ffStore.collection("users")
                .document(receiverUid)
                .get().addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       Picasso.with(PrivateChatActivity.this)
                               .load(task.getResult().get("profileImage")+"")
                               .placeholder(getDrawable(R.drawable.account_125))
                               .into(ivReceiverIcon);

                       tvReceiverName.setText(task.getResult().get("username")+"");
                       prepareMessagesRecyclerView();
                   }
                });
    }

    private void prepareMessagesRecyclerView() {
        ffStore.collection("chats")
                .whereArrayContains("users", currentUserUid)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        for (DocumentSnapshot ds:task.getResult().getDocuments()) {
                            ArrayList<String> chatUsers = (ArrayList<String>) ds.get("users");

                            if(chatUsers != null && chatUsers.contains(receiverUid)){
                                pDialog.dismiss();
                                chatUidReference = ds.getId();
                                displayMessages(chatUidReference);
                                setChatMessagesListener();
                                return;
                            }
                        }
                    }else {
                        pDialog.dismiss();
                        showFirstMessageDialog();
                        Log.i(TAG, "prepareMessagesRecyclerView: new chat created");
                    }
                });
    }

    private void displayMessages(String chatUid) {
        Log.i(TAG, "displayMessages: chatUid("+chatUid+")");
        ffStore.collection("chats")
                .document(chatUid)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()
                            && task.getResult().get("messages") != null
                            && !((ArrayList<Object>) task.getResult().get("messages")).isEmpty()){
                        ArrayList<HashMap<String, Object>> messagesList = (ArrayList<HashMap<String, Object>>) task.getResult().get("messages");
                        bindAdapterToRecycler(toMessageArrayList(messagesList));
                    }
                });
    }

    private void bindAdapterToRecycler(ArrayList<Message> messagesList) {
        MessagesAdapter adapter = new MessagesAdapter(messagesList);
        rvMessages.setAdapter(adapter);
    }

    private void setChatMessagesListener() {
        ffStore.collection("chats")
                .whereEqualTo("uid", chatUidReference)
                .limit(100)
                .addSnapshotListener((value, error) -> {
                    if(error == null){
                        Log.d(TAG, "Changes detected "+!value.getDocumentChanges().isEmpty());
                        if(value != null){
                            ArrayList<HashMap<String, Object>> messagesList = new ArrayList<>();
                            if(!value.isEmpty()
                                    && value.getDocuments().get(0) != null
                                    && value.getDocuments().get(0).get("messages") != null){
                                messagesList =
                                        (ArrayList<HashMap<String, Object>>) value.getDocuments().get(0).get("messages");
                                Log.d(TAG, "messagesList retrieved -> "+messagesList);
                            }
                            MessagesAdapter adapter = new MessagesAdapter(toMessageArrayList(messagesList));
                            rvMessages.setAdapter(adapter);
                            rvMessages.smoothScrollToPosition(adapter.getItemCount());
                        }
                    }
                });
    }

    private ArrayList<Message> toMessageArrayList(ArrayList<HashMap<String, Object>> list) {
        ArrayList<Message> messagesList = new ArrayList<>();
        for (HashMap<String, Object> map:list){
            messagesList.add(new Message(map));
        }

        return messagesList;
    }

    public void showFirstMessageDialog(){
        AlertDialog typeFirstMessage = new AlertDialog.Builder(PrivateChatActivity.this)
                .setTitle(getResources().getString(R.string.title_typeFirstMessage))
                .setMessage(getResources().getString(R.string.message_typeFirstMessage))
                .setIcon(getDrawable(R.drawable.start_chatting_icon))
                .create();

        typeFirstMessage.show();
        createNewChat();
    }

    private void createNewChat(){
        DocumentReference dr = ffStore.collection("chats").document();
        chatUidReference = dr.getId();

        ArrayList<String> chatUsers = new ArrayList<>();
            chatUsers.add(currentUserUid);
            chatUsers.add(receiverUid);

        HashMap<String, Object> chatData = new HashMap<>();
        chatData.put("uid", chatUidReference);
        chatData.put("date", Timestamp.now());
        chatData.put("users", chatUsers);

        dr.set(chatData).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                setChatMessagesListener();
            }
        });
    }

    private void sendMessage(String chatUidReference) {
        Message chatMessage = new Message(currentUserUid, etMessage.getText()+"", Timestamp.now());

        ffStore.collection("chats")
                .document(chatUidReference)
                .update("messages", FieldValue.arrayUnion(chatMessage))
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       Log.i(TAG, "sendMessage: sent");
                   }
                });
    }

    private void prepareRecyclerView() {
        rvMessages.setHasFixedSize(true);
        LinearLayoutManager llManager = new LinearLayoutManager(this);
            llManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(llManager);
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
        Intent chatList = new Intent(getApplicationContext(), ChatRecyclerView.class);
        startActivity(chatList);
        super.onBackPressed();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        LinearLayout lyEmitter;
        TextView tvEmitterMessageContent;
        TextView tvEmitterMessageTime;

        LinearLayout lyReceiver;
        TextView tvReceiverMessageContent;
        TextView tvReceiverMessageTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            lyEmitter = itemView.findViewById(R.id.lyEmitter);
            tvEmitterMessageContent = itemView.findViewById(R.id.tvEmitterMessageContent);
            tvEmitterMessageTime = itemView.findViewById(R.id.tvEmitterMessageTime);

            lyReceiver = itemView.findViewById(R.id.lyReceiver);
            tvReceiverMessageContent = itemView.findViewById(R.id.tvReceiverMessageContent);
            tvReceiverMessageTime = itemView.findViewById(R.id.tvReceiverMessageTime);
        }
    }
}
