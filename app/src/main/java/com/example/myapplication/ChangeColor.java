package com.example.myapplication;

import java.io.Serializable;

public class ChangeColor implements Serializable {
    private int myColorPrimaryDark;

    public ChangeColor(){
        myColorPrimaryDark = -1;
    }

    public int getMyColorPrimaryDark() {
        return myColorPrimaryDark;
    }

    public void setMyColorPrimaryDark(int myColorPrimaryDark) {
        this.myColorPrimaryDark = myColorPrimaryDark;
    }
}
