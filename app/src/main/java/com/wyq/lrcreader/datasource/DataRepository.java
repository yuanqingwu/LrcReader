package com.wyq.lrcreader.datasource;

import android.content.Context;
import android.graphics.Bitmap;

import com.wyq.lrcreader.base.GlideApp;
import com.wyq.lrcreader.datasource.local.gecimi.DbGecimiRepository;
import com.wyq.lrcreader.datasource.net.gecimi.GecimeModel;
import com.wyq.lrcreader.db.AppDatabase;
import com.wyq.lrcreader.db.entity.SearchHistoryEntity;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.db.entity.SongEntity;
import com.wyq.lrcreader.model.netmodel.gecimemodel.AlbumCover;
import com.wyq.lrcreader.model.netmodel.gecimemodel.Artist;
import com.wyq.lrcreader.model.netmodel.gecimemodel.LyricResult;
import com.wyq.lrcreader.utils.LrcFactory;

import org.reactivestreams.Publisher;

import java.util.List;

import androidx.lifecycle.LiveData;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Uni.W
 * @date 2019/1/12 16:48
 */
public class DataRepository {

    private static DataRepository repository;
    private GecimeModel gecimeModel;

    private AppDatabase database;

    private DataRepository(AppDatabase database) {
        gecimeModel = new GecimeModel();
        this.database = database;
    }

    public static DataRepository getInstance(AppDatabase appDatabase) {
        if (repository == null) {
            synchronized (DataRepository.class) {
                if (repository == null) {
                    repository = new DataRepository(appDatabase);
                }
            }
        }
        return repository;
    }

    public DbGecimiRepository getDbGecimiRepository() {
        return DbGecimiRepository.getInstance(database);
    }


    /**
     * todo: 只加载可见的条数
     *
     * @param searchtext
     * @return
     */
    public Flowable<List<SearchResultEntity>> getSearchResult(String searchtext) {
        return gecimeModel.getLrcResultList(searchtext)
                .concatMap(new Function<List<LyricResult>, Publisher<List<SearchResultEntity>>>() {
                               @Override
                               public Publisher<List<SearchResultEntity>> apply(List<LyricResult> lyricResults) {

                                   return Flowable.fromIterable(lyricResults)
                                           .concatMap(new Function<LyricResult, Publisher<SearchResultEntity>>() {
                                               @Override
                                               public Publisher<SearchResultEntity> apply(LyricResult lyricResult) {

                                                   return Flowable.zip(
                                                           gecimeModel.getArtist(lyricResult.getArtist_id()),
                                                           gecimeModel.getAlbumCover(lyricResult.getAid()),
                                                           new BiFunction<Artist, AlbumCover, SearchResultEntity>() {
                                                               @Override
                                                               public SearchResultEntity apply(Artist artist, AlbumCover albumCover) {
                                                                   SearchResultEntity searchResultEntity = new SearchResultEntity();
                                                                   searchResultEntity.setAid(lyricResult.getAid());
                                                                   searchResultEntity.setSongName(lyricResult.getSong());
                                                                   searchResultEntity.setArtist(artist.getName());
                                                                   searchResultEntity.setLrcUri(lyricResult.getLrc());
                                                                   searchResultEntity.setAlbumCoverUri(albumCover.getThumb());
                                                                   searchResultEntity.setDataSource(EDataResource.GECIME.name());

                                                                   DbGecimiRepository.getInstance(database).insertSearchResult(searchResultEntity);

                                                                   return searchResultEntity;
                                                               }
                                                           }
                                                   );
                                               }
                                           })
                                           .toList()
                                           .toFlowable();
                               }
                           }

                );

    }

    public Flowable<String> getLrc(String lrcUri) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder().url(lrcUri).method("GET", null).build();

                Call call = client.newCall(request);
                Response response = call.execute();
                assert response.body() != null;
                String res = response.body().string();
                emitter.onNext(res);
                emitter.onComplete();
            }
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.computation())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) {

                        String res = LrcFactory.getInstance().reFormat(s);

                        return res;
                    }
                });

    }

    public Flowable<Bitmap> getLrcViewBackground(String albumCoverUri, Context context) {
        //todo:暂时修复API的问题
        String coverUri = albumCoverUri.startsWith("http") ? albumCoverUri.replace("/cover/", "/album-cover/") : albumCoverUri;
        return Flowable.create(new FlowableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(FlowableEmitter<Bitmap> emitter) throws Exception {
                Bitmap bitmap = GlideApp.with(context)
                        .asBitmap()
                        .load(coverUri)
                        .submit()
                        .get();
                emitter.onNext(bitmap);
                emitter.onComplete();

            }
        }, BackpressureStrategy.DROP);
    }


    public LiveData<List<SongEntity>> getLocalSongList(AppDatabase appDatabase) {
        LiveData<List<SongEntity>> localSongs = appDatabase.getSongDao().getLocalSongList();
        return localSongs;
    }


    public LiveData<List<SearchHistoryEntity>> getSearchHistoryList() {
        LiveData<List<SearchHistoryEntity>> searchHistory = database.getSearchHistoryDao().getAll();
        return searchHistory;
    }

    public void addSearchHistory(SearchHistoryEntity entity) {
        database.getExecutors().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.getSearchHistoryDao().insert(entity);
            }
        });
    }

    public void deleteAllSearchHistory() {
        database.getExecutors().diskIO().execute(() -> {
            database.getSearchHistoryDao().deleteAll();
        });
    }
}
