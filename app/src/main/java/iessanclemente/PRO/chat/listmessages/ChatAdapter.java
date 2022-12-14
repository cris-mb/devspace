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

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private String multimediaRef;

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

        return new ChatRecyclerView.ChatViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerView.ChatViewHolder holder, int i) {
        decideEmitterReceiver(chatsList.get(i));
        setMessageHeader(holder.ivChatUserIcon, holder.tvChatUsername, holder.tvChatUserTag);
        setMultimediaOnHeader(holder.ivChatPostMultimedia, holder.tvChatPostDate, chatsList.get(i));
        holder.itemView.setOnClickListener(chatView -> {
            Intent startPrivateChat = new Intent(context.getApplicationContext(), PrivateChatActivity.class);
            startPrivateChat.putExtra("receiverTag", holder.tvChatUserTag.getText()+"");
            startPrivateChat.putExtra("postUid", chatsList.get(i).getPostUid());
            context.startActivity(startPrivateChat);
        });
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

    private void setMessageHeader(ImageView iv, TextView tvUsername, TextView tvTag) {
        ffStore.collection("users")
                .document(receiverUid)
                .get().addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       Picasso.with(context).load(task.getResult().get("profileImage")+"")
                               .placeholder(context.getDrawable(R.drawable.account_40))
                               .into(iv);

                       tvUsername.setText(task.getResult().get("username")+"");
                       tvTag.setText(task.getResult().get("tag")+"");
                   }
                });
    }

    private void setMultimediaOnHeader(ImageView iv, TextView tv, Chat chat) {
        ffStore.collection("posts")
            .document(chat.getPostUid())
            .get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    multimediaRef = task.getResult().get("multimedia")+"";
                    Picasso.with(context).load(multimediaRef)
                            .placeholder(context.getDrawable(R.drawable.image_not_found))
                            .into(iv);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                    Date date = ((Timestamp) task.getResult().get("date")).toDate();
                    tv.setText(sdf.format(date));
                }
            });
    }

    private void setLastMessage(ImageView iv, TextView tv, Chat chat) {
        if(chat != null && chat.getMessages() != null){
            Message lastMessage = chat.getMessages().get(chat.getMessages().size()-1);
            tv.setText(lastMessage.getMessage());
            if(lastMessage.getAuthor().equals(currentUserUid)) {
                iv.setImageDrawable(context.getDrawable(R.drawable.message_sent));
            }else{
                iv.setImageDrawable(context.getDrawable(R.drawable.message_received));
            }
        }
    }
}
