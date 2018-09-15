package com.example.gaozhili01.opengltextureview;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;

public class MainActivity extends Activity implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnVideoSizeChangedListener {

    private String videoPath = Environment.getExternalStorageDirectory().getPath() + "/testFace.mp4";
    private TextureView textureView;
    private MediaPlayer mediaPlayer;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private final static String TAG = "MainActivity";
    public int mVideoMode = 0;
    private int mVideoWidth;
    private int mVideoHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureView = findViewById(R.id.id_textureview);
        textureView.setSurfaceTextureListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (textureView.isAvailable()) {
            playVideo();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (surfaceTexture != null) {
            surfaceTexture.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playVideo() {
        if (mediaPlayer == null) {
            surfaceTexture = textureView.getSurfaceTexture();
            surface = new Surface(surfaceTexture);
            initMediaPlayer();
        }
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(videoPath);
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        playVideo();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        updateTextureViewSizeCenter();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.start();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        Log.i(TAG, "Buffering: " + i);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
        mVideoWidth = mediaPlayer.getVideoWidth();
        mVideoHeight = mediaPlayer.getVideoHeight();
        updateTextureViewSizeCenter();
    }

    private void updateTextureViewSizeCenter() {
        float sx = (float)textureView.getWidth() / (float)mVideoWidth;
        float sy = (float)textureView.getHeight() / (float)mVideoHeight;
        Matrix matrix = new Matrix();
        matrix.preTranslate((textureView.getWidth() - mVideoWidth) / 2, (textureView.getHeight() - mVideoHeight) / 2);
        matrix.preScale(mVideoWidth / (float)textureView.getWidth(), mVideoHeight / (float)textureView.getHeight());
        if (sx >= sy) {
            matrix.postScale(sy, sy, textureView.getWidth() / 2, textureView.getHeight() / 2);
        } else {
            matrix.postScale(sx, sx, textureView.getWidth() / 2, textureView.getHeight() / 2);
        }
        textureView.setTransform(matrix);
        textureView.postInvalidate();
    }
}
