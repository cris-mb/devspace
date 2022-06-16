package iessanclemente.PRO.recycler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import iessanclemente.PRO.R;
import iessanclemente.PRO.chat.privatechat.PrivateChatActivity;
import iessanclemente.PRO.model.Post;

public class PostAdapter extends RecyclerView.Adapter<PostRecyclerView.PostViewHolder> {

    private static final String TAG = PostAdapter.class.getSimpleName();
    private final Context context;

    private List<Post> postsList;
    private List<Post> postsListBackup;
    private final FirebaseFirestore ffStore;
    private final String currentUserUid;

    private boolean userDidLikeCurrentPost = false;

    public PostAdapter(Context context, List<Post> list){
        this.context = context;
        postsList = list;
        postsListBackup = new ArrayList<>();
        postsListBackup.addAll(postsList);

        ffStore = FirebaseFirestore.getInstance();
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public PostRecyclerView.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card, parent, false);

        return new PostRecyclerView.PostViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull PostRecyclerView.PostViewHolder holder, int i) {
        setPostHeader(holder, postsList.get(i).getAuthor());

        setPostMultimedia(holder, postsList.get(i).getMultimedia());
        holder.tvPostDescription.setText(postsList.get(i).getDescription());

        userHasLikedPost(holder.ivLike, postsList.get(i).getUid());
        holder.ivLike.setOnClickListener(view -> { handleUserAction(holder, postsList.get(i).getUid());});
        setPostLikes(postsList.get(i).getUid(), holder.tvPostLikes);

        holder.ivPostSendMessage.setOnClickListener(v -> {
            Intent sendNewMessage = new Intent(context.getApplicationContext(), PrivateChatActivity.class);
            sendNewMessage.putExtra("receiverTag", holder.tvPostAuthorTag.getText()+"");
            sendNewMessage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(sendNewMessage);
        });

        holder.btnLearnMore.setOnClickListener(v -> {
            Intent googleSearch = new Intent(Intent.ACTION_VIEW, Uri.parse(postsList.get(i).getUrl()));
            try{
                context.startActivity(googleSearch);
            }catch(Exception e){
                Toast advice = Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.malformed_URL), Toast.LENGTH_LONG);
                    advice.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 180);
                    advice.show();
            }
        });
    }

    private void setPostHeader(PostRecyclerView.PostViewHolder holder, String authorUid) {
        ffStore.collection("users")
                .document(authorUid)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        String profileImage = task.getResult().get("profileImage")+"";
                        Picasso.with(context).load(profileImage).placeholder(R.drawable.account_40).into(holder.ivPostAuthorIcon);
                        holder.tvPostAuthorName.setText(task.getResult().get("username").toString());
                        holder.tvPostAuthorTag.setText(task.getResult().get("tag").toString());
                        if(authorUid.equals(currentUserUid)){
                            holder.ivPostSendMessage.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void setPostMultimedia(PostRecyclerView.PostViewHolder holder, String multimediaUrl) {
        if(multimediaUrl != null){
            Picasso.with(context).load(multimediaUrl).into(holder.ivMultimediaPost);
        }else
            holder.ivMultimediaPost.setImageDrawable(context.getDrawable(R.drawable.image_not_found));

    }

    private void setPostLikes(String postUid, TextView tvPostLikes) {
        ffStore.collection("likes")
                .document(postUid)
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<String> postUsersLikes = ((ArrayList<String>)task.getResult().get("users"));
                    if(task.isSuccessful() && postUsersLikes != null){
                        String likesAmount = postUsersLikes.size()+" Likes";
                        tvPostLikes.setText(likesAmount);
                    }else
                        tvPostLikes.setText("0 Likes");
                });
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    private void handleUserAction(PostRecyclerView.PostViewHolder holder, String postUid) {
        if(userDidLikeCurrentPost){
            holder.ivLike.setImageDrawable(context.getDrawable(R.drawable.thumb_up_white_24));
            ffStore.collection("likes")
                    .document(postUid)
                    .update("users", FieldValue.arrayRemove(currentUserUid))
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            setPostLikes(postUid, holder.tvPostLikes);
                            userDidLikeCurrentPost = false;
                            Log.d(TAG, "userHasLikedPost: like removed");
                        }else
                            Log.d(TAG, "userHasLikedPost: failed -> "+task.getException().getMessage());
                    });
        }else{
            holder.ivLike.setImageDrawable(context.getDrawable(R.drawable.thumb_up_dkgrey_24));
            ffStore.collection("likes")
                    .document(postUid)
                    .update("users", FieldValue.arrayUnion(currentUserUid))
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            setPostLikes(postUid, holder.tvPostLikes);
                            userDidLikeCurrentPost = true;
                            Log.d(TAG, "userHasLikedPost: like added");
                        }else
                            Log.d(TAG, "userHasLikedPost: failed -> "+task.getException().getMessage());
                    });
        }
    }

    private void userHasLikedPost(ImageView iv, String postUid) {
        ffStore.collection("likes")
                .document(postUid)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        ArrayList<String> postLikesArray = (ArrayList<String>) task.getResult().get("users");
                        if (postLikesArray != null && postLikesArray.contains(currentUserUid)){
                            userDidLikeCurrentPost = true;
                            iv.setImageDrawable(context.getDrawable(R.drawable.thumb_up_dkgrey_24));
                        }else
                            iv.setImageDrawable(context.getDrawable(R.drawable.thumb_up_white_24));
                    }else
                        Log.d(TAG, "userHasLikedPost: failed -> "+task.getException().getMessage());
                });
    }

    public void filter(final String pattern){
        int ptLength = pattern.length();

        postsList.clear();
        if(ptLength == 0){
            postsList.addAll(postsListBackup);
        }else{
            for (Post p:postsListBackup) {
                String postDescription = p.getDescription().toLowerCase();
                if(postDescription.contains(pattern.toLowerCase())){
                    postsList.add(p);
                }
            }
        }

        notifyItemRangeChanged(0, postsList.size());
    }

}
