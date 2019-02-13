package com.wyq.lrcreader.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author Uni.W
 * @date 2019/1/22 19:48
 */
@Entity(tableName = "search_history")
public class SearchHistoryEntity {

    @PrimaryKey
    @NonNull
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
