package com.wyq.lrcreader.model.viewmodel;

import android.app.Application;

import com.wyq.lrcreader.base.BasicApp;
import com.wyq.lrcreader.datasource.DataRepository;
import com.wyq.lrcreader.db.AppDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * @author Uni.W
 * @date 2019/1/14 21:32
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private DataRepository repository;
    private AppDatabase database;

    public ViewModelFactory(Application application) {
        repository = ((BasicApp) application).getDataRepository();
        database = ((BasicApp) application).getDatabase();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LocalSongsViewModel.class)) {
            return (T) new LocalSongsViewModel(database, repository);
        }
        return null;
    }
}
