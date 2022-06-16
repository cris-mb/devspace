package iessanclemente.PRO.chat.privatechat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import iessanclemente.PRO.R;
import iessanclemente.PRO.model.Message;

public class MessagesAdapter extends RecyclerView.Adapter<PrivateChatActivity.MessageViewHolder> {

    private List<Message> messagesList;
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

    public MessagesAdapter(ArrayList<Message> messagesList) {
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public PrivateChatActivity.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout, parent, false);

        return new PrivateChatActivity.MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrivateChatActivity.MessageViewHolder holder, int i) {
        Message msg = messagesList.get(i);
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        boolean isCurrentUserMessage = currentUserUid.equals(msg.getAuthor());

        if(isCurrentUserMessage){
            holder.lyReceiver.setVisibility(View.GONE);
            holder.lyEmitter.setVisibility(View.VISIBLE);

            holder.tvEmitterMessageContent.setText(messagesList.get(i).getMessage());
            holder.tvEmitterMessageTime.setText(sdf.format(messagesList.get(i).getTime().toDate()));
        }else{
            holder.lyEmitter.setVisibility(View.GONE);
            holder.lyReceiver.setVisibility(View.VISIBLE);

            holder.tvReceiverMessageContent.setText(messagesList.get(i).getMessage());
            holder.tvReceiverMessageTime.setText(sdf.format(messagesList.get(i).getTime().toDate()));
        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
