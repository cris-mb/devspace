package iessanclemente.PRO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import iessanclemente.PRO.model.Post;
import iessanclemente.PRO.model.User;

public class DatabaseOperations extends SQLiteOpenHelper{

    public SQLiteDatabase sqlLiteDB;
    public final static String BD_NAME="DEVSPACE";
    public final static int VERSION_BD = 1;

    public DatabaseOperations(Context context) {
        super(context, BD_NAME, null, VERSION_BD);

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
        Cursor datosConsulta = sqlLiteDB.rawQuery("select * from user where tagId=?", new String[]{tag});

        if(datosConsulta.moveToFirst())
            return new User(datosConsulta.getString(0), datosConsulta.getString(1), datosConsulta.getString(2), datosConsulta.getString(3), datosConsulta.getString(4), datosConsulta.getString(5));

        return null;
    }

    public User getUserByEmail(String tag){
        Cursor datosConsulta = sqlLiteDB.rawQuery("select * from email where tagId=?", new String[]{tag});

        if(datosConsulta.moveToFirst())
            return new User(datosConsulta.getString(0), datosConsulta.getString(1), datosConsulta.getString(2), datosConsulta.getString(3), datosConsulta.getString(4), datosConsulta.getString(5));

        return null;
    }

    // ADD Declarations
    public long addFollow(String tagId_Followed, String tagId_Follower){
        ContentValues valores = new ContentValues();
        valores.put("tagId_Followed", tagId_Followed);
        valores.put("tagId_Follower", tagId_Follower);
        long id = sqlLiteDB.insert("follows",null,valores);

        return id;
    }

    public long addUser(User us){
        ContentValues valores = new ContentValues();
        valores.put("tagId", us.getUserTag());
        valores.put("username", us.getUsername());
        valores.put("profileImagePath", us.getProfileImagePath());
        valores.put("email", us.getEmail());
        valores.put("password", us.getPassword());
        valores.put("about", us.getAbout());

        long id = sqlLiteDB.insert("user",null,valores);


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

}
