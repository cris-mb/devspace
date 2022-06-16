package iessanclemente.PRO.chat.listmessages;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import iessanclemente.PRO.R;
import iessanclemente.PRO.chat.privatechat.PrivateChatActivity;
import iessanclemente.PRO.model.Chat;
import iessanclemente.PRO.model.Message;

public class ChatAdapter extends RecyclerView.Adapter<ChatRecyclerView.ChatViewHolder> {
    private Context context;
    private FirebaseFirestore ffStore;
    private List<Chat> chatsList;

    private String currentUserUid;
    private String receiverUid;

    public ChatAdapter(Context context, List<Chat> list){
        this.context = context;
        this.chatsList = list;

        ffStore = FirebaseFirestore.getInstance();
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ChatRecyclerView.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);

        return new ChatRecyclerView.ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerView.ChatViewHolder holder, int i) {
        decideEmitterReceiver(chatsList.get(i));
        holder.itemView.setOnClickListener(chatView -> {
            Intent startPrivateChat = new Intent(context.getApplicationContext(), PrivateChatActivity.class);
            startPrivateChat.putExtra("receiverUid", receiverUid);
            context.startActivity(startPrivateChat);
        });
        setMessageHeader(holder.ivChatUserIcon, holder.tvChatUsername);
        setLastMessage(holder.ivMessageType, holder.tvLastMessage, chatsList.get(i));
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    private void decideEmitterReceiver(Chat chat) {
        if(currentUserUid.equals(chat.getUsers().get(0)))
            receiverUid = chat.getUsers().get(1);
        else
            receiverUid = chat.getUsers().get(0);
    }

    private void setMessageHeader(ImageView iv, TextView tv) {
        ffStore.collection("users")
                .document(receiverUid)
                .get().addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       Picasso.with(context).load(task.getResult().get("profileImage")+"")
                               .placeholder(context.getDrawable(R.drawable.account_40))
                               .into(iv);

                       tv.setText(task.getResult().get("username").toString());
                   }
                });
    }

    private void setLastMessage(ImageView iv, TextView tv, Chat chat) {
        ArrayList<String> reverseList = new ArrayList<>();
            reverseList.add(chat.getUsers().get(1));
            reverseList.add(chat.getUsers().get(0));

        ffStore.collection("chat")
                .whereArrayContains("users", currentUserUid)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        List<DocumentSnapshot> listObjects = task.getResult().getDocuments();
                        for (DocumentSnapshot ds:listObjects) {
                            ArrayList<String> chatUsers = (ArrayList<String>) ds.get("users");
                            if(chatUsers.contains(receiverUid)){
                                Chat currentChat = task.getResult().getDocuments().get(0).toObject(Chat.class);
                                Message lastMessage = currentChat.getMessages().get(currentChat.getMessages().size()-1);
                                tv.setText(lastMessage.getMessage());
                                if(lastMessage.getAuthor().equals(currentUserUid)) {
                                    iv.setImageDrawable(context.getDrawable(R.drawable.message_sent));
                                }else{
                                    iv.setImageDrawable(context.getDrawable(R.drawable.message_received));
                                }
                            }
                        }
                    }
                });

    }
}
