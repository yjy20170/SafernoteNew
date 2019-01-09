package com.voyd.safernote;

import android.database.Cursor;

import java.util.Calendar;

public class Event {
    public int year;
    public int month;
    public int date;
    public int dayofweek;
    public int level;
    public Event(Cursor cursor){
        year = cursor.getInt(cursor.getColumnIndex("year"));
        month = cursor.getInt(cursor.getColumnIndex("month"));
        date = cursor.getInt(cursor.getColumnIndex("date"));
        dayofweek = cursor.getInt(cursor.getColumnIndex("dayofweek"));
        level = cursor.getInt(cursor.getColumnIndex("level"));
    }
    public Event(Calendar day, int level){
        year = day.get(Calendar.YEAR);
        month = day.get(Calendar.MONTH)+1;
        date = day.get(Calendar.DATE);
        dayofweek = day.get(Calendar.DAY_OF_WEEK)-1;
        this.level = level;
    }
    public int compareTo(Calendar thatDay){
        int thisValue = this.year * 13 * 32 + this.month * 32 + this.date;
        int thatValue = thatDay.get(Calendar.YEAR) * 13 * 32 + (thatDay.get(Calendar.MONTH)+1) * 32 + thatDay.get(Calendar.DATE);
        if(thisValue > thatValue){return 1;}
        else if(thisValue == thatValue){return 0;}
        else return -1;
    }
    
    //与activity_statistic同步
    //1: SafeActivity.onCreate()
    //2: Item.updateDbData(pw)
    //3: Item.updateDbData()[isNew] / Item.delete()[!isNew]
    //TODO 4: ?
    public static void recordTodayEvent(int level){
        Cursor cursor = MyApp.db.rawQuery("select * from events",null);
        int eventsCount = cursor.getCount();
        boolean isInsertNecessary = true;
        if(eventsCount > 0){
            cursor.moveToLast();
            Event lastEvent = new Event(cursor);
            if(lastEvent.compareTo(Calendar.getInstance()) == 0){
                isInsertNecessary = false;
                if(lastEvent.level < level){
                    MyApp.db.execSQL("update events set level="+level
                            + " where year="+lastEvent.year
                            + " and month="+lastEvent.month
                            + " and date="+lastEvent.date);
                }
            }
        }
        if(isInsertNecessary){
            Event todayEvent = new Event(Calendar.getInstance(),level);
            MyApp.db.execSQL("insert into events values ("
                    + todayEvent.year+","
                    + todayEvent.month+","
                    + todayEvent.date+","
                    + todayEvent.dayofweek+","
                    + todayEvent.level+")");
        }
    }
}
