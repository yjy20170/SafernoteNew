package com.voyd.safernote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.voyd.safernote.R;

public class DbHelper extends SQLiteOpenHelper {
    public static final String defaultPassword = "123456";
    public static final String CREATE_ITEMS = "create table items ("
            + "id integer primary key autoincrement, "
            + "title text, "
            + "wordCount text, "
            + "createTime text, "
            + "editTime text, "
            + "tagsString text, "
            + "content text, "
            + "stick integer DEFAULT 0, "
            + "writingSeconds text, "
            + "readingSeconds text)";
    public static final String CREATE_SETTINGS = "create table settings ("
            + "md5password text,"
            + "safetyLevel integer DEFAULT 1,"
            + "isShowWordCount integer DEFAULT 1,"
            + "isShowCreateAndEditTime integer DEFAULT 1,"
            + "isShowTags integer DEFAULT 1,"
            + "isShowSummary integer DEFAULT 1,"
            + "summaryLength integer DEFAULT 0,"
            + "tags text)";
    public static final String INSERT_SETTINGS = "insert into settings ("
            + "md5password,safetyLevel) values("
            + "'"+MD5Util.MD5(defaultPassword)+"',"
            + "1)";
    public static final String CREATE_EVENTS = "create table events ("
            + "year integer,"
            + "month integer,"
            + "date integer,"
            + "dayofweek integer,"
            + "level integer)";
    private String dbName;
    public DbHelper(Context context, String name, CursorFactory
            factory, int version) {
        super(context, name, factory, version);
        dbName = name;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        if(dbName.equals(MyApp.context.getString(R.string.database_name))){
            db.execSQL(CREATE_ITEMS);
            db.execSQL(CREATE_SETTINGS);
            db.execSQL(INSERT_SETTINGS);
            db.execSQL(CREATE_EVENTS);
            new alert("数据库创建完毕\n初始密码为"+defaultPassword);
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion){
        case 1://增加安全等级选项
            db.execSQL("alter table settings add column safetyLevel integer");
            db.execSQL("update settings set safetyLevel = 1");
        case 2://置顶
            db.execSQL("alter table items add column stick integer DEFAULT 0");
        case 3://事件
            db.execSQL(CREATE_EVENTS);
        case 4://时间记录
            db.execSQL("alter table items add column writingSeconds text");
            db.execSQL("alter table items add column readingSeconds text");
        case 5://视图
            db.execSQL("alter table settings add column isShowWordCount integer DEFAULT 1");
            db.execSQL("alter table settings add column isShowCreateAndEditTime integer DEFAULT 1");
            db.execSQL("alter table settings add column isShowTags integer DEFAULT 1");
            db.execSQL("alter table settings add column isShowSummary integer DEFAULT 1");
            db.execSQL("alter table settings add column summaryLength integer DEFAULT 0");
        case 6://tags
            db.execSQL("alter table settings add column tags text");
        }
    }
}