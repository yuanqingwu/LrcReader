package com.wyq.lrcreader.datasource.net.gecimi;

import com.wyq.lrcreader.model.netmodel.gecimemodel.AlbumCoverResponse;
import com.wyq.lrcreader.model.netmodel.gecimemodel.ArtistResponse;
import com.wyq.lrcreader.model.netmodel.gecimemodel.LyricResponse;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * refer to open api: http://api.geci.me/en/latest/
 *
 * @author Uni.W
 * @date 2019/1/12 16:04
 */
public interface IGecimeRequest {

    @GET("lyric/{songName}")
    Flowable<LyricResponse> requestWithName(@Path("songName") String songName);

    @GET("artist/{artistId}")
    Flowable<ArtistResponse> getArtist(@Path("artistId") long artistId);

//    @GET("lrc/{songId}")
//    Flowable<LyricResponse> getLrc(@Path("songId") int songId);

    @GET("cover/{albumId}")
    Flowable<AlbumCoverResponse> getAlbumCover(@Path("albumId") long albumId);
}
