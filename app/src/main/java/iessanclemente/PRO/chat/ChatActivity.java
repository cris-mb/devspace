package iessanclemente.PRO.chat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import iessanclemente.PRO.R;

public class ChatActivity extends AppCompatActivity {

    private Toolbar chatToolbar;

    private RecyclerView rvMessages;
    private ImageView ivTakePhoto, ivSendMessage;
    private EditText etMessage;

    private CircleImageView ivReceiverIcon;
    private TextView tvReceiverName;

    public ChatActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        initializeComponents();
//        setToolBar();

    }

    private void initializeComponents() {
        rvMessages = findViewById(R.id.rvMessages);
        ivTakePhoto = findViewById(R.id.ivTakePhoto);
        ivSendMessage = findViewById(R.id.ivSendMessage);
        etMessage = findViewById(R.id.etMessage);
    }

    private void setToolBar() {
        chatToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar == null)return;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setCustomView(R.layout.chat_toolbar);
    }
}
