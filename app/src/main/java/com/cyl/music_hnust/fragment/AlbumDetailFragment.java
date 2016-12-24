package com.cyl.music_hnust.fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.cyl.music_hnust.R;
import com.cyl.music_hnust.adapter.AlbumMusicAdapter;
import com.cyl.music_hnust.adapter.LocalMusicAdapter;
import com.cyl.music_hnust.dataloaders.MusicLoader;
import com.cyl.music_hnust.fragment.base.BaseFragment;
import com.cyl.music_hnust.model.music.Music;
import com.cyl.music_hnust.utils.Extras;
import com.cyl.music_hnust.utils.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：yonglong on 2016/8/15 19:54
 * 邮箱：643872807@qq.com
 * 版本：2.5
 * 专辑
 */
public class AlbumDetailFragment extends BaseFragment {

    RecyclerView mRecyclerView;
    Toolbar mToolbar;
    CollapsingToolbarLayout collapsing_toolbar;
    ImageView album_art;


    long albumID;
    boolean isAlbum;
    String transitionName;
    String title;

    private AlbumMusicAdapter mAdapter;
    private List<Music> musicInfos = new ArrayList<>();

    Runnable loadSongs = new Runnable() {
        @Override
        public void run() {
            new loadPlaylist().execute("");
        }
    };

    public static AlbumDetailFragment newInstance(long id, boolean isAlbum, String title, String transitionName) {

        Bundle args = new Bundle();
        args.putLong(Extras.ALBUM_ID, id);
        args.putString(Extras.PLAYLIST_NAME, title);
        args.putBoolean("isAlbum", isAlbum);
        args.putString("transitionName", transitionName);
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void listener() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initDatas() {
        albumID = getArguments().getLong(Extras.ALBUM_ID);
        isAlbum = getArguments().getBoolean("isAlbum");
        transitionName = getArguments().getString("transitionName");
        title = getArguments().getString(Extras.PLAYLIST_NAME);

        if (transitionName != null)
            album_art.setTransitionName(transitionName);
        if (title != null)
            collapsing_toolbar.setTitle(title);
        setAlbumart();
        loadSongs.run();
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_album;
    }


    @Override
    public void initViews() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        collapsing_toolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        album_art = (ImageView) rootView.findViewById(R.id.album_art);

        //toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    /**
     * 显示专辑图片
     */
    private void setAlbumart() {
        Log.e("====", albumID + "=="+title);
        if (isAlbum) {
            loadBitmap(ImageUtils.getAlbumArtUri(albumID).toString());
        }
    }
    private void loadBitmap(String uri) {
        Log.e("EEEE", uri);
        ImageLoader.getInstance().displayImage(uri, album_art,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.default_cover)
                        .resetViewBeforeLoading(true)
                        .build());
    }

    private class loadPlaylist extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (isAlbum) {
                Log.e("专辑id++++++", albumID + "==" +title + "");
                musicInfos = MusicLoader.getAlbumSongs(getContext(), albumID + "");
                Log.e("歌单id++++++", musicInfos.size() + "");
            } else {
                Log.e("歌单id++++++", albumID + "");
                musicInfos = MusicLoader.getArtistSongs(getContext(), albumID + "");
                Log.e("歌单id++++++", musicInfos.size() + "");
            }
            mAdapter = new AlbumMusicAdapter((AppCompatActivity) getActivity(), musicInfos);

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setRecyclerViewAapter();
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private void setRecyclerViewAapter() {
        mRecyclerView.setAdapter(mAdapter);
    }

}
