package com.voyd.safernote;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class MyApp extends Application{
    public static Context context;
    public static SQLiteDatabase db;
    public static final int dbVersion = 7;
    public static String password="";
    public static boolean isErrorPasswordInputed = false;
    private static ArrayList<String> allTags = null;
    private static String allTagsString;
    private static Map<String, Integer> IntSettingMap = new Hashtable<>();

    //public static is
    @Override
    public void onCreate(){
        context = getApplicationContext();
        DbHelper dbHelper = new DbHelper(context, context.getString(R.string.database_name), null, dbVersion);
        db = dbHelper.getWritableDatabase();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate();
    }
    public static String getMD5Password(){
        Cursor cursor = db.rawQuery("select * from settings", null);        
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex("md5password"));
    }
    public static void updatePassword(String newPassword){
        //更新数据库MD5密码；重新加密所有Item数据；更新内存中密码；
        String newMD5Password = MD5Util.MD5(newPassword);

        updateStringSetting("md5password", newMD5Password);
        String oldTags = AES.decrypt(password, getStringSetting("tags"));
        updateStringSetting("tags", AES.encrypt(newPassword, oldTags));
        
        int itemsCount = getTableLength("items");
        Item item = new Item();
        for(int i=0;i<itemsCount;i++){
            item.loadDataByPosition(i, false);
            item.updateMainData(newPassword);
        }
        
        password = newPassword;
    }

    //update: use a Map as the cache of database;
    public static void updateIntSetting(String name, int value){
        db.execSQL("update settings set "+name+" = "+value);
        IntSettingMap.put(name, value);
    }
    public static int getIntSetting(String name){
        if(IntSettingMap.containsKey(name)){
            return IntSettingMap.get(name);
        }else {
            Cursor cursor = db.rawQuery("select * from settings", null);
            if (cursor.moveToFirst()) {
                int value = cursor.getInt(cursor.getColumnIndex(name));
                IntSettingMap.put(name, value);
                return value;
            } else {
                new alert("error when getting " + name);
                return 0;
            }
        }
    }
    public static void updateStringSetting(String name, String value){
        db.execSQL("update settings set "+name+" = '"+value+"'");
    }
    public static String getStringSetting(String name){
        Cursor cursor = db.rawQuery("select * from settings", null);        
        if(cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex(name));
        }else{
            new alert("error when getting "+name);
            return "";
        }
    }
    
    public static int getTableLength(String tableName){
        //得到表中行数
        SQLiteStatement statement = db.compileStatement("select count(*) from "+tableName);
        long count = statement.simpleQueryForLong();
        return (int)count;
    }
    
    //标签
    public static String getAllTagsString(){
        return allTagsString;
    }
    public static ArrayList<String> getAllTags(){
        if(allTags == null){
            allTags = new ArrayList<String>();
            allTagsString = AES.decrypt(password, getStringSetting("tags"));
            stringToList(allTagsString, allTags);
        }
        return allTags;
    }
    public static void createNewTag(String newTag){
        if(allTags.indexOf(newTag)==-1){
            allTags.add(newTag);
            allTagsString = listToString(allTags);
            updateStringSetting("tags", AES.encrypt(password,allTagsString));
        }else{
            new alert("标签\""+newTag+"\"已存在");
        }
    }
    public static void deleteTag(String tag){
        if(allTags.indexOf(tag)!=-1){
            allTags.remove(tag);
            allTagsString = listToString(allTags);
            updateStringSetting("tags", AES.encrypt(password,allTagsString));
        }
    }
    public static String listToString(ArrayList<String> list){
        String string = "";
        if(list.size()>0){
            for(String ele:list){
                string += ele+",";
            }
            string = string.substring(0, string.length()-1);
        }
        return string;
    }
    public static void stringToList(String string, ArrayList<String> list){
        list.clear();
        for(String ele: string.split(",")){
            if(!ele.equals("")){
                list.add(ele);
            }
        }
    }
}
