package com.voyd.safernote;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TagsManager{
    private Context context;
    private Item item;
    public AlertDialog dialog;
    public TagsManager(Context context, Item item, TextView textView, boolean isForSearch){
        this.context = context;
        this.item = item;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LinearLayout dialogView= (LinearLayout) ((SafeActivity)context).getLayoutInflater()
                .inflate(R.layout.dialog_tags_manager,null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        dialog = dialogBuilder.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = 470;
        dialog.getWindow().setAttributes(lp);
        
        if(isForSearch){
            dialog.findViewById(R.id.addNewTag_raw).setVisibility(View.GONE);
        }
        final TagAdapter tagAdapter = new TagAdapter(context, R.layout.view_tag, item, textView, isForSearch);
        ((ListView)dialog.findViewById(R.id.tags_listView)).setAdapter(tagAdapter);
        OnClickListener onClickListener = new OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId()){
                case R.id.cancel:
                    /*
                    MyApp.db.execSQL("update settings set tags='"+AES.encrypt(MyApp.password, 
                            "hello,world,i,am,a,student,in,beijing,hangkong,hangtian,university,safernote")+"'");
                    */    
                    dialog.dismiss();
                    break;
                case R.id.addNewTag_confirm:
                    //关闭键盘
                    InputMethodManager inputMethodManager = (InputMethodManager) TagsManager.this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null && dialog.getCurrentFocus() != null) {
                        inputMethodManager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    
                    String newTag = ((TextView)dialog.findViewById(R.id.addNewTag_input)).getText().toString();
                    MyApp.createNewTag(newTag);
                    TagsManager.this.item.addTag(newTag);
                    tagAdapter.notifyDataSetChanged();
                    ((TextView)dialog.findViewById(R.id.addNewTag_input)).setText("");
                    ((TextView)((SafeActivity)TagsManager.this.context).findViewById(R.id.tags)).setText(TagsManager.this.item.tagsString);
                    break;
                }
            }
        };
        dialog.findViewById(R.id.cancel).setOnClickListener(onClickListener);
        dialog.findViewById(R.id.addNewTag_confirm).setOnClickListener(onClickListener);
    }
}