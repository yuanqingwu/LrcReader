package com.wyq.lrcreader.model.viewmodel;

import com.wyq.lrcreader.datasource.DataRepository;
import com.wyq.lrcreader.model.ISong;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MediatorLiveData<ISong> songListLiveData;

    private DataRepository repository;

    public HomeViewModel(DataRepository repository) {
        this.repository = repository;

        songListLiveData = new MediatorLiveData<>();
        songListLiveData.setValue(null);


    }


}
