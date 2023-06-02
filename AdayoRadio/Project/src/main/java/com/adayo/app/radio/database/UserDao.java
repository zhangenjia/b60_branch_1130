package com.adayo.app.radio.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * @author ADAYO-06
 */
@Dao
public interface UserDao {

    /**
     * 增
     *
     * @param users user
     */
    @Insert
    void insert(User... users);

    /**
     * 删单个
     *
     * @param users user
     */
    @Delete
    void delete(User... users);

    /**
     * 删全部
     */
    @Query("DELETE  FROM user")
    void deleteAll();

    /**
     * 改
     *
     * @param users User
     */
    @Update
    void update(User... users);

    /**
     * 查全部
     *
     * @return 返回查找的全部值
     */
    @Query("SELECT * FROM user")
    List<User> getAllUsers();


    /**
     * 查单个
     *
     * @param name 传入的name
     * @return 根据name获取的单个值
     */
    @Query("SELECT * FROM user WHERE name = :name")
    User getUserByName(String name);

}
