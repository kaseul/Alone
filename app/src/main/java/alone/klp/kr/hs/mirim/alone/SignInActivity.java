package alone.klp.kr.hs.mirim.alone;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import alone.klp.kr.hs.mirim.alone.model.varStructure;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    public static varStructure var = new varStructure();

    private static final String TAG = SignInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 1000;

    // Firebase 인스턴스 변수
    private FirebaseAuth mFirebaseAuth;

    // GoogleApiClient 인스턴스 변수
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        permissionCheck();

        // FirebaseAuth 초기화
        mFirebaseAuth = FirebaseAuth.getInstance();

        // GoogleApiClient 초기화
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }//onClick

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }//onConnectionFailed

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 로그인 결과
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // 구글 로그인에 성공하면 파이어베이스에 인증
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // 구글 로그인 실패
                Log.e(TAG, "Google Sign-In failed.");
            }//else
        }//if(requestCode == RC_SIGN_IN)
    }//onActivityResult

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // 인증에 성공하면 MainActivity 로 이동, 실패하면 에러 메시지 표시
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "인증실패", task.getException());
                        Toast.makeText(SignInActivity.this, "인증실패",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        var.UserID = FirebaseAuth.getInstance().getUid();
                        startActivity(new Intent(SignInActivity.this, TutorialActivity.class));
                        finish();
                    }//else
            }//onComplete
        });//mFirebaseAuth
    }//firebaseAuthWithGoogle

    public void permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 사용자의 안드로이드 OS버전이 마시멜로우 이상인지 체크. 맞다면 IF문 내부의 소스코드 작동.
            // 사용자의 단말기에 "전화 걸기" 기능이 허용되어 있는지 확인.
            int permissionResult = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE); // 해당 퍼미션 체크.

            if (permissionResult == PackageManager.PERMISSION_DENIED) { // 해당 퍼미션 권한여부 체크.
                /*
                 * 해당 권한이 거부된 적이 있는지 유무 판별 해야함.
                 * 거부된 적이 있으면 true, 거부된 적이 없으면 false 리턴
                 */
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) { // 거부된 적이 있으면 해당 권한을 사용할 때 상세 내용을 설명. 거부한 적 있으면 true 리턴.
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("이 기능을 사용하기 위해서는 단말기의 \"저장소 접근\"권한이 필요합니다. 계속 하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                                    }
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "기능을 취소했습니다", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();
                } else { //  최초 요청시. 설명 없이 해당 권한을 요청.
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                }
            } else { // 사용자가 권한을 승락함. 바로 실행.

            }
        } else { // 마시멜로우 미만 버전. 즉시 실행.

        }
    }
}
