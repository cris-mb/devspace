package iessanclemente.PRO;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.File;
import java.sql.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import iessanclemente.PRO.model.Post;

public class AddPost extends Activity {

    private static final int FILE_CHOOSER = 1;

    private DatabaseOperations op;
    private String postImagePath;
    private String postImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post_activity);

        Button btnPostFileChooser = findViewById(R.id.btnPostFileChooser);
        btnPostFileChooser.setOnClickListener(view -> {
            openFileChooser();
        });
        EditText etPostCodeURL = findViewById(R.id.etPostCodeURL);
        EditText etDescription = findViewById(R.id.etPostDescription);

        Button btnPostSave = findViewById(R.id.btnPostSave);
        btnPostSave.setOnClickListener(view -> {
            new Thread(){
                @Override
                public void run() {
                    op = new DatabaseOperations(getApplicationContext());
                    op.sqlLiteDB = op.getWritableDatabase();

                    String currentUserTagId = getCurrentUser();
                    op.addPost(new Post(currentUserTagId, postImagePath,etDescription.getText()+"",0, new Date(new java.util.Date().getTime()), etPostCodeURL.getText()+""));

                    op.close();
                }

            }.start();
            Intent recycler = new Intent(getApplicationContext(), PostRecyclerView.class);
            startActivity(recycler);
        });

        Button btnPostCancel = findViewById(R.id.btnPostCancel);
        btnPostCancel.setOnClickListener(view -> {
            finish();
        });
    }

    private String getCurrentUser() {
        String tagId = "";

        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc=db.parse(new File(getFilesDir()+File.separator+"current_user.xml"));
            doc.getDocumentElement().normalize();

            NodeList el = doc.getElementsByTagName("tagId");
            tagId = el.item(0).getTextContent();

        }catch(Exception e){
            e.printStackTrace();
        }

        return tagId;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FILE_CHOOSER && resultCode == Activity.RESULT_OK){
            if(data == null)return;

            Uri path = data.getData();
            postImagePath = path.getPath();
            Log.i("PATH", "The path selected was "+postImagePath);
        }
    }

    public void openFileChooser(){
        Intent chooser = new Intent(Intent.ACTION_PICK);
        chooser.setType("*/*");
        startActivityForResult(chooser, FILE_CHOOSER);
    }
}
