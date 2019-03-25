package alone.klp.kr.hs.mirim.alone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

import alone.klp.kr.hs.mirim.alone.adapter.CommentAdapter;
import alone.klp.kr.hs.mirim.alone.adapter.CommunityAdapter;
import alone.klp.kr.hs.mirim.alone.model.Comment;
import de.hdodenhof.circleimageview.CircleImageView;

import static alone.klp.kr.hs.mirim.alone.SignInActivity.var;

public class CommunityDetailActivity extends AppCompatActivity {

    private Button btn_cancle, btn_create;
    private ListView listView;
    private EditText edit_comment;
    private CircleImageView circleImageView;
    private TextView name, email, date, text;

    private ArrayList<Comment> comments;
    private CommentAdapter adapter;

    private String PostPhotoUrl, key, mUsername, mPhotoUrl, mEmail;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("messages");

        listView = findViewById(R.id.comment_listview);
        btn_cancle = findViewById(R.id.btn_cancel);
        btn_create = findViewById(R.id.btn_create);
        edit_comment = findViewById(R.id.edit_comment);
        circleImageView = findViewById(R.id.circleImage);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        date = findViewById(R.id.date);
        text = findViewById(R.id.text);

        Intent intent = getIntent();

        PostPhotoUrl = intent.getStringExtra("PostPhotoUrl");
        if (PostPhotoUrl == null) {
            circleImageView.setBackgroundResource(R.drawable.ic_account_circle_black_24dp);
        } else {
            Glide.with(getApplicationContext())
                    .load(PostPhotoUrl)
                    .into(circleImageView);
        }//else
        name.setText(intent.getStringExtra("PostName"));
        email.setText(intent.getStringExtra("PostEmail"));
        date.setText(intent.getStringExtra("PostDate"));
        text.setText(intent.getStringExtra("PostText"));
        key = intent.getStringExtra("PostKey");
        mUsername = intent.getStringExtra("mUsername");
        mPhotoUrl = intent.getStringExtra("mPhotoUrl");
        mEmail = intent.getStringExtra("mEmail");

        comments = new ArrayList<>();
        adapter = new CommentAdapter(comments);
        comments.clear();
        getDatabase();
        listView.setAdapter(adapter);

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(CommunityDetailActivity.this);
                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Comment comment = new Comment(edit_comment.getText().toString(), mUsername, mEmail, mPhotoUrl, null);
                        mFirebaseDatabaseReference.child(key).child("comments")
                                .push()
                                .setValue(comment);
                        edit_comment.setText("");
                    }
                });
                alert.setMessage("댓글을 등록하시겠습니까?");
                alert.show();
            }
        });
    }

    //함수 getDatabase()
    private void getDatabase() {
        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("messages").child(key).child("comments");

        mChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                createCommentList(comment);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };//mchild
        mReference.addChildEventListener(mChild);
    }//getDatabase()

    private void  createCommentList(Comment comment){
        comments.add(comment);
        Log.d("값 확인", comment.getText() + " " + comments.size());
        adapter.setCommentAdapter(comments);
    }//createMemberList
}
