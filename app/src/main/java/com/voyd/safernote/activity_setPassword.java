package com.voyd.safernote;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.voyd.safernote.R;

public class activity_setPassword extends SafeActivity implements OnClickListener{
    private String inputString;
    private TextView tip;
    private PasswordInputer passwordInputer;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_set_password);
        ((Button)findViewById(R.id.finish)).setOnClickListener(this);
        ((Button)findViewById(R.id.save)).setOnClickListener(this);
        tip = (TextView)findViewById(R.id.titlebarText);
        tip.setText("请输入新密码");
        inputString = "null";
        passwordInputer = (PasswordInputer)findViewById(R.id.password_inputer);
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
        case R.id.finish:
            onBackPressed();
            break;
        case R.id.save:
            if(inputString == "null"){
                inputString = passwordInputer.getInput();
                tip.setText("请再次输入新密码");
                //重置PasswordInputer
                passwordInputer.reset();
                break;
            }else{
                if(inputString.equals(passwordInputer.getInput())){
                    new alert("设置成功");
                    MyApp.updatePassword(inputString);
                    onBackPressed();
                }else{
                    new alert("两次输入不一致，请重新输入");
                    //重置PasswordInputer和自身
                    passwordInputer.reset();
                    tip.setText("请输入新密码");
                    inputString = "null";
                }
            }
        }
    }
}
