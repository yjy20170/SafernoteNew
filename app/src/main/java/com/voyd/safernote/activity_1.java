package com.voyd.safernote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

public class activity_1 extends SafeActivity implements OnClickListener {
    private ListView listView;
    private ItemAdapter itemAdapter;
    private ArrayList<Item> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_1);
        
        listView = (ListView)findViewById(R.id.itemListView);
        int tableItemsLength = MyApp.getTableLength("items");
        //初始化sticks数组
        Item.loadSticks();
        //根据数据库中Item行数，初始化list
        list = new ArrayList<Item>();
        for(int i=0;i<tableItemsLength;i++){
            list.add(new Item());
        }
        
        itemAdapter = new ItemAdapter(this, R.layout.view_item, list, true, true);
        listView.setAdapter(itemAdapter);
        
        listView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Intent intent = new Intent(activity_1.this,activity_2.class);
                intent.putExtra("item",list.get(position));
                intent.putExtra("viewType", 0);
                
                startSafeActivity(intent);
            }
        });
        ((Button) findViewById(R.id.createItem)).setOnClickListener(this);
        //左侧菜单
        ((Button) findViewById(R.id.set_password)).setOnClickListener(this);
        ((Button) findViewById(R.id.import_export_db)).setOnClickListener(this);
        ((Button) findViewById(R.id.settings)).setOnClickListener(this);
        ((Button) findViewById(R.id.statistic)).setOnClickListener(this);
        ((Button) findViewById(R.id.search)).setOnClickListener(this);
        findViewById(R.id.switch_daynight).setOnClickListener(this);

        //改变左侧菜单响应范围
        setDrawerLeftEdgeSize((DrawerLayout)findViewById(R.id.drawer_layout),(float)0.10);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
        case R.id.createItem:
            Intent intent = new Intent(this, activity_2.class);
            intent.putExtra("viewType", 2);
            Item newItem = new Item();
            newItem.createNew(Item.timeFormat.format(new Date()));
            intent.putExtra("item", newItem);
            startSafeActivity(intent);
            /*
            SQLiteDatabase db = MyApplication.db;
            db.execSQL(DbHelper.CREATE_ITEMS.replace("items", "temp"));
            db.execSQL("insert into temp(title,wordCount,createTime,editTime,tagsString,content,stick) select title,wordCount,createTime,editTime,tagsString,content,stick from items");
            db.execSQL("drop table if exists items");
            db.execSQL("alter table temp rename to items");
            db.execSQL("update items set writingSeconds='"+AES.encrypt(MyApplication.password, "0")+"'");
            db.execSQL("update items set readingSeconds='"+AES.encrypt(MyApplication.password, "0")+"'");*/
            /*/加密可能会出现在字符串结尾随机加上ascii码小于等于16的字符的bug;先滑动显示完每个item再使用
            for(Item item:list){
                String[] ss = {item.title,item.wordCount,item.createTime,item.editTime,item.tagsString,item.content};
                for(int k=0;k<6;k++){
                    if(ss[k]!=null){
                        while(ss[k].length()>0){
                            if(Character.valueOf(ss[k].charAt(ss[k].length()-1)).hashCode()<17){
                                ss[k]=ss[k].substring(0, ss[k].length()-1);
                            }else{
                                break;
                            }
                        }
                    }
                }
                item.title=ss[0];
                item.wordCount=ss[1];
                item.createTime=ss[2];
                item.editTime=ss[3];
                item.tagsString=ss[4];
                item.content=ss[5];
                item.updateDbData();
            }
            new alert("over");
            /*/
            
            break;
        case R.id.set_password:
            startSafeActivity(new Intent(this, activity_setPassword.class));
            break;
        case R.id.import_export_db:
            startSafeActivity(new Intent(this, activity_importExportDb.class));
            break;
        case R.id.settings:
            startSafeActivity(new Intent(this, activity_settings.class));
            break;
        case R.id.statistic:
            startSafeActivity(new Intent(this, activity_statistic.class));
            break;
        case R.id.search:
            startSafeActivity(new Intent(this, activity_search.class));
            break;
        case R.id.switch_daynight:
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate();
            break;
        default:
        }
    }
    
    //
    @Override
    public void onRestart(){
        if(isFromStack){
            Item.loadSticks();
            int tableItemsLength = MyApp.getTableLength("items");
            //根据数据库中Item行数，初始化list
            list.clear();
            for(int i=0;i<tableItemsLength;i++){
                list.add(new Item());
            }
            itemAdapter.notifyDataSetChanged();
        }
        super.onRestart();
    }
    private void setDrawerLeftEdgeSize(DrawerLayout drawerLayout, float displayWidthPercentage) {
        try {
            // find ViewDragHelper and set it accessible
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);
            // find edge size and set is accessible
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);
            // set new edge size
            // Point displaySize = new Point();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (dm.widthPixels * displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
            Log.e("NoSuchFieldException", e.getMessage().toString());
        } catch (IllegalArgumentException e) {
            Log.e("IllegalArgument", e.getMessage().toString());
        } catch (IllegalAccessException e) {
            Log.e("IllegalAccessException", e.getMessage().toString());
        }
    }
    
}