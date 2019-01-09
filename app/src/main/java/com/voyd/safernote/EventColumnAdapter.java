package com.voyd.safernote;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EventColumnAdapter  extends ArrayAdapter<EventColumn>{
    private int resourceId;
    private EventColumn eventColomn;
    public EventColumnAdapter(Context context, int resourceId,List<EventColumn> objects){
        super(context, resourceId, objects);
        this.resourceId = resourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        eventColomn = getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        //设置颜色和年月
        setColor(view.findViewById(R.id.event_block_0),0);
        setColor(view.findViewById(R.id.event_block_1),1);
        setColor(view.findViewById(R.id.event_block_2),2);
        setColor(view.findViewById(R.id.event_block_3),3);
        setColor(view.findViewById(R.id.event_block_4),4);
        setColor(view.findViewById(R.id.event_block_5),5);
        setColor(view.findViewById(R.id.event_block_6),6);
        ((TextView)view.findViewById(R.id.event_year)).setText(eventColomn.year);
        ((TextView)view.findViewById(R.id.event_month)).setText(eventColomn.month);
        return view;
    }
    private void setColor(View view, int i){
        int level = eventColomn.blocks[i];
        if(level == -1){
            view.setBackgroundColor(Color.argb(0, 0, 0, 0));
        }else{
            int color = Color.parseColor(EventColumn.color[level]);
            view.setBackgroundColor(color);
        }
    }
}
