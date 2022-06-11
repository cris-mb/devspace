package iessanclemente.PRO;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import iessanclemente.PRO.onboarding.EnterUtilities;

public class AddPostActivity extends Activity {

    private static final String TAG = AddPostActivity.class.getSimpleName();
    private static final int CHOOSE_MULTIMEDIA_IMAGE = 101;

    private EnterUtilities eu;
    private FirebaseFirestore ffStore;

    private ImageView ivMultimedia;
    private EditText etDescription;
    private EditText etURL;

    private byte[] selectedImageData;
    private Dialog pDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post_dialog);

        eu = new EnterUtilities(getApplicationContext());
        ffStore = FirebaseFirestore.getInstance();

        ivMultimedia = findViewById(R.id.ivAddPostMultimedia);
        ivMultimedia.setOnLongClickListener(v -> {
            openFileChooser();
            return false;
        });

        etDescription = findViewById(R.id.etAddPostDescription);
        etURL = findViewById(R.id.etAddPostURL);

        Button btnAddPost = findViewById(R.id.btnAddPost);
        btnAddPost.setOnClickListener(v ->{
            if(allFieldsCovered()){
                uploadPostImage();
            }else{
                Toast.makeText(AddPostActivity.this, getResources().getString(R.string.err_IncompleteRegistration), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean allFieldsCovered() {
        if(ivMultimedia.getDrawable().equals(getResources().getDrawable(R.drawable.image_search))
        || selectedImageData == null)
            return false;
        else if((etDescription.getText()+"").equals("")) {
            return false;
        }else return !(etURL.getText() + "").equals("");
    }

    private void uploadPostImage() {
        pDialog = new Dialog(AddPostActivity.this);
        pDialog.setContentView(R.layout.loading_view);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        StorageMetadata stMeta = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        String newProfileImagePath = "post_images/"+eu.generateTimeStamp()+".jpeg";

        FirebaseStorage.getInstance().getReference().child(newProfileImagePath).putBytes(selectedImageData, stMeta).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                String multimediaReference = task.getResult().getStorage().getDownloadUrl().toString();
                uploadPost(multimediaReference);
            }else {
                Log.d(TAG, "uploadProfileImage: failed -> " + task.getException());
            }
        });
    }

    private void uploadPost(String multimediaReference) {
        HashMap<String, Object> postData = new HashMap<>();
        postData.put("multimedia", multimediaReference);
        postData.put("description", etDescription.getText()+"");
        postData.put("url", etURL.getText()+"");
        postData.put("likes", 0);
        postData.put("author", FirebaseAuth.getInstance().getCurrentUser().getUid());
        postData.put("date", Timestamp.now());

        ffStore.collection("posts")
                .add(postData)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                        Log.d(TAG, "uploadPost: success");
                    else
                        Log.d(TAG, "uploadPost: failed -> "+task.getException().getMessage());
                    pDialog.dismiss();
                    finish();
                });
    }

    public void openFileChooser(){
        Intent chooser = new Intent(Intent.ACTION_PICK);
        chooser.setType("image/*");
        startActivityForResult(chooser, CHOOSE_MULTIMEDIA_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_MULTIMEDIA_IMAGE && resultCode==RESULT_OK){
            if(data != null){
                ivMultimedia.setImageURI(data.getData());

                Bitmap bitmap = ((BitmapDrawable) ivMultimedia.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                selectedImageData = baos.toByteArray();
            }
        }
    }

}
