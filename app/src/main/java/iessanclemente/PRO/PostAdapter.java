package iessanclemente.PRO;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import iessanclemente.PRO.model.Post;
import iessanclemente.PRO.model.User;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{

    private DatabaseOperations op;

    private List<Post> postsList;
    private List<Post> postsListBackup;
    private Context context;

    public PostAdapter(Context context) {
        this.postsList = new ArrayList<>();
        this.context = context;
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
        PostViewHolder pvh = new PostViewHolder(view);

        return pvh;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        holder.setPostId(postsList.get(position).getPostId());
        User author = getUser(postsList.get(position).getAuthor());

        if(author == null) {
            holder.tvAccountName.setText("Anonymous");
            Drawable d = context.getDrawable(R.drawable.account_36);
            holder.ivAccountIconPost.setImageDrawable(d);
        }else{
            holder.tvAccountName.setText(author.getUsername());
            holder.ivAccountIconPost.setImageURI(Uri.fromFile(new File(author.getProfileImagePath())));
        }

        holder.ivAccountMultimediaPost.setImageURI(Uri.parse(postsList.get(position).getMultimediaPath()));
        holder.tvAccountDescription.setText(postsList.get(position).getDescription());
        holder.tvAmountLikes.setText(postsList.get(position).getLikes()+" "+context.getResources().getString(R.string.text_tvAmountLikes));
        holder.btnCodeURL.setOnClickListener(view -> {
            Intent search = new Intent(Intent.ACTION_VIEW, Uri.parse(postsList.get(position).getCodeURL()));
            context.startActivity(search);
        });

    }

    private User getUser(String tagId) {
        op = new DatabaseOperations(context.getApplicationContext());
        op.sqlLiteDB = op.getWritableDatabase();

        User us = op.getUser(tagId);

        op.close();

        return us;
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
        ImageView ivAccountMultimediaPost;
        TextView tvAmountLikes;
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
