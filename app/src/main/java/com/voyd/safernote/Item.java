package com.voyd.safernote;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class Item implements Serializable{
    private static final long serialVersionUID = 1L;
    
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public int id;
    public boolean isNew = false;
    public static SQLiteDatabase db = MyApp.db;
    public String title;
    public String createTime;
    public String editTime;
    public ArrayList<String> tags = new ArrayList<String>();
    public String tagsString = "";
    public String content;
    public String wordCount;
    //置顶功能相关
    public static ArrayList<Integer> sticks = new ArrayList<Integer>();
    public static int stickyCount = 0;
    public int stick;
    //时间记录
    public int writingSeconds = 0;
    public int readingSeconds = 0;
    
    public Item(){}
    
    public void createNew(String createTime){
        isNew = true;
        title = "";
        wordCount = "0";
        this.createTime = createTime;
        tagsString = "";
        content = "";
        stick = 0;
    }
    
    public void addTag(String choosedTag){
        if(tags.indexOf(choosedTag) == -1){
            tags.add(choosedTag);
            tagsString = MyApp.listToString(tags);
        }else{
            new alert("本日志已添加该标签");
        }
    }
    public void removeTag(String choosedTag){
        if(tags.indexOf(choosedTag) != -1){
            tags.remove(choosedTag);
            tagsString = MyApp.listToString(tags);
        }
    }
    public void updateTags(){
        if(!isNew){
            db.execSQL("update items set tagsString='"
                    +AES.encrypt(MyApp.password, tagsString)+"' where id="+id);
        }
    }
    //用于修改密码后的更新
    public void updateMainData(String password){
        ContentValues values = new ContentValues();
        values.put("title", AES.encrypt(password, title));
        values.put("wordCount",AES.encrypt(password, wordCount));
        values.put("createTime", AES.encrypt(password, createTime));
        values.put("editTime", AES.encrypt(password, editTime));
        values.put("tagsString",AES.encrypt(password, tagsString));
        values.put("content", AES.encrypt(password, content));
        db.update("items", values, "id = ?", new String[]{Integer.toString(id)});
        
        Event.recordTodayEvent(2);
    }
    public void updateMainData(){
        if(isNew){
            //建一条空项目
            String INSERT_ITEM = "insert into items ("
                    + "title, wordCount, createTime, editTime, tagsString, content,writingSeconds,readingSeconds) "
                    + "values('', 0, '"+AES.encrypt(MyApp.password, createTime)+"','','','',"
                    + "'"+AES.encrypt(MyApp.password, "0")+"','"+AES.encrypt(MyApp.password, "0")+"')";
            db.execSQL(INSERT_ITEM);
            int offset = MyApp.getTableLength("items") - 1;
            Cursor cursor = db.rawQuery("select * from items limit 1 offset "+offset, null);        
            cursor.moveToFirst();
            id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            isNew = false;
            //stick也需更新
            setStick(stick);
            Event.recordTodayEvent(3);
        }
        updateMainData(MyApp.password);
    }

    //根据id，从数据库加载数据
    public void loadDataByPosition(int position, boolean isUseStick){
        //position: activity_1显示的顺序
        //positionS: 在数据库中按id递减的顺序
        int positionS = 0;
        if(!isUseStick){
            positionS = position;
        }else{
            if(position<=stickyCount-1){//是置顶项
                while(true){
                    if(sticks.get(sticks.size()-positionS-1)>0)position--;
                    if(position<0)break;
                    positionS++;
                }
            }else{
                position -= stickyCount;
                while(true){
                    if(sticks.get(sticks.size()-positionS-1)==0)position--;
                    if(position<0)break;
                    positionS++;
                }
            }
        }
        //wrong: this.id = getTableLength(db, "items") - position;未考虑删除
        int offset = MyApp.getTableLength("items") - positionS - 1;
        Cursor cursor = db.rawQuery("select * from items limit 1 offset "+offset, null);
        cursor.moveToFirst();
        id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
        
        title = AES.decrypt(MyApp.password, cursor.getString(cursor.getColumnIndex("title")));
        wordCount = AES.decrypt(MyApp.password, cursor.getString(cursor.getColumnIndex("wordCount")));
        createTime = AES.decrypt(MyApp.password, cursor.getString(cursor.getColumnIndex("createTime")));
        editTime = AES.decrypt(MyApp.password, cursor.getString(cursor.getColumnIndex("editTime")));
        
        tagsString = AES.decrypt(MyApp.password, cursor.getString(cursor.getColumnIndex("tagsString")));
        MyApp.stringToList(tagsString, tags);
//        for(String tag: tags){
//            if(MyApp.getAllTags().indexOf(tag)==-1){
//                tags.remove(tag);
//            }
//        }
        Iterator<String> iter = tags.iterator();
        while(iter.hasNext()){
            String s = iter.next();
            if(MyApp.getAllTags().indexOf(s)==-1){
                iter.remove();
            }
        }
        tagsString = MyApp.listToString(tags);
        
        content = AES.decrypt(MyApp.password, cursor.getString(cursor.getColumnIndex("content")));
        stick = cursor.getInt(cursor.getColumnIndex("stick"));
        try{
            writingSeconds = Integer.parseInt(AES.decrypt(MyApp.password, cursor.getString(cursor.getColumnIndex("writingSeconds"))));
            readingSeconds = Integer.parseInt(AES.decrypt(MyApp.password, cursor.getString(cursor.getColumnIndex("readingSeconds"))));
        }catch(Exception e){
        }
        finally{
            writingSeconds = Math.max(writingSeconds, 0);
            readingSeconds = Math.max(readingSeconds, 0);
        }
    }
    
    public void delete(){
        if(isNew){//未写入数据库
            return;
        }
        db.delete("items", "id = ?", new String[] { Integer.toString(id) });
        
        Event.recordTodayEvent(3);
    }
    
    public static void loadSticks(){
        sticks.clear();
        stickyCount = 0;
        Cursor cursor = db.rawQuery("select * from items", null);
        if(cursor.moveToFirst()){
            do{
                int stick = cursor.getInt(cursor.getColumnIndex("stick"));
                if(stick>0)stickyCount++;
                sticks.add(stick);
            }while(cursor.moveToNext());
        }
        cursor.close();
    }
    public void setStick(int newStick){
        stick = newStick;
        //若isNew，下面操作无意义
        ContentValues values = new ContentValues();
        values.put("stick", stick);
        db.update("items", values, "id = ?", new String[]{Integer.toString(id)});
    }
    
    public void updateSeconds(){
        ContentValues values = new ContentValues();
        values.put("writingSeconds", AES.encrypt(MyApp.password, Integer.toString(writingSeconds)));
        values.put("readingSeconds", AES.encrypt(MyApp.password, Integer.toString(readingSeconds)));
        db.update("items", values, "id = ?", new String[]{Integer.toString(id)});
    }
}
