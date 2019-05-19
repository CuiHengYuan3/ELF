//package com.example.lenovo.elf;
//
//import android.app.Service;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.os.Binder;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.os.Message;
//
//import com.example.lenovo.elf.util.MusicInterface;
//
//import java.io.IOException;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class MusicPlayService extends Service {
//    private MediaPlayer player;
//    private Timer timer;
//    @Override
//    public IBinder onBind(Intent intent) {
//        return new MusicControl();
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        player = new MediaPlayer();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        player.stop();
//        player.release();
//        player = null;
//    }
//
//    public void play() {
//        try {
//            if (player == null) {
//                player = new MediaPlayer();
//            }
//            player.reset();
//            player.setDataSource("sdcard/zxmzf.mp3");
//            player.prepare();
//            player.start();
//
//            addTimer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void pausePlay() {
//        player.pause();
//    }
//
//    public void continuePlay() {
//        player.start();
//    }
//
//    class MusicControl extends Binder implements MusicInterface {
//        @Override
//        public void play() {
//            MusicPlayService.this.play();
//        }
//
//        @Override
//        public void pausePlay() {
//            MusicPlayService.this.pausePlay();
//        }
//
//        @Override
//        public void continuePlay() {
//            MusicPlayService.this.continuePlay();
//        }
//
//        @Override
//        public void seekTo(int progress) {
//            MusicPlayService.this.seekTo(progress);
//        }
//    }
//
//    public void seekTo(int progress) {
//        player.seekTo(progress);
//    }     //添加计时器用于设置音乐播放器中的播放进度
//
//    public void addTimer() {
//        //如果没有创建计时器对象
//        if (timer == null) {
//            timer = new Timer();
//            timer.schedule(new TimerTask() {
//                //执行计时任务
//                @Override
//                public void run() {
//                    int duration = player.getDuration();
//                    int currentPosition = player.getCurrentPosition();
//                    Message msg = HomeActivity.handler.obtainMessage();
//                    //将音乐的播放进度封装至消息对象中
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("duration", duration);
//                    bundle.putInt("currentPosition", currentPosition);
//                    msg.setData(bundle);
//                    HomeActivity.handler.sendMessage(msg);
//                }
//            }, 5, 500);
//
//
//            //开始计时任务后的5毫秒，第一次执行run方法，以后每500毫秒执行一次
//
//
//        }
//
//    }
//}