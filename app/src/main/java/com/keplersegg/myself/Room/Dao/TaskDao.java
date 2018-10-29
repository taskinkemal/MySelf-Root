package com.keplersegg.myself.Room.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.keplersegg.myself.Room.Entity.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM Task")
    List<Task> getAll();

    @Query("SELECT * FROM Task where TaskId = :id")
    Task get(int id);

    @Query("SELECT COUNT(0) from Task")
    int getCount();

    @Insert
    void insertAll(Task... tasks);

    @Delete
    void delete(Task task);
}