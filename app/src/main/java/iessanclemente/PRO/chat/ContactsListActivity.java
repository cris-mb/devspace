package iessanclemente.PRO.chat;

import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import iessanclemente.PRO.R;

public class ContactsListActivity extends AppCompatActivity {

    private static final String TAG = ContactsListActivity.class.getSimpleName();
    private ContactsContract contactsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_list);

        RecyclerView contactsRecycler = findViewById(R.id.contactsRecycler);
        contactsRecycler.setHasFixedSize(true);
        contactsRecycler.setLayoutManager(new LinearLayoutManager(this));
        contactsRecycler.setAdapter(new ContactAdapter(ContactsListActivity.this));

    }
}
