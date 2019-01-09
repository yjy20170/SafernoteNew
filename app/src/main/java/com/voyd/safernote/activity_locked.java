package com.voyd.safernote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import android.support.v7.app.AppCompatActivity;

public class activity_locked extends AppCompatActivity implements OnClickListener{
    private String MD5Password;
    private PasswordInputer passwordInputer;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_locked);
        
        MD5Password = MyApp.getMD5Password();
        
        passwordInputer = (PasswordInputer)findViewById(R.id.password_inputer);
        findViewById(R.id.submit_password).setOnClickListener(this);
    }
    @Override
    public void onBackPressed() {
        
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
        case R.id.submit_password:
            String MD5inputString = MD5Util.MD5(passwordInputer.getInput());
            if(getIntent().getBooleanExtra("isOnAppStart",true)){
                if(MD5inputString.equals(MD5Password)){
                    //将password储存到静态变量中
                    MyApp.password = passwordInputer.getInput();
                    startActivity(new Intent(this,activity_1.class));
                    finish();
                }else{
                    new alert("密码错误！");
                    passwordInputer.reset();
                }
            }else{//onRestart
                if(MD5inputString.equals(MD5Password)){
                    MyApp.isErrorPasswordInputed = false;
                    finish();
                }else if(!MyApp.isErrorPasswordInputed
                        && MyApp.getIntSetting("safetyLevel")==1
                        && MyApp.password.length() > 4
                        && passwordInputer.getInput().equals(MyApp.password.substring(0, 4))){
                    finish();
                }else{
                    MyApp.isErrorPasswordInputed = true;
                    new alert("密码错误！需输入长密码!");
                    passwordInputer.reset();
                }
            }
            break;
        }
    }
}
