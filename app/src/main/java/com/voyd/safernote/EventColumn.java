package com.voyd.safernote;

import java.util.Arrays;

public class EventColumn {
    public static String[] color = {"#e5e7ea","#c6e48b","#7bc96f","#239a3b","#196127"};
    public int[] blocks = {0,0,0,0,0,0,0};
    public String month = "";
    public String year = "";
    public EventColumn(int b0, int b1, int b2, int b3, int b4, int b5, int b6, String year, String month){
        this.blocks = new int[]{b0, b1, b2, b3, b4, b5, b6};
        this.year = year;
        this.month = month;
    }
    public EventColumn(){
    }
    public EventColumn(EventColumn old){
        blocks = Arrays.copyOf(old.blocks, 7);
        month = old.month;
        year = old.year;
    }
    public void reset(){
        blocks = new int[]{0,0,0,0,0,0,0};
        month = "";
        year = "";
    }
}
