package com.wyq.lrcreader.model.viewmodel;

import android.app.Application;

import com.wyq.lrcreader.base.BasicApp;
import com.wyq.lrcreader.datasource.DataRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * @author Uni.W
 * @date 2019/1/14 21:32
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private DataRepository repository;

    public ViewModelFactory(Application application) {
        repository = ((BasicApp) application).getDataRepository();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(repository);
        }
        return null;
    }
}
