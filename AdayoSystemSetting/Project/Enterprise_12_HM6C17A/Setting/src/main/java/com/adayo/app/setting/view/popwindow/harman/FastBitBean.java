package com.adayo.app.setting.view.popwindow.harman;

import android.graphics.Bitmap;

import java.util.List;

public class FastBitBean {
    private int leftTopX;
    private int leftTopY;
    private int leftBottomX;
    private int leftBottomY;
    private int rightTopX;
    private int rightTopY;
    private int rightBottomX;
    private int rightBottomY;
    private int centerX;
    private int centerY;
   private List<Bitmap> list;public FastBitBean(int leftTopX, int leftTopY, int leftBottomX, int leftBottomY, int rightTopX, int rightBottomX, int rightBottomY, int centerX, int centerY, int rightTopY,  List<Bitmap> list) {
        this.leftTopX = leftTopX;
        this.leftTopY = leftTopY;
        this.leftBottomX = leftBottomX;
        this.leftBottomY = leftBottomY;
        this.rightTopX = rightTopX;
        this.rightTopY = rightTopY;
        this.rightBottomX = rightBottomX;
        this.rightBottomY = rightBottomY;
        this.centerX = centerX;
        this.centerY = centerY;

        this.list=list;
    }

    public int getLeftTopX() {
        return leftTopX;
    }

    public void setLeftTopX(int leftTopX) {
        this.leftTopX = leftTopX;
    }

    public int getLeftTopY() {
        return leftTopY;
    }

    public void setLeftTopY(int leftTopY) {
        this.leftTopY = leftTopY;
    }

    public int getLeftBottomX() {
        return leftBottomX;
    }

    public void setLeftBottomX(int leftBottomX) {
        this.leftBottomX = leftBottomX;
    }

    public int getLeftBottomY() {
        return leftBottomY;
    }

    public void setLeftBottomY(int leftBottomY) {
        this.leftBottomY = leftBottomY;
    }

    public int getRightTopX() {
        return rightTopX;
    }

    public void setRightTopX(int rightTopX) {
        this.rightTopX = rightTopX;
    }

    public int getRightTopY() {
        return rightTopY;
    }

    public void setRightTopY(int rightTopY) {
        this.rightTopY = rightTopY;
    }

    public int getRightBottomX() {
        return rightBottomX;
    }

    public void setRightBottomX(int rightBottomX) {
        this.rightBottomX = rightBottomX;
    }

    public int getRightBottomY() {
        return rightBottomY;
    }

    public void setRightBottomY(int rightBottomY) {
        this.rightBottomY = rightBottomY;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }


    public List<Bitmap> getList() {
        return list;
    }

    public void setList(List<Bitmap> list) {
        this.list = list;
    }
}
