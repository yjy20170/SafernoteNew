package com.voyd.safernote;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.voyd.safernote.R;

import java.util.List;


public class ItemAdapter extends ArrayAdapter<Item>{
    private boolean isEmptyList;
    private boolean isShowStick;
    private int resourceId;
    private Item item;
    public static int[] showSettings = {0,0,0,0,0};
    public ItemAdapter(Context context, int itemViewResourceId, List<Item> items, boolean isEmptyList, boolean isShowStick){
        super(context, itemViewResourceId, items);
        resourceId = itemViewResourceId;
        this.isEmptyList = isEmptyList;
        this.isShowStick = isShowStick;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        item = getItem(position);
        if (isEmptyList) {
            //从数据库加载
            item.loadDataByPosition(position, true);
        }
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.itemStick = (ImageView) view.findViewById(R.id.itemStick);
            viewHolder.itemTitle = (TextView) view.findViewById(R.id.itemTitle);
            viewHolder.itemWordCount = (TextView) view.findViewById(R.id.itemWordCount);

            viewHolder.itemCreateAndEditTimeLine = (LinearLayout) view.findViewById(R.id.itemCreateAndEditTimeLine);
            viewHolder.itemCreateTime = (TextView) view.findViewById(R.id.itemCreateTime);
            viewHolder.itemEditTime = (TextView) view.findViewById(R.id.itemEditTime);

            viewHolder.itemTagLine = (LinearLayout) view.findViewById(R.id.itemTagLine);
            viewHolder.itemTags = (TextView) view.findViewById(R.id.itemTags);
            viewHolder.itemSummary = (TextView) view.findViewById(R.id.itemSummary);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if (item.stick > 0 && isShowStick) {
            viewHolder.itemStick.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemStick.setVisibility(View.GONE);
        }

        //根据setting设置是否显示
        loadShowSettings();
        viewHolder.itemWordCount.setVisibility(
                showSettings[0] == 1 ? View.VISIBLE : View.GONE);
        viewHolder.itemCreateAndEditTimeLine.setVisibility(
                showSettings[1] == 1 ? View.VISIBLE : View.GONE);
        viewHolder.itemTagLine.setVisibility(
                showSettings[2] == 1 ? View.VISIBLE : View.GONE);
        viewHolder.itemSummary.setVisibility(
                showSettings[3] == 1 ? View.VISIBLE : View.GONE);
        viewHolder.itemTitle.setText(item.title);
        viewHolder.itemWordCount.setText("字数：" + item.wordCount);
        viewHolder.itemCreateTime.setText("创建:" + item.createTime);
        viewHolder.itemEditTime.setText("修改:" + item.editTime);
        viewHolder.itemTags.setText(item.tagsString);

        //update: 花哨度++
        if (item.tagsString.contains("刺激")) {
            viewHolder.itemTitle.setTextColor(0xFFDF76A4);
        } else if (item.tagsString.contains("觉醒")) {
            viewHolder.itemTitle.setTextColor(0xFFC1342D);
        } else if (item.tagsString.contains("心情")){
            viewHolder.itemTitle.setTextColor(0xFF1CA8C1);
        }else{
            viewHolder.itemTitle.setTextColor(getContext().getResources().getColor(R.color.myColorTitle));
        }

        if(showSettings[4]==0){
            viewHolder.itemSummary.setMaxLines(3);
            viewHolder.itemSummary.setEllipsize(TruncateAt.END);
            viewHolder.itemSummary.setText(item.content.replace("\n", "  "));
        }else{
            viewHolder.itemSummary.setMaxLines(Integer.MAX_VALUE);
            viewHolder.itemSummary.setEllipsize(null);
            viewHolder.itemSummary.setText(item.content);
        }
        
        return view;
    }
    class ViewHolder{
        public ImageView itemStick;
        public TextView itemTitle;
        public TextView itemWordCount;
        public LinearLayout itemCreateAndEditTimeLine;
        public TextView itemCreateTime;
        public TextView itemEditTime;
        public LinearLayout itemTagLine;
        public TextView itemTags;
        public TextView itemSummary;
    }
    public void loadShowSettings(){
        showSettings[0] = MyApp.getIntSetting("isShowWordCount");
        showSettings[1] = MyApp.getIntSetting("isShowCreateAndEditTime");
        showSettings[2] = MyApp.getIntSetting("isShowTags");
        showSettings[3] = MyApp.getIntSetting("isShowSummary");
        showSettings[4] = MyApp.getIntSetting("summaryLength");
    }
}
