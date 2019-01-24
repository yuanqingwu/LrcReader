package com.wyq.lrcreader.model.viewmodel;

import com.wyq.lrcreader.datasource.local.gecimi.DbGecimiRepository;
import com.wyq.lrcreader.db.entity.SearchResultEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

/**
 * @author Uni.W
 * @date 2019/1/22 21:44
 */
public class SearchResultViewModel extends ViewModel {


    private MediatorLiveData<List<SearchResultEntity>> mObservableSearchResults;

    private DbGecimiRepository repository;

    public SearchResultViewModel(DbGecimiRepository gecimiRepository) {

        mObservableSearchResults = new MediatorLiveData<>();
        mObservableSearchResults.setValue(null);

        repository = gecimiRepository;
        LiveData<List<SearchResultEntity>> listLiveData = repository.getAllSearchResult();

        mObservableSearchResults.addSource(listLiveData, new Observer<List<SearchResultEntity>>() {
            @Override
            public void onChanged(List<SearchResultEntity> searchResultEntities) {
                mObservableSearchResults.setValue(searchResultEntities);
            }
        });
    }

    public LiveData<List<SearchResultEntity>> getSearchResults() {
        return mObservableSearchResults;
    }


    public void setSearchResults(List<SearchResultEntity> results) {
        mObservableSearchResults.setValue(results);
    }

}
