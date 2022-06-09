package iessanclemente.PRO.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import iessanclemente.PRO.DatabaseOperations;
import iessanclemente.PRO.R;
import iessanclemente.PRO.model.Post;
import iessanclemente.PRO.model.User;
import iessanclemente.PRO.onboarding.EnterUtilities;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{

    private EnterUtilities eu;
    private DatabaseOperations op;

    private List<Post> postsList;
    private List<Post> postsListBackup;
    private final Context context;

    public PostAdapter(Context context) {
        this.postsList = new ArrayList<>();
        this.context = context;

        eu = new EnterUtilities(context.getApplicationContext());
        loadPostsFromDatabase();
    }

    private void loadPostsFromDatabase() {
        op = new DatabaseOperations(context.getApplicationContext());
        op.sqlLiteDB = op.getWritableDatabase();

        postsList = op.postsList();
        postsListBackup = new ArrayList<>();
        postsListBackup.addAll(postsList);

        op.close();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card, parent, false);

        return new PostViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        holder.setPostId(postsList.get(position).getPostId());
        User author = getCurrentUser(postsList.get(position).getAuthor());

        holder.ivAccountMultimediaPost.setImageURI(Uri.parse(postsList.get(position).getMultimediaPath()));
        holder.tvAccountDescription.setText(postsList.get(position).getDescription());
        holder.tvAmountLikes.setText(postsList.get(position).getLikes()+" "+context.getResources().getString(R.string.text_tvAmountLikes));
        holder.btnCodeURL.setOnClickListener(view -> {
            Intent search = new Intent(Intent.ACTION_VIEW, Uri.parse(postsList.get(position).getCodeURL()));
            context.startActivity(search);
        });

    }

    private User getCurrentUser(String tagId) {
        return new User();
    }

    public void filter(final String pattern){
        int ptLength = pattern.length();

        if(ptLength == 0){
            postsList.clear();
            postsList.addAll(postsListBackup);
        }else{
            postsList.clear();

            for (Post p:postsListBackup) {
                String postDescription = p.getDescription().toLowerCase();
                if(postDescription.contains(pattern.toLowerCase())){
                    postsList.add(p);
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAccountIconPost;
        TextView tvAccountName;
        ImageView ivLike;
        ImageView ivComments;
        ImageView ivAccountMultimediaPost;
        TextView tvAmountLikes;
        ExpandableListView lvComments;
        TextView tvAccountDescription;
        Button btnCodeURL;

        boolean postLiked = false;
        private final ThreadBridge bridge = new ThreadBridge(this);
        int postId = 0;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAccountIconPost = itemView.findViewById(R.id.ivAccountIconPost);
            tvAccountName = itemView.findViewById(R.id.tvAccountNamePost);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivLike.setOnClickListener(view -> {
                new Thread() {
                    @Override
                    public void run() {
                        op = new DatabaseOperations(context.getApplicationContext());
                        op.sqlLiteDB = op.getWritableDatabase();
                        int addSubtractLike;

                        if(!postLiked){
                            ivLike.setImageDrawable(view.getResources().getDrawable(R.drawable.thumb_up_dkgrey_24, view.getContext().getTheme()));
                            addSubtractLike = 1;
                            postLiked = true;
                        }else{
                            ivLike.setImageDrawable(view.getResources().getDrawable(R.drawable.thumb_up_white_24, view.getContext().getTheme()));
                            addSubtractLike = -1;
                            postLiked = false;
                        }

                        op.updateLikes(postId, addSubtractLike);

                        Message msg = new Message();
                        msg.arg1 = 1;
                        bridge.sendMessage(msg);

                        op.close();
                    }
                }.start();
            });
            ivComments = itemView.findViewById(R.id.ivComments);
            lvComments = itemView.findViewById(R.id.lvComments);
            ivComments.setOnClickListener(view -> {
                if(lvComments.isShown())
                    lvComments.setVisibility(ExpandableListView.GONE);
                else
                    lvComments.setVisibility(ExpandableListView.VISIBLE);
            });
            ivAccountMultimediaPost = itemView.findViewById(R.id.ivAccountMultimediaPost);
            tvAmountLikes = itemView.findViewById(R.id.tvAmountLikes);
            tvAccountDescription = itemView.findViewById(R.id.tvAccountDescriptionPost);
            btnCodeURL = itemView.findViewById(R.id.btnLearnMore);
        }

        public void setPostId(int postId) {
            this.postId = postId;
        }

        public void updateAmountLikes(){
            op = new DatabaseOperations(context.getApplicationContext());
            op.sqlLiteDB = op.getWritableDatabase();

            Cursor cur = op.sqlLiteDB.rawQuery("SELECT likes FROM post WHERE postId=?", new String[]{String.valueOf(postId)});
            int likes = 0;
            if(cur.moveToFirst()){
                likes = cur.getInt(0);
            }
            tvAmountLikes.setText(likes+" "+context.getResources().getString(R.string.text_tvAmountLikes));

            op.close();
        }
    }

    private static class ThreadBridge extends Handler {

        private final WeakReference<PostViewHolder> target;

        ThreadBridge(PostViewHolder target){
            this.target = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            PostViewHolder parent = target.get();
            if(msg.arg1 == 1){
                parent.updateAmountLikes();
            }
        }
    }
}
