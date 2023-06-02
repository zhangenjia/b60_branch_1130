package com.adayo.app.dvr.entity;

public class EditEntity {

    private int  num;

    private boolean isCheck;

   public int isNum(){
       return num;
   }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public String toString() {
        return "EditEntity{" +
                "num=" + num +
                ", isCheck=" + isCheck +
                '}';
    }
}
