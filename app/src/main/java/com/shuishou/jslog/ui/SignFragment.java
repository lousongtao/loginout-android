package com.shuishou.jslog.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.jslog.ConstantValue;
import com.shuishou.jslog.R;
import com.shuishou.jslog.bean.UpmsResult;
import com.shuishou.jslog.http.BasicNameValuePair;
import com.shuishou.jslog.http.OkHttpUtil;
import com.shuishou.jslog.utils.CommonTool;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class SignFragment extends Fragment {
    public static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SignFragment.class.getSimpleName());
    private static final int SIGNTYPE_IN = 1;
    private static final int SIGNTYPE_OUT = 2;
    private Button btnSignIn;
    private Button btnSignOut;
    private SignInListener signInListener = new SignInListener();
    private SignOutListener signOutListener = new SignOutListener();
    
    public static final int PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG = 0;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS = 1;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_SIGNSUCCESS = 2;
    private ProgressDialog progressDlg;
    private Handler progressDlgHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG) {
                if (progressDlg != null)
                    progressDlg.dismiss();
                CommonTool.buildAlertDialog(getContext(), String.valueOf(msg.obj), "Error");
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_SIGNSUCCESS){
                if (progressDlg != null){
                    progressDlg.dismiss();
                }
                onSignSuccess(null);
            }
        }
    };
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign, container, false);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        btnSignOut = view.findViewById(R.id.btnSignOut);
        btnSignIn.setOnClickListener(signInListener);
        btnSignOut.setOnClickListener(signOutListener);
        return view;
    }
    
    private void clickSign(final int type){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this.getContext())
            .setNegativeButton("No", null)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    doSign(type);
                }
            })
            .setMessage("Do you want to " + (type == SIGNTYPE_IN ? "SIGN IN" : "SIGN OUT") + " now?")
            .setTitle("Sign");
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    private void doSign(int type){
        String url = type == SIGNTYPE_IN ? "/sign/signIn" : "/sign/signOut";
        List<BasicNameValuePair> pairs = new ArrayList<>();
        Request request = OkHttpUtil.buildPostRequest(pairs, ConstantValue.URL_TOMCAT + url);
        try {
            Response response = OkHttpUtil.execute(request);
            if (response.isSuccessful()){
                String get = response.body().string();
                UpmsResult result = new Gson().fromJson(get, new TypeToken<UpmsResult>(){}.getType());
                if (result.getCode() != 1){
                    LOG.error("get wrong response while sign , error code = " + result.getCode() + ", error message = " + result.getMessage());
                    progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG, result.getMessage()+"[code " + result.getCode() + "]"));
                    return;
                } else {
                    progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_SIGNSUCCESS, null));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Error occur while login. response.getException() = " + e.getStackTrace());
            progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG, e.getMessage()));
            return ;
        }
    }
    
    class SignInListener implements View.OnClickListener{
    
        @Override
        public void onClick(View view) {
            clickSign(SIGNTYPE_IN);
        }
    }
    
    class SignOutListener implements View.OnClickListener{
        
        @Override
        public void onClick(View view) {
            clickSign(SIGNTYPE_OUT);
        }
    }
}