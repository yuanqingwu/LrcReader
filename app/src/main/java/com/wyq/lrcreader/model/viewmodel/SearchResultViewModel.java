package com.wyq.lrcreader.model.viewmodel;

import com.wyq.lrcreader.datasource.local.gecimi.DbGecimiRepository;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.utils.LogUtil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

/**
 * @author Uni.W
 * @date 2019/1/22 21:44
 */
public class SearchResultViewModel extends ViewModel {


    private MediatorLiveData<PagedList<SearchResultEntity>> mObservableSearchResults;

    private DbGecimiRepository repository;

    public SearchResultViewModel(DbGecimiRepository gecimiRepository) {

        mObservableSearchResults = new MediatorLiveData<>();
        mObservableSearchResults.setValue(null);

        repository = gecimiRepository;
        LiveData<PagedList<SearchResultEntity>> listLiveData = repository.getAllSearchResult();
        LogUtil.i(listLiveData == null ? "数据源无效" : "数据源有效");

        mObservableSearchResults.addSource(listLiveData, new Observer<PagedList<SearchResultEntity>>() {
            @Override
            public void onChanged(PagedList<SearchResultEntity> searchResultEntities) {
                mObservableSearchResults.setValue(searchResultEntities);
            }
        });
    }

    public LiveData<PagedList<SearchResultEntity>> getSearchResults() {
        return mObservableSearchResults;
    }


    public void setSearchResults(PagedList<SearchResultEntity> results) {
        mObservableSearchResults.setValue(results);
    }

}
