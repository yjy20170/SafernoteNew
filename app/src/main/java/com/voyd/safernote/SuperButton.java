package com.voyd.safernote;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.voyd.safernote.R;

public class SuperButton extends Button{
    public SuperButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable image = getBackground();
        setTwoBackground(this, image);
    }
    private void setTwoBackground(View v, Drawable image){
        GradientDrawable pressed = (GradientDrawable)getResources()
                .getDrawable(R.drawable.superbutton_pressed_style);
        Drawable normal = image;
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
        stateListDrawable.addState(new int[]{}, normal);
        v.setBackground(stateListDrawable);
    }
}
