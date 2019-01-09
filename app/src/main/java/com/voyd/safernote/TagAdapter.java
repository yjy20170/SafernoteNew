package com.voyd.safernote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class TagAdapter extends ArrayAdapter<String> {
    private String tag;
    private Item item;
    private boolean isForSearch;
    private int resourceId;
    private Context context;
    private CheckBox checkBox;
    private TextView textView;
    
    public TagAdapter(Context context, int resourceId, Item item, TextView textView, boolean isForSearch) {
        super(context, resourceId, MyApp.getAllTags());
        this.resourceId = resourceId;
        this.item = item;
        this.isForSearch = isForSearch;
        this.context = context;
        this.textView = textView;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        tag = getItem(position);
        final View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        checkBox = (CheckBox)view.findViewById(R.id.tag_checkBox);
        checkBox.setText(tag);
        if(isForSearch){
            view.findViewById(R.id.tag_delete).setVisibility(View.INVISIBLE);
        }
        if(item.tags.indexOf(tag)!=-1){
            checkBox.setChecked(true);
        }
        
        final String thisTag = tag;//若不使用final，调用方法时tag的值为最后一个tag
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked){
                if(isChecked){
                    item.addTag(thisTag);
                }else{
                    item.removeTag(thisTag);
                }
                if(!isForSearch){
                    item.updateTags();
                }
                textView.setText(item.tagsString);
            }
        });
        view.findViewById(R.id.tag_delete).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                MyApp.deleteTag(thisTag);
                item.removeTag(thisTag);
                ((TextView)((SafeActivity)context).findViewById(R.id.tags)).setText(item.tagsString);
                notifyDataSetChanged();
            }
        });
        return view;
    }
}
