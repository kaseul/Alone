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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import alone.klp.kr.hs.mirim.alone.model.Member;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateMemoActivity extends AppCompatActivity {

    EditText et_memo;
    CircleImageView circleImage;
    TextView name, email;
    Button btn_create, btn_cancel;
    TextView txt_memo_date;

    private DatabaseReference mFirebaseDatabaseReference;

    // 사용자 이름과 이메일, 사진
    private String mUsername;
    private String mEmail;
    private String mPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_memo);

        Intent intent = getIntent();
        mUsername = intent.getStringExtra("mUsername");
        mPhotoUrl = intent.getStringExtra("mPhotoUrl");
        mEmail = intent.getStringExtra("mEmail");

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        circleImage = findViewById(R.id.circleImage);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        et_memo = findViewById(R.id.et_memo);
        txt_memo_date = findViewById(R.id.txt_memo_date);

        if (mPhotoUrl == null) {
            circleImage.setBackgroundResource(R.drawable.ic_account_circle_black_24dp);
        } else {
            Glide.with(getApplicationContext())
                    .load(mPhotoUrl)
                    .into(circleImage);
        }//else
        name.setText(mUsername);
        email.setText(mEmail);

        btn_create = findViewById(R.id.btn_create);
        btn_cancel = findViewById(R.id.btn_cancel);

        Log.d("testmemo", mUsername+"  "+mPhotoUrl);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_memo.getText().toString().equals("")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(CreateMemoActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage("내용을 입력해주세요");
                    alert.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(CreateMemoActivity.this);
                    alert.setPositiveButton("게시하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            // 데이터베이스 저장
                            Member member = new Member(et_memo.getText().toString(),
                                    mUsername,
                                    mEmail,
                                    mPhotoUrl,
                                    null);
                            member.setDate(new Date());

                            mFirebaseDatabaseReference.child("messages")
                                    .push()
                                    .setValue(member);
                            et_memo.setText("");

                            //  Intent saveIntent = new Intent(CreateMemoActivity.this, CommunityActivity.class);
                            //  startActivity(saveIntent);
                            finish();
                        }
                    });
                    alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage("글을 게시하시겠습니까?");
                    alert.show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(CreateMemoActivity.this);
                alert.setNegativeButton("나가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                alert.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.setMessage("정말로 나가시겠습니까? 나가시면 글이 저장되지 않습니다");
                alert.show();
            }
        });
    }
}
