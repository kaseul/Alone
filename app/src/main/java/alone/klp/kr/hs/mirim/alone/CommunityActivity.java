package alone.klp.kr.hs.mirim.alone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import alone.klp.kr.hs.mirim.alone.adapter.CommunityAdapter;
import alone.klp.kr.hs.mirim.alone.model.Member;

public class CommunityActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Firebase 인스턴스 변수
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;

    ArrayList<Member> items = new ArrayList<>();//멤버
    ArrayList<Member> searchList = new ArrayList<>();

    // 구글
    private GoogleApiClient mGoogleApiClient;

    private ListView listView;
    private CommunityAdapter adapter;
    Button btn_add;
    EditText editSearch;

    // 사용자 이름과 이메일, 사진
    private String mUsername;
    private String mEmail;
    private String mPhotoUrl;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }//구글 인증이 안되었을 떄


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        listView = findViewById(R.id.listview);
        btn_add = findViewById(R.id.btn_add);
        //editSearch = findViewById(R.id.et_fri_name);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mUsername = mFirebaseUser.getDisplayName();
        mEmail = mFirebaseUser.getEmail();
        mEmail = "@" + mEmail.substring(0, mEmail.indexOf('@')); // 이메일 '@asdf' 형태로 나타내기
        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(AppInvite.API)
                .build();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommunityActivity.this, CreateMemoActivity.class);
                intent.putExtra("mUsername", mUsername);
                intent.putExtra("mEmail", mEmail);
                intent.putExtra("mPhotoUrl", mPhotoUrl);

                startActivity(intent);
            }//onClick
        });//btn_input.setOnClickListener

        // Firebase Remote Config 초기화
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Firebase Remote Config 설정
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();

        // 인터넷 연결이 안 되었을 때 기본 값 정의
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("message_length", 10L);

        // 설정과 기본 값 설정
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

    }//onCreate

    @Override
    protected void onResume() {
        super.onResume();
        items.clear();
        searchList.clear();
        getDatabase();
    }

    //함수 getDatabase()
    private void getDatabase() {
        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("messages");

        mChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Member member = dataSnapshot.getValue(Member.class);
                createMemberList(member);
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

    private void  createMemberList(Member member){
        items.add(member);
        searchList.add(member);
        Log.d("값 확인", member.getText() + " " + items.size());
        adapter = new CommunityAdapter(items);
        listView.setAdapter(adapter);
    }//createMemberList

}
