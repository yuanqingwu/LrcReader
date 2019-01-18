package com.wyq.lrcreader.datasource.net.gecimi;

import com.wyq.lrcreader.constants.UrlConstant;
import com.wyq.lrcreader.model.netmodel.gecimemodel.AlbumCover;
import com.wyq.lrcreader.model.netmodel.gecimemodel.AlbumCoverResponse;
import com.wyq.lrcreader.model.netmodel.gecimemodel.Artist;
import com.wyq.lrcreader.model.netmodel.gecimemodel.ArtistResponse;
import com.wyq.lrcreader.model.netmodel.gecimemodel.LyricResponse;
import com.wyq.lrcreader.model.netmodel.gecimemodel.LyricResult;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Uni.W
 * @date 2019/1/12 18:00
 */
public class GecimeModel implements IGecimeModel {


    private IGecimeRequest buildRequest() {
        Retrofit gecime = new Retrofit.Builder()
                .baseUrl(UrlConstant.URL_BASE_GECIME)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return gecime.create(IGecimeRequest.class);
    }

    @Override
    public Flowable<List<LyricResult>> getLrcResultList(String songName) {

        IGecimeRequest gecimeRequest = buildRequest();

        Flowable<LyricResponse> lrcResponseObservable = gecimeRequest.requestWithName(songName);

        return lrcResponseObservable
                .subscribeOn(Schedulers.io())
                .map(new Function<LyricResponse, List<LyricResult>>() {
                    @Override
                    public List<LyricResult> apply(LyricResponse lrcResponse) throws Exception {
                        //如果返回值为0且条数大于0则返回
                        if (lrcResponse.isDataAvailable()) {
                            return lrcResponse.getResult();
                        }
                        return null;
                    }
                });

    }

    @Override
    public Flowable<Artist> getArtist(long artistId) {

        IGecimeRequest artistRequest = buildRequest();

        Flowable<Artist> artistObservable = artistRequest.getArtist(artistId)
                .map(new Function<ArtistResponse, Artist>() {
                    @Override
                    public Artist apply(ArtistResponse artistResponse) throws Exception {
                        if (artistResponse.isSuccessful()) {
                            return artistResponse.getResult();
                        }
                        return null;
                    }
                });
        return artistObservable;
    }


    @Override
    public Flowable<AlbumCover> getAlbumCover(long albumId) {

        return buildRequest().getAlbumCover(albumId).map(new Function<AlbumCoverResponse, AlbumCover>() {
            @Override
            public AlbumCover apply(AlbumCoverResponse albumCoverResponse) throws Exception {

                if (albumCoverResponse.isDataAvailable()) {
                    return albumCoverResponse.getResult();
                }
                return null;
            }
        });
    }
}
