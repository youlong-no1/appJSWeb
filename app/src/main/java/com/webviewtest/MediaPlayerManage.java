package com.webviewtest;

import android.annotation.TargetApi;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * MediaPlayer管理
 * Created by surge on 2016/8/21.
 */
public class MediaPlayerManage implements MediaPlayer.OnErrorListener ,MediaPlayer.OnInfoListener{

    private MediaPlayer mediaPlayer;

    /*是否播放完毕*/
    private boolean isComplete;
    /*视频源*/
    private String source;
    /*视频画布*/
    private SurfaceHolder mSurfaceHolder;
    /*handler*/
    private Handler mHandler;

    /**
     * 播放声音
     * @param url
     */
    public void playAudio(String url){
//        destroy();
        System.gc();
        // 初始化MediaPlayer
        if(mediaPlayer==null) {
            mediaPlayer = new MediaPlayer();
        }
        // 设置声音效果
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置播放完成监听
//        mediaPlayer.setOnCompletionListener(this);
//
//        // 错误监听回调函数
//        mediaPlayer.setOnErrorListener(this);
//        // 设置缓存变化监听
//        mediaPlayer.setOnBufferingUpdateListener(this);

//        Uri uri = Uri
//                .parse(url);
        try {
            // 重置mediaPaly,建议在初始滑mediaplay立即调用。
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置异步加载视频，包括两种方式 prepare()同步，prepareAsync()异步
            mediaPlayer.prepareAsync();
            // 设置媒体加载完成以后回调函数。
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e("播放音频","ok");
                        mp.start();//播放
//                      handler.sendEmptyMessage(Config.HANDLER_CANCEL_PRO);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 播放视频
     */
    public void playVideo(String url, final SurfaceHolder surfaceHolder, final Handler handler) {
        source=url;
        mSurfaceHolder=surfaceHolder;
        mHandler=handler;
//        destroy();
        System.gc();
        // 初始化MediaPlayer
        if(mediaPlayer==null) {
            Log.e("重新初始化","mediaPlayer");
            mediaPlayer = new MediaPlayer();
        }

        try{
            mediaPlayer.reset();
        }catch (IllegalStateException e){
            e.printStackTrace();
            mediaPlayer=null;
            mediaPlayer=new MediaPlayer();
        }
        try {
            // 重置mediaPaly,建议在初始滑mediaplay立即调用。
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            // 设置异步加载视频，包括两种方式 prepare()同步，prepareAsync()异步
            mediaPlayer.prepareAsync();
            // 设置媒体加载完成以后回调函数。
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                    if("0".equals(SharedUtil.getShare("main_current_display"))||"live_tv".equals(SharedUtil.getShare("main_current_display"))||"full_play".equals(SharedUtil.getShare("main_current_display"))) {
//                      if(SharedUtil.getShareBool(SharedUtil.main_current_display)){
                        mp.start();//播放
                        Log.e("开始播放","ok");
                        mediaPlayer.setDisplay(surfaceHolder);
                          Message msg=new Message();
                          msg.what=1;
                          msg.obj=true;
                          handler.sendMessage(msg);
//                        handler.sendEmptyMessage(Config.HANDLER_CANCEL_PRO);

                          int h=mediaPlayer.getVideoHeight();
                          int w=mediaPlayer.getVideoWidth();
                          Log.e("视频分辨率",w+"x"+h);
//                    }else{
//                          Log.e("无法播放","no");
//                      }
                }
            });
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    if(bufferingUpdateListener!=null) {
                        bufferingUpdateListener.onBufferingUpdateListener(mp, percent);
                    }
                }
            });
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    Log.e("跳帧完成","ok");
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isComplete=true;
                    if(completionListener!=null) {
                        completionListener.onCompletionListener(mp);
                    }
                }
            });
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnInfoListener(this);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置画布
     * @param surfaceHolder 画布
     */
    public void setDisplay(SurfaceHolder surfaceHolder){
        try{
            if(mediaPlayer!=null){
                mediaPlayer.setDisplay(surfaceHolder);
            }
        }catch (Exception e){
            Log.e("Exception","Display");
        }
    }
    /**
     * 获取视频总长度
     * @return
     */
    public int getDuration(){
        int duration=0;
        try{
            if(mediaPlayer!=null){
                duration=mediaPlayer.getDuration();
            }
        }catch (Exception e){
            Log.e("Exception","Duration");
        }
        return duration;
    }

    /**
     * 当前视频进度
     * @return
     */
    public int getCurrentPosition(){
        int currentPosition=0;
        try{
            if(mediaPlayer!=null){
                currentPosition=mediaPlayer.getCurrentPosition();
            }
        }catch (Exception e){
            Log.e("Exception","CurrentPosition");
        }
        return currentPosition;
    }

    /**
     * 跳转到指定帧数
     * @param msec 帧数
     */
    public void seekTo(int msec){
        if(mediaPlayer!=null){
            mediaPlayer.seekTo(msec);
        }
    }

    /**
     * 是否正在播放
     * @return
     */
    public boolean isPlaying(){
       boolean isPlaying = false;
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                isPlaying = true;
            }
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return isPlaying;
    }
    /**
     * 暂停
     */
    public void pause() {
        try{
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }catch (Exception e){
            Log.e("Exception","pause");
        }

    }

    /**
     * 停止
     */
    public void stop() {
        try{
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        }catch (Exception e){
            Log.e("Exception","stop");
        }

    }

    /**
     * 销毁
     */
    public void destroy() {
        try{
            if (null != mediaPlayer) {
                mediaPlayer.release();
                mediaPlayer = null;
                System.gc();
            }
        }catch (Exception e){
            Log.e("Exception","destroy");
        }

    }
    /**
     * 获取音轨信息
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void getTrack(){
        try {
            MediaPlayer.TrackInfo[] t = mediaPlayer.getTrackInfo();
            Log.e("TrackInfo的长度", t.length + "//");
            for (int i = 0; i < t.length; i++) {
                Log.e("TrackInfo", t[i].getLanguage() + "//" + t[i].describeContents() + "//" + t[i].getTrackType() + "//" + t[i].getFormat().toString() + "//");
            }
        }catch (NoSuchMethodError e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 切换音轨
     * @param track 指定音轨
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void selectTrack(int track){
        try{
            if (mediaPlayer != null) {
                mediaPlayer.selectTrack(track);
            }
        }catch (Exception e){
            Log.e("Exception","selectTrack="+e.getMessage());
        }
    }
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("tag","错误="+what+"//"+extra);
        if(mediaErrorListener!=null) {
            mediaErrorListener.onMediaErrorListener(mp, what, extra);
        }
//        mHandler.sendEmptyMessage(Config.HANDLER_MEDIA_ERROR);
        return false;
    }
    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        if(mediaInfoListener!=null){
            mediaInfoListener.onMediaInfoListener(mediaPlayer,i,i1);
        }
        return false;
    }
    //视频播放错误接口
    private OnMediaErrorListener mediaErrorListener;

    public interface OnMediaErrorListener{
        boolean onMediaErrorListener(MediaPlayer mp, int what, int extra);
    }
    public void setOnMediaErrorListener(OnMediaErrorListener onMediaErrorListener){
        mediaErrorListener=onMediaErrorListener;
    }
    //视频播放信息接口
    private OnMediaInfoListener mediaInfoListener;
    public interface OnMediaInfoListener{
        boolean onMediaInfoListener(MediaPlayer mp, int what, int extra);
    }
    public void setOnMediaInfoListener(OnMediaInfoListener onMediaInfoListener){
        mediaInfoListener=onMediaInfoListener;
    }
    //获取缓冲进度接口
    private OnBufferingUpdateListener bufferingUpdateListener;
    //视频播放完成接口
    private OnCompletionListener completionListener;
    public interface OnBufferingUpdateListener{
        public void onBufferingUpdateListener(MediaPlayer mp, int percent);
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener){
        bufferingUpdateListener=onBufferingUpdateListener;
    }

    public interface OnCompletionListener{
        public void onCompletionListener(MediaPlayer mp);
    }

    public void setOnCompletionListener(OnCompletionListener onCompletionListener){
        completionListener=onCompletionListener;
    }
}
