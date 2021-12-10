package com.example.mobilepjapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mobilepjapp.calender.CalenderActivity;
import com.example.mobilepjapp.model.DataLoad;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity{//} implements GoogleApiClient.OnConnectionFailedListener{

    private View btn_klogin, btn_glogin;    // 카카오, 구글 로그인 버튼

    /* firebase 연동 */
    private static final int RC_SIGN_IN = 100;
    public static FirebaseAuth mAuth;
    GoogleApiClient mGoogleApiClient;

    public static String state = null;

    public static final int MULTIPLE_PERMISSIONS = 1801;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private DataLoad dataLoad = null;

    HttpConnection http;
    public static String resultData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //checkPermissions();

        http = new HttpConnection();
        resultData = startReq();

        dataLoad = new DataLoad();
        dataLoad.dataLoading();

        Intent intent = new Intent(MainActivity.this, CalenderActivity.class);
        startActivity(intent);

        btn_klogin = findViewById(R.id.btn_Kakao_Login);    // 카카오 로그인 버튼
        btn_glogin = findViewById(R.id.btn_Google_Login);   // 구글 로그인 버튼

        //getAppKeyHash();

/*
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            state = "G";

            Intent intent = new Intent(getApplication(), CalenderActivity.class);

            startActivity(intent);
            finish();
        }

        //카카오톡 callback 메소드
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if(oAuthToken != null) {
                    Log.i("user", oAuthToken.getAccessToken() + " " + oAuthToken.getRefreshToken());
                } if(throwable != null) {
                    Log.w(TAG, "invoke: " + throwable.getLocalizedMessage());
                }
                updateKaKaoLoginUi();
                return null;
            }
        };

        //카카오 로그인 버튼 클릭 시
        btn_klogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)) {  // 카카오톡이 있을 경우
                    UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, callback);    // 카카오톡 애플리케이션에 연동하여 로그인
                } else {    //카카오톡이 없는 경우
                    UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this, callback); //카카오 링크에 연결하여 로그인
                }
            }
        });
        updateKaKaoLoginUi();

        btn_glogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
 */
    }   //onCreate()

    /*
    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);

            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
*/
    // 시작 시, 서버 요청 함수
    public String startReq(){
        String link = "get/start";

        JSONObject reqForm = new JSONObject();
        try {
            reqForm.put("email", "fujeong15@gmail.com");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String resultData = http.httpRequest(link, reqForm);

        return resultData;
    }

 /*
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                boolean isDeny = false;
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //permission denyed
                            isDeny = true;
                        }
                    }
                }

                if (isDeny) {
                    showNoPermissionToastAndFinish();
                }
            }
        }
    }

    private void showNoPermissionToastAndFinish() {

        Toast toast = Toast.makeText(this, "권한 요청에 동의하셔야 이용 가능합니다.", Toast.LENGTH_LONG);
        toast.show();

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {   // 구글 로그인 인증을 요청 했을 때 결과 값을 되돌려 받는 곳
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {    // 인증 결과가 성공적일 시
                GoogleSignInAccount account = result.getSignInAccount();    // account: 구글 로그인 정보를 담고 있음
                resultLogin(account);   // 로그인 결과값 출력을 수행
            }
        }
    }

    private void resultLogin(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {   // 로그인 성공 시
                            state = "G";
                            Intent intent = new Intent(getApplicationContext(), CalenderActivity.class);
                            intent.putExtra("nickname", account.getDisplayName());
                            intent.putExtra("photoUrl", String.valueOf(account.getPhotoUrl())); //String으로 변환
                            intent.putExtra("email", account.getEmail());

                            startActivity(intent);
                            System.out.println("넘어감");
                        } else { // 로그인 실패 시

                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    //카카오 UI를 가져오는 메소드
    //카카오톡 User 정보 수집
    public void updateKaKaoLoginUi() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if(user != null){   // User 정보가 정상적으로 전달됨
                    state = "K";

                    Log.i(TAG, "email " + user.getKakaoAccount().getEmail());
                    Log.i(TAG, "invoke: nickname=" + user.getKakaoAccount().getProfile().getNickname());
                    Log.i(TAG, "userimage " + user.getKakaoAccount().getProfile().getProfileImageUrl());

                    Intent intent = new Intent(MainActivity.this, CalenderActivity.class);
                    intent.putExtra("nickname", user.getKakaoAccount().getProfile().getNickname());
                    intent.putExtra("email", user.getKakaoAccount().getEmail());
                    intent.putExtra("photoUrl", user.getKakaoAccount().getProfile().getProfileImageUrl());

                    startActivity(intent);
                    System.out.println("넘어감");
                }

                if(throwable != null) { // 로그인 오류
                    Log.w(TAG, "invoke: " + throwable.getLocalizedMessage());
                }
                return null;
            }
        });
    }

    //카카오 로그인 시 필요한 해시키를 얻는 메소드
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
    }

  */
}