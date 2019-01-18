package com.wyq.lrcreader.datasource;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.request.RequestOptions;
import com.wyq.lrcreader.base.GlideApp;
import com.wyq.lrcreader.datasource.net.gecimi.GecimeModel;
import com.wyq.lrcreader.db.entity.SongEntity;
import com.wyq.lrcreader.model.netmodel.gecimemodel.AlbumCover;
import com.wyq.lrcreader.model.netmodel.gecimemodel.Artist;
import com.wyq.lrcreader.model.netmodel.gecimemodel.LyricResult;
import com.wyq.lrcreader.model.viewmodel.SongListModel;
import com.wyq.lrcreader.utils.LrcFactory;

import org.reactivestreams.Publisher;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;
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

    private DataRepository() {
        gecimeModel = new GecimeModel();
    }

    public static DataRepository getInstance() {
        if (repository == null) {
            synchronized (DataRepository.class) {
                if (repository == null) {
                    repository = new DataRepository();
                }
            }
        }
        return repository;
    }


    /**
     * todo: 只加载可见的条数
     *
     * @param searchtext
     * @return
     */
    public Flowable<List<SongListModel>> getSearchResult(String searchtext) {
        return gecimeModel.getLrcResultList(searchtext)
                .concatMap(new Function<List<LyricResult>, Publisher<List<SongListModel>>>() {
                               @Override
                               public Publisher<List<SongListModel>> apply(List<LyricResult> lyricResults) throws Exception {

                                   return Flowable.fromIterable(lyricResults)
                                           .concatMap(new Function<LyricResult, Publisher<SongListModel>>() {
                                               @Override
                                               public Publisher<SongListModel> apply(LyricResult lyricResult) throws Exception {

                                                   return Flowable.zip(
                                                           gecimeModel.getArtist(lyricResult.getArtist_id()),
                                                           gecimeModel.getAlbumCover(lyricResult.getAid()),
                                                           new BiFunction<Artist, AlbumCover, SongListModel>() {
                                                               @Override
                                                               public SongListModel apply(Artist artist, AlbumCover albumCover) throws Exception {
                                                                   SongListModel songListModel = new SongListModel();
                                                                   songListModel.setSongName(lyricResult.getSong());
                                                                   songListModel.setArtist(artist.getName());
                                                                   songListModel.setLrcUri(lyricResult.getLrc());
                                                                   songListModel.setAlbumCoverUri(albumCover.getThumb());
                                                                   return songListModel;
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
                    public String apply(String s) throws Exception {

                        String res = LrcFactory.getInstance().reFormat(s);

                        return res;
                    }
                });

    }

    public Flowable<Bitmap> getLrcViewBackground(String albumCoverUri, Context context) {
        //todo:暂时修复API的问题
        String coverUri = albumCoverUri.replace("/cover/", "/album-cover/");
        return Flowable.create(new FlowableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(FlowableEmitter<Bitmap> emitter) throws Exception {
                Bitmap bitmap = GlideApp.with(context)
                        .asBitmap()
                        .load(coverUri)
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation()))
                        .submit()
                        .get();
                emitter.onNext(bitmap);
                emitter.onComplete();

            }
        }, BackpressureStrategy.DROP);
    }


    public Flowable<List<SongEntity>> getLocalHistory() {


        return null;
    }
}
