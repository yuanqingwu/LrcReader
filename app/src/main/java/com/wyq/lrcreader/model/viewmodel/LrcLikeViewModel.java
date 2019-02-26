package com.wyq.lrcreader.model.viewmodel;

import com.wyq.lrcreader.datasource.DataRepository;
import com.wyq.lrcreader.db.entity.SongEntity;
import com.wyq.lrcreader.ui.LikeGrade;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * @author Uni.W
 * @date 2019/1/31 16:49
 */
public class LrcLikeViewModel extends ViewModel {

    private MediatorLiveData<List<SongEntity>> songEntityMediatorLiveData;
    private DataRepository repository;

    public LrcLikeViewModel(DataRepository repository) {

        this.repository = repository;

        songEntityMediatorLiveData = new MediatorLiveData<>();
        songEntityMediatorLiveData.setValue(null);

        LiveData<List<SongEntity>> listLiveData = repository.getDbGecimiRepository().getLikeSongList(LikeGrade.LIKE_GRADE_LITTER.getValue());

        songEntityMediatorLiveData.addSource(listLiveData, new Observer<List<SongEntity>>() {
            @Override
            public void onChanged(List<SongEntity> songEntities) {
                songEntityMediatorLiveData.setValue(songEntities);
            }
        });
    }

    public LiveData<List<SongEntity>> getLikedSongs() {
        return songEntityMediatorLiveData;
    }

    public void setLikeGrade(int likeGrade) {
        LiveData<List<SongEntity>> listLiveData = repository.getDbGecimiRepository().getLikeSongList(likeGrade);

        songEntityMediatorLiveData.addSource(listLiveData, new Observer<List<SongEntity>>() {
            @Override
            public void onChanged(List<SongEntity> songEntities) {
                songEntityMediatorLiveData.setValue(songEntities);
            }
        });
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private DataRepository repository;

        public Factory(DataRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new LrcLikeViewModel(repository);
        }
    }
}
