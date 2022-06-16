package iessanclemente.PRO.chat.privatechat;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import iessanclemente.PRO.R;
import iessanclemente.PRO.model.Message;

public class MessagesAdapter extends RecyclerView.Adapter<PrivateChatActivity.MessageViewHolder> {

    private List<Message> messagesList;
    private Context context;

    public MessagesAdapter(Context context, ArrayList<Message> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public PrivateChatActivity.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent_layout, parent, false);

        return new PrivateChatActivity.MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrivateChatActivity.MessageViewHolder holder, int i) {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boolean isMessageSent = messagesList.get(i).getAuthor().equals(currentUserUid);

//        if(isMessageSent){
//            holder.messageContainer.;
//        }else{
//
//        }
        holder.tvMessageContent.setText(messagesList.get(i).getMessage());
        holder.tvMessageTime.setText(messagesList.get(i).getTime().toString());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
