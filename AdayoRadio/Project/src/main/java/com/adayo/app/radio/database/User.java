package com.adayo.app.radio.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * @author ADAYO-06
 */
@Entity
public class User {
    /**
     * 主键是否自动增长，默认为false
     */
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private boolean isFocused =false;

    public boolean getFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }
    @Ignore
    public User(String name,boolean isFocused) {
        this.name = name;
        this.isFocused = isFocused;
    }

    public User() {
    }

    /**
     * 这里的getter/setter方法是必须的
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
