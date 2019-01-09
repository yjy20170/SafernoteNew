package com.voyd.safernote;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

import com.voyd.safernote.R;

public class SuperEditText extends EditText
        implements OnClickListener,OnTouchListener{
    private activity_2 activity;
    private int lastTouchDownY;
    public SuperEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (activity_2)context;
        this.setBackground(null);
        //this.setFocusable(false);
        //this.setFocusableInTouchMode(false);
        this.setCursorVisible(false);
        this.setOnClickListener(this);
        this.setOnTouchListener(this);
        this.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count){
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                boolean isChanged = activity.isChanged();
                if(activity.viewType==3    && !isChanged){
                    activity.setViewType(null, 2);
                }
                if(activity.viewType==2 && isChanged){
                    activity.setViewType(null, 3);
                }
            }
        });
        //长按后调整layout_2高度和scrollView位置以适应键盘
        //关键：调整layout_2高度后，需等一段时间scrollView的高度才会随之变化
        this.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.setLongClickViewType(SuperEditText.this, activity.viewType+2);
                Runnable runnable = new Runnable() {
                    @Override 
                    public void run() {
                        ScrollView scrollView = ((ScrollView)activity.findViewById(R.id.mainArea));
                        scrollView.scrollTo(0, Math.max(0, lastTouchDownY-50));
                    }
                };
                Handler handler = new Handler();
                handler.postDelayed(runnable, 200);
                return false;
            }
        });
    }
    @Override
    public boolean onTouch(View v, MotionEvent event){
        if(event.getActionMasked()==MotionEvent.ACTION_DOWN){
            lastTouchDownY = (int)event.getY();
        }
        if(activity.getCurrentFocus()!=this){
            boolean result = super.onTouchEvent(event);
            if(activity.getCurrentFocus()==this
                    && event.getActionMasked()==MotionEvent.ACTION_UP){
                onClick(v);
            }
            return result;
        }else{
            return super.onTouchEvent(event);
        }
    }
    @Override
    public void onClick(View v){
        activity_2 activity = SuperEditText.this.activity;
        int viewType = activity.viewType;
        if(viewType==0||viewType==1){
            //0 -> 2 ; 1 -> 3
            setCursorVisible(true);
            activity.setViewType(SuperEditText.this, viewType+2);
        }else{
            //考虑到从长按状态直接点击文字，viewType不变而高度发生变化
            activity.setViewType(SuperEditText.this, viewType);
            //改变当前焦点的属性和重新聚焦到点击位置
            if(activity.lastFocus != null
                    && activity.lastFocus.equals(SuperEditText.this)){
                activity.lastFocus.setCursorVisible(false);
            }
            setCursorVisible(true);
        }
        activity.lastFocus = SuperEditText.this;
        //return super.onTouchEvent(event);
    }
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if(event.getAction()==KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_BACK){
            //2 -> 0 ; 3 -> 1
            leaveEdit();
            if(activity.viewType==2||activity.viewType==3){
                activity.setViewType(this, activity.viewType - 2);
            }
            //必须返回true，否则在关闭输入法的情况下点击事件将继续传播，被activity_2.onBackPressed()捕获
            return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }
    
    public void leaveEdit(){
        setSelection(0);//解决点击上次离开时位置不会自动调整scrollView的问题
        setCursorVisible(false);
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        clearFocus();
        //防止自动聚焦重新将this设为焦点
        activity.findViewById(R.id.blank_view).requestFocus();
    }
    public void enterEdit(){
        requestFocus();
        setCursorVisible(true);
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }
}
