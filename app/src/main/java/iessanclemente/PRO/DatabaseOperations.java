package iessanclemente.PRO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import iessanclemente.PRO.model.Post;
import iessanclemente.PRO.model.User;

public class DatabaseOperations extends SQLiteOpenHelper{

    public SQLiteDatabase sqlLiteDB;
    public final static String BD_NAME="DEVSPACE";
    public final static int VERSION_BD = 1;

    private static final String TAG = DatabaseOperations.class.getSimpleName();
    private final Context context;

    private final StorageReference stRef;
    private final DatabaseReference usersRef, postsRef;
    private FirebaseAuth auth;

    private String downloadUrl;
    private String currUserUid;


    public DatabaseOperations(Context context) {
        super(context, BD_NAME, null, VERSION_BD);
        this.context = context;
        stRef = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");

        auth = FirebaseAuth.getInstance();
        currUserUid = auth.getCurrentUser().getUid();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void tablesCreation(){
        sqlLiteDB.execSQL("create table user(tagId TEXT primary key NOT NULL, username TEXT NOT NULL, profileImagePath TEXT, email TEXT NOT NULL, password TEXT NOT NULL, about TEXT);");
        sqlLiteDB.execSQL("create table post(postId INTEGER primary key AUTOINCREMENT NOT NULL, tagId_FK TEXT NOT NULL, multimediaPath TEXT NOT NULL, description TEXT, likes INTEGER, date TEXT, codeURL TEXT, foreign key (tagId_FK) references user(tagId));");
        sqlLiteDB.execSQL("create table comment(commentId INTEGER primary key AUTOINCREMENT NOT NULL, postId_FK INTEGER NOT NULL, tagId_FK TEXT NOT NULL, message TEXT NOT NULL, date TEXT NOT NULL, foreign key (postId_FK) references post(postId), foreign key (tagId_FK) references user(tagId));");

        sqlLiteDB.execSQL("create  table follows(tagId_Followed TEXT NOT NULL, tagId_Follower TEXT NOT NULL, foreign key (tagId_Followed) references user(tagId), foreign key (tagId_Follower) references user(tagId));");
        sqlLiteDB.execSQL("create  table describes(commentId_Comments INTEGER NOT NULL, commentId_Commented INTEGER NOT NULL, foreign key (commentId_Comments) references comment(commentId), foreign key (commentId_Commented) references comment(commentId));");
    }

    public void addAllPost(){
        sqlLiteDB.execSQL("INSERT INTO user(tagId, username, profileImagePath, email, password, about) values(\"mariaa5123\", \"Maria\", \"/data/data/iessanclemente.PRO/files/profileImages/Maria.jpg\", \"mariagutiérrez@gmail.com\", \"88888888*\", \"I'm a junior system administrator.\");");
        sqlLiteDB.execSQL("INSERT INTO user(tagId, username, profileImagePath, email, password, about) values(\"anntiia4421\", \"Antia\", \"/data/data/iessanclemente.PRO/files/profileImages/Antia.jpg\", \"antiaformoso@gmail.com\", \".321cba\", \"I'm a senior web developer.\");");
        sqlLiteDB.execSQL("INSERT INTO user(tagId, username, profileImagePath, email, password, about) values(\"aalberrto1123\", \"Alberto\", \"/data/data/iessanclemente.PRO/files/profileImages/Alberto.jpg\", \"albertocaamaño@gmail.com\", \"abc123.\", \"I'm a recently graduated multi-platform developer. I learnt different programming languages such as Python, Java and Kotlin. Also I often use other tecnologies like Docker, Git, Odoo, HTML, CSS, MySQL, MongoDB, ...\");");

        sqlLiteDB.execSQL("INSERT INTO post(tagId_FK, multimediaPath, description, likes, date, codeURL) values(\"aalberrto1123\", \"/data/data/iessanclemente.PRO/files/postsImages/copilot.jpeg\", \"GitHub Copilot works with a broad set of frameworks and languages. The technical preview does especially well for Python, JavaScript, TypeScript, Ruby, Java, and Go, but it understands dozens of languages and can help you find your way around almost anything.\", 662, DATE(), \"https://copilot.github.com/\");");
        sqlLiteDB.execSQL("INSERT INTO post(tagId_FK, multimediaPath, description, likes, date, codeURL) values(\"mariaa5123\", \"/data/data/iessanclemente.PRO/files/postsImages/toplanguages.jpg\", \"This top was made out of the 2021 StackOverflow poll. The results reflects that the most used ones are web-oriented\", 299, DATE(), \"https://www.stackscale.com/es/blog/lenguajes-programacion-populares-2021/\");");
        sqlLiteDB.execSQL("INSERT INTO post(tagId_FK, multimediaPath, description, likes, date, codeURL) values(\"anntiia4421\", \"/data/data/iessanclemente.PRO/files/postsImages/code.png\", \"This is my own solution of AceptaElReto315. This challenge was about building a minesweeper and I solved it with a recursive method that checks all the cells aroudn the one selected. To learn how this works, please click on the link in 'Learn more'\", 1200, DATE(), \"https://github.com/MiYazJE/Acepta-el-reto/blob/master/p315.java\");");

        sqlLiteDB.execSQL("INSERT INTO comment(postId_FK, tagId_FK, message, date) VALUES(1, \"mariaa5123\", \"Looks nice!\", DATE());");
    }

    public ArrayList<Post> postsList() {
        ArrayList<Post> list = new ArrayList<>();

        Cursor datosConsulta = sqlLiteDB.rawQuery("select * from post", null);
        if (datosConsulta.moveToFirst()) {
            Post post;
            while (!datosConsulta.isAfterLast()) {
                post = new Post(datosConsulta.getInt(0), datosConsulta.getString(1), datosConsulta.getString(2), datosConsulta.getString(3), datosConsulta.getLong(4), Date.valueOf(datosConsulta.getString(5)), datosConsulta.getString(6));
                list.add(post);
                datosConsulta.moveToNext();
            }
        }

        return list;
    }

    public User getUser(String tag){
        User us = null;

        return us;
    }

    public User getCurrentUser(){
        User us = null;

        return us;
    }

    private void uploadUser(String downloadUrl) {
        // TODO : complete this method
//        usersRef.child(currUserUid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    User us = getUser();
//                    String tag = snapshot.child("tag").getValue()+"";
//                    String profileImage = snapshot.child("profileImage").getValue()+"";
//
//                    HashMap userMap = new HashMap();
//                    userMap.put("about", "");
//                    userMap.put("email", "");
//                    userMap.put("password", );
//                    userMap.put("profileImage", profile);
//                    userMap.put("tag", tag);
//                    userMap.put("username", );
//
//                    usersRef.child(generateTimeStamp()).updateChildren(userMap).addOnCompleteListener(task -> {
//                        if(task.isSuccessful())
//                            Toast.makeText(context, "Profile image successfully uploaded", Toast.LENGTH_SHORT).show();
//                        else
//                            Toast.makeText(context, "Error, profile image wasn't uploaded", Toast.LENGTH_SHORT).show();
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    private void uploadPostOnDatabase(String downloadUrl) {
        // TODO : Retrieve all post data
//        usersRef.child(currUserTag).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    String tag_FK = snapshot.child("username").getValue()+"";
//                    String multimedia = snapshot.child("multimedia").getValue()+"";
//
//                    HashMap postMap = new HashMap();
//                    postMap.put("date", new java.util.Date());
//                    postMap.put("description", "");
//                    postMap.put("likes", "");
//                    postMap.put("multimedia", multimedia);
//                    postMap.put("tag_FK", tag_FK);
//                    postMap.put("url", "");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    // ADD Declarations
    public long addFollow(String tagId_Followed, String tagId_Follower){
        ContentValues valores = new ContentValues();
        valores.put("tagId_Followed", tagId_Followed);
        valores.put("tagId_Follower", tagId_Follower);
        long id = sqlLiteDB.insert("follows",null,valores);

        return id;
    }

    public long addPost(Post p){
        ContentValues valores = new ContentValues();
        valores.put("tagId_FK", p.getAuthor());
        valores.put("multimediaPath", p.getMultimediaPath());
        valores.put("description", p.getDescription());
        valores.put("likes", p.getLikes());
        valores.put("date", new SimpleDateFormat("yyyy-MM-dd").format(p.getDate()));
        valores.put("codeURL", p.getCodeURL());
        long id = sqlLiteDB.insert("post",null,valores);

        return id;
    }

    //DELETE Declarations
    public long deleteFollow(String tagId_Followed, String tagId_Follower){
        String where = "tagId_Followed=? AND tagId_Follower=?";
        String[] parametros = new String[]{tagId_Followed, tagId_Follower};
        int rexistrosafectados = sqlLiteDB.delete("follows", where, parametros);

        return rexistrosafectados;

    }

    public int updateLikes(int postId, int like) {

        Cursor datosConsulta = sqlLiteDB.rawQuery("select likes from post where postId=?", new String[]{postId+""});
        if(datosConsulta.moveToFirst()){
            like += datosConsulta.getInt(0);
        }

        ContentValues datos = new ContentValues();
        datos.put("likes", like);

        String where = "postId=?";
        String[] params = new String[]{String.valueOf(postId)};

        int rexistrosModificados = sqlLiteDB.update("post", datos, where, params);

        return rexistrosModificados;
    }

    public void logout(){
        auth.signOut();
    }

}
