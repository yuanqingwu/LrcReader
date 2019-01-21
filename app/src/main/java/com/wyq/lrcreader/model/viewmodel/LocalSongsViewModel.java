package com.wyq.lrcreader.model.viewmodel;

import com.wyq.lrcreader.datasource.DataRepository;
import com.wyq.lrcreader.db.AppDatabase;
import com.wyq.lrcreader.db.entity.SongEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class LocalSongsViewModel extends ViewModel {

    private MediatorLiveData<List<SongEntity>> songListLiveData;

    private DataRepository repository;

    public LocalSongsViewModel(AppDatabase appDatabase, DataRepository repository) {
        this.repository = repository;

        songListLiveData = new MediatorLiveData<>();
        songListLiveData.setValue(null);

        LiveData<List<SongEntity>> localList = repository.getLocalSongList(appDatabase);
        songListLiveData.addSource(localList, new Observer<List<SongEntity>>() {
            @Override
            public void onChanged(List<SongEntity> songs) {
                songListLiveData.setValue(songs);
            }
        });
    }

    public LiveData<List<SongEntity>> getObservableLocalSong() {
        return songListLiveData;
    }

}
