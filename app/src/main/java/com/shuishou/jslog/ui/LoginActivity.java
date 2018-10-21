package com.shuishou.jslog.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.jslog.ConstantValue;
import com.shuishou.jslog.R;
import com.shuishou.jslog.bean.UpmsResult;
import com.shuishou.jslog.bean.UserData;
import com.shuishou.jslog.http.BasicNameValuePair;
import com.shuishou.jslog.http.OkHttpUtil;
import com.shuishou.jslog.utils.CommonTool;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {
    public static final org.slf4j.Logger LOG = LoggerFactory.getLogger(LoginActivity.class.getSimpleName());
    private static final String TAG = "LoginActivity";
    public static final String INTENTEXTRA_LOGINUSER = "LOGINUSER";
    private static final int REQUEST_SIGNUP = 0;

    private EditText accountText;
    private EditText passwordText;
    private Button loginButton;
    
    public static final int PROGRESSDLGHANDLER_MSGWHAT_ERROR_DISMISSDIALOG = 0;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS = 1;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_LOGINSUCCESS = 2;
    private ProgressDialog progressDlg;
    private Handler progressDlgHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_ERROR_DISMISSDIALOG) {
                if (progressDlg != null)
                    progressDlg.dismiss();
                CommonTool.buildAlertDialog(LoginActivity.this, String.valueOf(msg.obj), "Error");
                loginButton.setEnabled(true);
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_LOGINSUCCESS){
                if (progressDlg != null){
                    progressDlg.dismiss();
                }
                onLoginSuccess();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accountText = findViewById(R.id.accountTxt);
        passwordText = findViewById(R.id.passwordTxt);
        loginButton = findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
        if (!validate()) {
            loginButton.setEnabled(true);
            return;
        }
        loginButton.setEnabled(false);
    
        final String account = accountText.getText().toString();
        final String password = passwordText.getText().toString();
        progressDlg = CommonTool.startProgressDialog(this, "waiting", "Login...", 5);
        new Thread(){
            @Override
            public void run() {
                doLogin(account, password);
            }
        }.start();
    }
    
    public void doLogin(String userName, String password){
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("username", userName));
        pairs.add(new BasicNameValuePair("password", password));
        pairs.add(new BasicNameValuePair("rememberMe", "false"));
        Request request = OkHttpUtil.buildPostRequest(pairs, ConstantValue.URL_TOMCAT + "/sso/login");
        try {
            Response response = OkHttpUtil.execute(request);
            if (response.isSuccessful()){
                String get = response.body().string();
                UpmsResult result = new Gson().fromJson(get, new TypeToken<UpmsResult>(){}.getType());
                if (result.getCode() != 1){
                    LOG.error("get wrong response while login. username = " + userName + ", password = " + password + ", error code = " + result.getCode() + ", error message = " + result.getMessage());
                    progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_ERROR_DISMISSDIALOG, result.getMessage()+"[code " + result.getCode() + "]"));
                    return;
                } else {
                    progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_LOGINSUCCESS, null));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Error occur while login. response.getException() = " + e.getStackTrace());
            progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_ERROR_DISMISSDIALOG, e.getMessage()));
            return ;
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean validate() {
//        if (1==1)
//            return true;
        boolean valid = true;

        String account = accountText.getText().toString();
        String password = passwordText.getText().toString();

        if (account.isEmpty()) {
            accountText.setError("Please enter your account");
            valid = false;
        } else {
            accountText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

//        if ("admin".equals(account) && "1234".equals(password)){
//            return true;
//        }
        return valid;
    }
}
