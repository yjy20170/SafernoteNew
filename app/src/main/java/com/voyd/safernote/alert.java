package com.voyd.safernote;

import android.widget.Toast;

public class alert {
    public alert(String string){
        Toast.makeText(MyApp.context, string, Toast.LENGTH_SHORT).show();
    }
    public alert(int num){
        Toast.makeText(MyApp.context, Integer.toString(num), Toast.LENGTH_SHORT).show();
    }
    public alert(String string,String flag){
        if(flag.equals("long")){
            Toast.makeText(MyApp.context, string, Toast.LENGTH_LONG).show();
        }
    }
}
