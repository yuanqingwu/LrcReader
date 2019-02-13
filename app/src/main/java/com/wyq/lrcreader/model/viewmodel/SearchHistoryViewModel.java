package com.wyq.lrcreader.model.viewmodel;

import com.wyq.lrcreader.datasource.DataRepository;
import com.wyq.lrcreader.db.entity.SearchHistoryEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author Uni.W
 * @date 2019/2/13 16:49
 */
public class SearchHistoryViewModel extends ViewModel {

    private LiveData<List<SearchHistoryEntity>> searchHistory;

    private DataRepository repository;

    public SearchHistoryViewModel(DataRepository repository) {
        this.repository = repository;
        searchHistory = repository.getSearchHistoryList();
    }

    public LiveData<List<SearchHistoryEntity>> getSearchHistory() {
        return searchHistory;
    }

    public void addSearchHistory(SearchHistoryEntity entity) {
        repository.addSearchHistory(entity);
    }
}
