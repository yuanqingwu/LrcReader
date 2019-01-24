package com.wyq.lrcreader.db;

import android.content.Context;

import com.wyq.lrcreader.db.converter.DateConverter;
import com.wyq.lrcreader.db.dao.SearchResultDao;
import com.wyq.lrcreader.db.dao.SongDao;
import com.wyq.lrcreader.db.entity.SearchHistoryEntity;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.db.entity.SongEntity;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * @author Uni.W
 * @date 2019/1/16 20:35
 */
@Database(entities = {SongEntity.class, SearchHistoryEntity.class, SearchResultEntity.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    @VisibleForTesting
    private static final String DATABASE_NAME = "song.db";

    private static AppDatabase database;

    public abstract SongDao getSongDao();

    public abstract SearchResultDao getSearchResultDao();

    private MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(Context context) {
        if (database == null) {
            synchronized (AppDatabase.class) {
                if (database == null) {
                    database = buildDatabase(context);
                    database.updateDatabaseCreated(context);
                }
            }
        }

        return database;
    }


    private static AppDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).addCallback(new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }

            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
            }
        }).build();
    }

    private void updateDatabaseCreated(Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }
}
