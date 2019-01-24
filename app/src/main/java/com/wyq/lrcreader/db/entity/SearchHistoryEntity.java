package com.wyq.lrcreader.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author Uni.W
 * @date 2019/1/22 19:48
 */
@Entity(tableName = "search_history")
public class SearchHistoryEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
