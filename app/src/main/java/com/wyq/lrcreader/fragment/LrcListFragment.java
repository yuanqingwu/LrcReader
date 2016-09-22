package com.wyq.lrcreader.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wyq.lrcreader.R;
import com.wyq.lrcreader.activity.LrcActivity;
import com.wyq.lrcreader.constants.UrlConstant;
import com.wyq.lrcreader.model.Artist;
import com.wyq.lrcreader.model.ArtistResponse;
import com.wyq.lrcreader.model.LrcResponse;
import com.wyq.lrcreader.model.LrcResult;
import com.wyq.lrcreader.model.Song;
import com.wyq.lrcreader.model.ThumbCoverResponse;
import com.wyq.lrcreader.utils.BitmapUtil;
import com.wyq.lrcreader.utils.RecyclerAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class LrcListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private List<Song> songsList;
    private OkHttpClient client;
    private RecyclerAdapter adapter;


    private static final int MESSAGE_LRC = 0;
    private static final int MESSAGE_ERROR_TOAST = 1;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_LRC:
                    Song song = (Song) msg.obj;
                    songsList.set(msg.arg1, song);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case MESSAGE_ERROR_TOAST:
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = new OkHttpClient();
        songsList = new ArrayList<>();
        String searchText = getArguments().getString("searchText");
        searchForLrc(searchText);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lrclist, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_lrclist_recyclerview);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("searching...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        adapter = new RecyclerAdapter(getActivity(), songsList);
//        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Bundle bundle = new Bundle();
                bundle.putString("artist",songsList.get(position).getArtist().toString());
                bundle.putString("lrcText", songsList.get(position).getLrc().toString());
                bundle.putString("songName",songsList.get(position).getSongName().toString());
                bundle.putString("albumCover", BitmapUtil.convertIconToString(songsList.get(position).getAlbumCover()));
                bundle.putBoolean("isLike",false);
//                ByteArrayOutputStream bos=new ByteArrayOutputStream();
//                songsList.get(position).getAlbumCover().compress(Bitmap.CompressFormat.PNG,100,bos);
//                bundle.putByteArray("albumCover",bos.toByteArray());
//                LrcFragment lrcFragment = new LrcFragment();
//                lrcFragment.setArguments(bundle);
//                fragmentReplace(lrcFragment);

                Intent intent=new Intent();
                intent.putExtras(bundle);
                intent.setClass(getActivity(),LrcActivity.class);
                startActivity(intent,bundle);
            }
        });
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //   outState.putParcelableArrayList("songsList", (ArrayList<? extends Parcelable>) songsList);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private void searchForLrc(String searchText) {

        //简单判断是否带有歌手名字；
        String url = "";
        String[] strings = searchText.split(" ");
        if (strings.length == 1) {
            url = UrlConstant.NAME_LRC_URL_ROOT + strings[0];
        } else {
            url = UrlConstant.NAME_LRC_URL_ROOT + strings[0] + "/" + strings[strings.length - 1];
        }
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //   Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Message.obtain(handler, MESSAGE_ERROR_TOAST, "请求失败").sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String res = response.body().string();
//                Log.i("Test", res);
                progressDialog.dismiss();
                analyzeAndShow(res);
            }
        });
    }

    private void searchForArtist(int artistId, final int count, final Song song) {
        String url = UrlConstant.ARTIST_URL_ROOT + artistId;
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Message.obtain(handler, MESSAGE_ERROR_TOAST, "请求失败").sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String res = response.body().string();
//                Log.i("Test", "artist:" + res);
                ArtistResponse artistResponse = new Gson().fromJson(res, ArtistResponse.class);
                Artist artist = artistResponse.getResult();
                song.setArtist(artist.getName());
                Message.obtain(handler, MESSAGE_LRC, count, -1, song).sendToTarget();
            }
        });
    }

    private void searchForLrcText(String url, final int count, final Song song) {
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Message.obtain(handler, MESSAGE_ERROR_TOAST, "请求失败").sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String originStr=response.body().string();
                song.setLrc(originStr.replace("\r\n","\n"));
                Message.obtain(handler, MESSAGE_LRC, count, -1, song).sendToTarget();
            }
        });
    }

    private void searchForAlbumCover(int aid, final int count, final Song song) {

        String url = UrlConstant.AID_ALBUM_COVER_URL_ROOT + aid;
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Message.obtain(handler, MESSAGE_ERROR_TOAST, "请求失败").sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                ThumbCoverResponse thumbCoverResponse = new Gson().fromJson(response.body().string(), ThumbCoverResponse.class);
                searchForCoverImage(thumbCoverResponse.getResult().getCover(), count, song);
            }
        });
    }

    private void searchForCoverImage(String url, final int count, final Song song) {
        Request imageRequest = new Request.Builder().url(url).build();
        Call call = client.newCall(imageRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Message.obtain(handler, MESSAGE_ERROR_TOAST, "请求失败").sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                song.setAlbumCover(bitmap);
                Message.obtain(handler, MESSAGE_LRC, count, -1, song).sendToTarget();
            }
        });
    }

    private void analyzeAndShow(String res) {
        LrcResponse lrcResponse = new Gson().fromJson(res, LrcResponse.class);
        if (lrcResponse.getCode() != 0) {
            // Toast.makeText(getActivity(), "返回结果出错", Toast.LENGTH_SHORT).show();
            Message.obtain(handler, MESSAGE_ERROR_TOAST, "返回结果出错").sendToTarget();
            return;
        }
        if (lrcResponse.getCount() == 0) {
            // Toast.makeText(getActivity(), "返回结果出错", Toast.LENGTH_SHORT).show();
            Message.obtain(handler, MESSAGE_ERROR_TOAST, "抱歉，未搜索到结果").sendToTarget();
            return;
        }
        List<LrcResult> lrcResults = lrcResponse.getResult();
//        Log.i("Test", "lrcResponse:" + lrcResponse.toString());
        for (int i = 0; i < lrcResponse.getCount(); i++) {
            Song song = new Song();
            songsList.add(song);
        }
        for (int count = 0; count < lrcResults.size(); count++) {
            Song song = new Song();
            song.setSongName(lrcResponse.getResult().get(count).getSong());//设置歌名
            searchForArtist(lrcResults.get(count).getArtist_id(), count, song);
            searchForLrcText(lrcResults.get(count).getLrc(), count, song);
            searchForAlbumCover(lrcResults.get(count).getAid(), count, song);
        }

    }

    private void fragmentReplace(Fragment fragment) {
        FragmentManager manager = getFragmentManager();
       // manager.popBackStackImmediate();
        FragmentTransaction transition = manager.beginTransaction();
        transition.replace(R.id.fragment_parent_view, fragment);
        transition.addToBackStack("Lrc");
        transition.commit();
    }

}
