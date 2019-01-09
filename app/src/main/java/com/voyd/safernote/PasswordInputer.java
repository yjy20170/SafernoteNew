package com.voyd.safernote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.voyd.safernote.R;

public class PasswordInputer extends LinearLayout implements OnClickListener{
    private TextView input;
    private String inputString;
    public PasswordInputer(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_password_inputer, this);
        input = (TextView) findViewById(R.id.input_password);
        inputString = "";
        findViewById(R.id.pw_1).setOnClickListener(this);
        findViewById(R.id.pw_2).setOnClickListener(this);
        findViewById(R.id.pw_3).setOnClickListener(this);
        findViewById(R.id.pw_4).setOnClickListener(this);
        findViewById(R.id.pw_5).setOnClickListener(this);
        findViewById(R.id.pw_6).setOnClickListener(this);
        findViewById(R.id.pw_7).setOnClickListener(this);
        findViewById(R.id.pw_8).setOnClickListener(this);
        findViewById(R.id.pw_9).setOnClickListener(this);
        findViewById(R.id.pw_point).setOnClickListener(this);
        findViewById(R.id.pw_0).setOnClickListener(this);
        findViewById(R.id.pw_delete).setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
        case R.id.pw_1:
            addInput("1");
            break;
        case R.id.pw_2:
            addInput("2");
            break;
        case R.id.pw_3:
            addInput("3");
            break;
        case R.id.pw_4:
            addInput("4");
            break;
        case R.id.pw_5:
            addInput("5");
            break;
        case R.id.pw_6:
            addInput("6");
            break;
        case R.id.pw_7:
            addInput("7");
            break;
        case R.id.pw_8:
            addInput("8");
            break;
        case R.id.pw_9:
            addInput("9");
            break;
        case R.id.pw_point:
            addInput(".");
            break;
        case R.id.pw_0:
            addInput("0");
            break;
        case R.id.pw_delete:
            if(inputString.length()>0){
                    inputString=inputString.substring(0, inputString.length() - 1);
                    addInput("");
            }
            break;
        default:
        }
    }
    
    private void addInput(String c){
        int n = inputString.length();
        if(n + c.length()>16){
            new alert("密码长度不能超过16！");
        }else{
            inputString += c;
            n = inputString.length();
            String s = "";
            for(int i=0;i<n;i++){
                s += "•";
            }
            input.setText(s);
        }
        //动态改变字体大小
        if(n <= 8){
            input.setTextSize(120);
        }else{
            input.setTextSize(8 * 120 / n);
        }
    }
    
    //以下供外部调用
    public void reset(){
        input.setText("");
        inputString = "";
    }
    public String getInput(){
        return inputString;
    }
}
