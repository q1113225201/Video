package com.sjl.video;

import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sjl.video.media.MediaController;
import com.sjl.video.media.VideoBackListener;
import com.sjl.video.media.VideoPlayerView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class VideoActivity extends AppCompatActivity implements VideoBackListener {
    private VideoPlayerView videoPlayerView;
    private RelativeLayout rlLoading;
    private RelativeLayout rlStart;
    private TextView tvStart;
    private ImageView ivVideoLoading;

    private AnimationDrawable mLoadingAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initView();
    }

    private void initView() {
        videoPlayerView = findViewById(R.id.player_view);
        rlLoading = findViewById(R.id.rl_loading);
        rlStart = findViewById(R.id.rl_start);
        tvStart = findViewById(R.id.tv_start);
        ivVideoLoading = findViewById(R.id.iv_video_loading);
        loadData();
    }
    private StringBuilder mStartText = new StringBuilder();
    private String mUrl;
    private void loadData() {
        mUrl = getIntent().getStringExtra("url");
        initAnimation();
        initMediaPlayer();
        videoPlayerView.setVideoURI(Uri.parse(mUrl));
        videoPlayerView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                stopAnim();
                mStartText.append("【完成】\n视频缓冲中...");
                tvStart.setText(mStartText.toString());
                rlLoading.setVisibility(View.GONE);
            }
        });
        rlStart.setVisibility(View.GONE);
    }
    private MediaController mMediaController;

    private void initMediaPlayer() {
        mMediaController = new MediaController(this);
        mMediaController.setTitle("标题");
        videoPlayerView.setMediaController(mMediaController);
        videoPlayerView.setMediaBufferingIndicator(rlLoading);
        videoPlayerView.requestFocus();
        videoPlayerView.setOnInfoListener(onInfoListener);
        videoPlayerView.setOnSeekCompleteListener(onSeekCompleteListener);
        videoPlayerView.setOnCompletionListener(onCompletionListener);
        videoPlayerView.setOnControllerEventsListener(onControllerEventsListener);
        // 设置返回键监听
        mMediaController.setVideoBackEvent(this);
    }

    /**
     * 视频缓冲事件回调
     */
    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int i1) {
            if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                rlLoading.setVisibility(View.GONE);
            }
            return true;
        }
    };

    /**
     * 视频跳转事件回调
     */
    private IMediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {

        }
    };

    /**
     * 视频播放完成事件回调
     */
    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            videoPlayerView.pause();
        }
    };

    /**
     * 控制条控制状态事件回调
     */
    private VideoPlayerView.OnControllerEventsListener onControllerEventsListener = new VideoPlayerView.OnControllerEventsListener() {
        @Override
        public void onVideoPause() {

        }

        @Override
        public void onVideoResume() {

        }
    };

    @Override
    public void back() {
        onBackPressed();
    }

    private int mLastPosition = 0;

    @Override
    protected void onResume() {
        super.onResume();
        if (!videoPlayerView.isPlaying()) {
            videoPlayerView.seekTo(mLastPosition);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLastPosition = videoPlayerView.getCurrentPosition();
        videoPlayerView.pause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopAnim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(videoPlayerView.isDrawingCacheEnabled()){
            videoPlayerView.destroyDrawingCache();
        }
        stopAnim();
    }


    private void stopAnim() {
        if(mLoadingAnim!=null){
            mLoadingAnim.stop();
            mLoadingAnim = null;
        }
    }

    private void initAnimation() {
        rlStart.setVisibility(View.VISIBLE);
        mStartText.append("【完成】\n解析视频地址...【完成】");
        tvStart.setText(mStartText.toString());
        mLoadingAnim = (AnimationDrawable) ivVideoLoading.getBackground();
        mLoadingAnim.start();
    }

}
