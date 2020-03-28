package com.csquared.bgcloud.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.csquared.bgcloud.R;
import com.csquared.bgcloud.dialog.DownloadDialog;
import com.csquared.bgcloud.io.FileIO;
import com.csquared.bgcloud.json.LoginResponse;
import com.csquared.bgcloud.manager.DownloadManager;
import com.csquared.bgcloud.net.OkHttpBuilder;
import com.csquared.bgcloud.statics.Session;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    ConstraintLayout root;
    TextInputEditText etStudentId, etPassword;
    MaterialCardView btLogin;
    ImageView ivGo, ivCloud;
    ProgressBar progressBar;
    TextInputLayout inLayoutId, inLayoutPwd;

    View layoutWelcomeBack;
    TextView tvWelcome, tvLoggedAccount;
    MaterialButton btChangeAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        root = findViewById(R.id.root);
        etStudentId = findViewById(R.id.edit_student_id);
        etPassword = findViewById(R.id.edit_password);
        btLogin = findViewById(R.id.bt_login);
        ivGo = findViewById(R.id.image_go);
        ivCloud = findViewById(R.id.image_cloud);
        progressBar = findViewById(R.id.progress_bar);
        inLayoutId = findViewById(R.id.in_layout_student_id);
        inLayoutPwd = findViewById(R.id.in_layout_password);

        layoutWelcomeBack = findViewById(R.id.layout_welcome_back);
        tvWelcome = findViewById(R.id.text_welcome);
        tvLoggedAccount = findViewById(R.id.text_logged_account);
        btChangeAccount = findViewById(R.id.bt_change_account);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //startActivity(intent);
                login();
            }
        });

        Animation animCloud = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        animCloud.setFillAfter(true);
        ivCloud.setAnimation(animCloud);

        requestPermissions();

        if (hasLoginRecord()) {
            SharedPreferences pref = getSharedPreferences("auth", Activity.MODE_PRIVATE);
            etStudentId.setText(pref.getString("id", ""));
            etPassword.setText(pref.getString("pwd", ""));
            showQuickLogin();
            tvWelcome.setText("欢迎回来");
            String name = pref.getString("name", null);
            String id = pref.getString("id", "");
            if (name != null) {
                tvWelcome.setText("欢迎回来，" + name);
            } else {
                tvWelcome.setText("欢迎回来");
            }
            tvLoggedAccount.setText("已登录的账号 (" + id + ")");
        } else {
            showDefaultLogin();
        }

        btChangeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etStudentId.setText("");
                etPassword.setText("");
                cleanLoginRecord();
                showDefaultLogin();
            }
        });
    }

    private void login() {
        String id = etStudentId.getText().toString();
        String pwd = etPassword.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        ivGo.setVisibility(View.INVISIBLE);

        OkHttpClient client = new OkHttpBuilder().create(this);
        Request request = new Request.Builder()
                .url("http://dufestu.edufe.com.cn/student/login")
                .post(new FormBody.Builder().add("username", id).add("password", pwd).build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        ivGo.setVisibility(View.VISIBLE);
                        Snackbar.make(root, "无法连接到白果云课堂，请检查网络连接或稍后再试。", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        ivGo.setVisibility(View.VISIBLE);
                    }
                });
                handleLoginResponse(response.body().string());
            }
        });
    }

    private void initDownloader() {
        FileIO.init(Environment.getExternalStorageDirectory());
        FileIO.createDownloadFolder();
    }

    private void handleLoginResponse(String data) {
        LoginResponse response = new Gson().fromJson(data, LoginResponse.class);
        if (response.code == LoginResponse.CODE_SUCCESS) {
            setLoginRecord(etStudentId.getText().toString(), etPassword.getText().toString());
            Session.token = response.data.token;
            Session.studentId = etStudentId.getText().toString();
            Log.d("AUTH", "handleLoginResponse: token = " + Session.token);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Snackbar.make(root, response.message, Snackbar.LENGTH_SHORT).show();
            etPassword.setText("");
            showDefaultLogin();
            cleanLoginRecord();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void requestPermissions() {
        String[] perms = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(this, perms)) {
            initDownloader();
        } else {
            EasyPermissions.requestPermissions(this, "未能获取存储读写权限，打开权限后才能进行资源下载。", 0, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        initDownloader();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private void setLoginRecord(String id, String pwd) {
        SharedPreferences.Editor pref = getSharedPreferences("auth", Activity.MODE_PRIVATE).edit();
        pref.putString("id", id).putString("pwd", pwd).apply();
    }

    private boolean hasLoginRecord() {
        return getSharedPreferences("auth", Activity.MODE_PRIVATE).getString("id", null) != null;
    }

    private void cleanLoginRecord() {
        SharedPreferences.Editor pref = getSharedPreferences("auth", Activity.MODE_PRIVATE).edit();
        pref.remove("id").remove("pwd").remove("name").apply();
    }

    private void showDefaultLogin() {
        inLayoutId.setVisibility(View.VISIBLE);
        inLayoutPwd.setVisibility(View.VISIBLE);
        layoutWelcomeBack.setVisibility(View.INVISIBLE);
    }

    private void showQuickLogin() {
        inLayoutId.setVisibility(View.INVISIBLE);
        inLayoutPwd.setVisibility(View.INVISIBLE);
        layoutWelcomeBack.setVisibility(View.VISIBLE);
    }
}
