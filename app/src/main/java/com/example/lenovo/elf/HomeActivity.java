package com.example.lenovo.elf;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lenovo.elf.util.CallBackListener;
import com.example.lenovo.elf.util.DefaultLrcBuilder;
import com.example.lenovo.elf.util.HttpUtil;
import com.example.lenovo.elf.util.ILrcBuilder;
import com.example.lenovo.elf.util.ILrcView;
import com.example.lenovo.elf.util.Loader;
import com.example.lenovo.elf.util.LrcRow;
import com.example.lenovo.elf.util.SongListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomeActivity";
    private static String COLLECTION_FRAGMENT = "collectionFragment";//1
    private static String COMMENT_FRAGMENT = "commentFragment";//2
    private static String DAILYREC_FRAGMETN = "dailyRecFragment";//3
    String lrcToBePass;
    String picUrl;
    String songName;
    boolean isShowAll = false;
    ILrcView mLrcView;
    private int mPalyTimerDuration = 1000;
    //更新歌词的定时器
    private Timer mTimer;
    //更新歌词的定时任务
    private TimerTask mTask;
    ILrcBuilder builder = new DefaultLrcBuilder();
    //    private static TextView tv_progress;
//    private static TextView tv_total;
//    private static SeekBar sb;
//    MyServiceConn conn;
//    Intent intent;
//    MusicInterface mi;
    LinearLayout line_lrcContainer;
    TextView tv_songNmae;
    TextView tv_singer;
    myImageView myImageView;
    ImageView happyimag;
    ImageView unhappyimag;
    ImageView calmiamg;
    ImageView excitimag;
    ImageView playImag;
    LinearLayout lineDayily;
    LinearLayout lineComment;
    LinearLayout lineMycollection;
    LinearLayout linesetting;
    LinearLayout myImageContainer;
    int courentTime;
    List<SongListModel> modelList = new ArrayList<>();
    public myMadiaPlayer mediaPlayer;
    int courrentNum = 0;
    Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
     toolbar.setNavigationOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent2 = new Intent(HomeActivity.this, SongDitailActivity.class);
             intent2.putExtra("madiaPlayer", mediaPlayer);
             //   intent2.putExtra("LrcString",)
             intent2.putExtra("lrcString", lrcToBePass);
             intent2.putExtra("songName", songName);
             intent2.putExtra("picUrl", picUrl);
             intent2.putExtra("list", (Serializable) modelList);
             intent2.putExtra("courrent", courrentNum);
             courentTime = mediaPlayer.getCurrentPosition();
             mediaPlayer.stop();
             if (mTimer != null && mTask != null) {
                 mTimer.cancel();
                 mTask.cancel();
             }
             startActivity(intent2);

         }
     });
        intiView();
        loader = new Loader(HomeActivity.this);
        sendRequest(2332160280L);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.collection:
                generalActivity.actionStartByTag(HomeActivity.this, COLLECTION_FRAGMENT);
                break;
            case R.id.recommend:
                generalActivity.actionStartByTag(HomeActivity.this, DAILYREC_FRAGMETN);
                break;
            case R.id.CommentPlaza:
                generalActivity.actionStartByTag(HomeActivity.this, COMMENT_FRAGMENT);
                break;
            case R.id.SongPic:
                line_lrcContainer.setVisibility(View.VISIBLE);
                myImageContainer.setVisibility(View.GONE);
                myImageView.setVisibility(View.GONE);
                break;
//            case R.id.cover:
//                line_lrcContainer.setVisibility(View.GONE);
//                myImageContainer.setVisibility(View.VISIBLE);
//                myImageView.setVisibility(View.VISIBLE);
//  case  R.id.lrcContainer:
//                myImageContainer.setVisibility(View.VISIBLE);
//                myImageView.setVisibility(View.VISIBLE);
//                line_lrcContainer.setVisibility(View.GONE);
//                break;
            case R.id.line:
                line_lrcContainer.setVisibility(View.VISIBLE);
                myImageView.setVisibility(View.GONE);
                myImageContainer.setVisibility(View.GONE);
                break;
            case R.id.play:
                if (playImag.isClickable()) {
                    Intent intent2 = new Intent(HomeActivity.this, SongDitailActivity.class);
                    intent2.putExtra("madiaPlayer", mediaPlayer);
                    //   intent2.putExtra("LrcString",)
                    intent2.putExtra("lrcString", lrcToBePass);
                    intent2.putExtra("songName", songName);
                    intent2.putExtra("picUrl", picUrl);
                    intent2.putExtra("list", (Serializable) modelList);
                    intent2.putExtra("courrent", courrentNum);
                    courentTime = mediaPlayer.getCurrentPosition();
                    mediaPlayer.stop();
                    if (mTimer != null && mTask != null) {
                        mTimer.cancel();
                        mTask.cancel();
                    }
                    startActivity(intent2);
                }
                break;
            case R.id.happy:

                if (isShowAll == true) {
                    sendOtherRequest(2332160280L);
                } else {
                    showAllEotion();
                }
                break;
            case R.id.unhappy:
                sendOtherRequest(772031667L);
                break;
            case R.id.clam:
                sendOtherRequest(102563603L);
                break;
            case R.id.excit:
                sendOtherRequest(93073411L);
                break;

        }

    }

    void sendOtherRequest(Long ID) {
        sendRequest(ID);
        isShowAll = false;
        hideAlleotion();
        if (ID == 772031667L) {
            happyimag.setImageResource(R.mipmap.ic_mood_unhappy);
        } else if (ID == 102563603L) {
            happyimag.setImageResource(R.mipmap.ic_mood_clam);
        } else if (ID == 93073411L) {
            happyimag.setImageResource(R.mipmap.ic_mood_exciting);
        } else if (ID == 2332160280L) {
            happyimag.setImageResource(R.mipmap.ic_mood_happy);
        }
        happyimag.setVisibility(View.VISIBLE);

    }

    void showAllEotion() {
        isShowAll = true;
        happyimag.setVisibility(View.VISIBLE);
        unhappyimag.setVisibility(View.VISIBLE);
        calmiamg.setVisibility(View.VISIBLE);
        excitimag.setVisibility(View.VISIBLE);
    }

    void hideAlleotion() {
        happyimag.setVisibility(View.INVISIBLE);
        unhappyimag.setVisibility(View.INVISIBLE);
        calmiamg.setVisibility(View.INVISIBLE);
        excitimag.setVisibility(View.INVISIBLE);
    }

    void intiView() {

        tv_singer = findViewById(R.id.singer);
        tv_songNmae = findViewById(R.id.songName);
        lineDayily = findViewById(R.id.recommend);
        lineComment = findViewById(R.id.CommentPlaza);
        lineMycollection = findViewById(R.id.collection);
        linesetting = findViewById(R.id.action_settings);
        happyimag = findViewById(R.id.happy);
        unhappyimag = findViewById(R.id.unhappy);
        calmiamg = findViewById(R.id.clam);
        excitimag = findViewById(R.id.excit);
        playImag = findViewById(R.id.play);
        happyimag.setOnClickListener(this);
        unhappyimag.setOnClickListener(this);
        calmiamg.setOnClickListener(this);
        excitimag.setOnClickListener(this);
        lineDayily.setOnClickListener(this);
        lineComment.setOnClickListener(this);
        lineMycollection.setOnClickListener(this);
        myImageView = findViewById(R.id.SongPic);
        line_lrcContainer = findViewById(R.id.lrcContainer);
        myImageView.setOnClickListener(this);
        mLrcView = findViewById(R.id.LrcView);

        LinearLayout linearLayout = findViewById(R.id.line);
        linearLayout.setOnClickListener(this);
        playImag.setOnClickListener(this);
        myImageContainer = findViewById(R.id.myImageContainer);
        playImag.setClickable(false);
        happyimag.setClickable(false);
        line_lrcContainer.setOnClickListener(this);

        //        tv_progress = findViewById(R.id.progress);
//        tv_total = findViewById(R.id.total);
//        sb = findViewById(R.id.seekBar);
        //setting没有设置监听


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final LinearLayout linearLayout = findViewById(R.id.drawer_content);
            final DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.openDrawer(linearLayout);
        }

        return super.onOptionsItemSelected(item);
    }

    void sendRequest(final Long SongListID) {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mTask != null) {
            mTask.cancel();
        }
        HttpUtil.senHttpReqest("http://elf.egos.hosigus.com/music/playlist/detail?id=" + SongListID, new CallBackListener() {
            @Override
            public void onFinish(String response) {
                if (modelList.size() != 0) {
                    modelList.clear();
                    mediaPlayer.release();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("playlist");
                    JSONArray jsonArray = jsonObject1.getJSONArray("tracks");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        SongListModel songListModel = new SongListModel();
                        int songID = jsonObject2.getInt("id");
                        songListModel.setSongID(songID);
                        String songName = jsonObject2.getString("name");
                        songListModel.setSongName(songName);
                        JSONArray jsonArray1 = jsonObject2.getJSONArray("ar");
                        JSONObject jsonObject3 = jsonArray1.getJSONObject(0);
                        String singerName = jsonObject3.getString("name");
                        songListModel.setSingerName(singerName);
                        JSONObject jsonObject4 = jsonObject2.getJSONObject("al");
                        String picUrl = jsonObject4.getString("picUrl");
                        songListModel.setPicUrl(picUrl);
                        modelList.add(songListModel);
                    }
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run() {
                            toNextSong(courrentNum);
                            playImag.setClickable(true);
                            happyimag.setClickable(true);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(Exception e) {

            }
        });

    }

    //    class MyServiceConn implements ServiceConnection {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mi = (MusicInterface) service;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void toNextSong(int courent) {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mTask != null) {
            mTask.cancel();
        }
        SongListModel songListModelone = modelList.get(courent);
        tv_singer.setText(songListModelone.getSingerName());
        String url = songListModelone.getPicUrl();
        picUrl = url;
        tv_songNmae.setText(songListModelone.getSongName());
        songName = songListModelone.getSongName();
        loader.bindBitmap(url, myImageView);
        int songId = songListModelone.getSongID();
        sendlrcReqeuest(songId, mLrcView, builder);
        String baseUrl = "http://music.163.com/song/media/outer/url?id=";
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new myMadiaPlayer();
        try {
            mediaPlayer.setDataSource(baseUrl + songId + ".mp3");
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    AnimatorAction();
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTask = new LrcTask();
                        mTimer.scheduleAtFixedRate(mTask, 0, mPalyTimerDuration);
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                courrentNum++;
                if (mTimer != null && mTask != null) {
                    mTimer.cancel();
                    mTask.cancel();
                }
                toNextSong(courrentNum);

            }
        });
    }


    @SuppressLint("WrongConstant")
    public void AnimatorAction() {
        if (mediaPlayer.isPlaying()) {
            ObjectAnimator ra = ObjectAnimator.ofFloat(myImageView, "rotation", 0f, 360f);
            ra.start();
            ra.setDuration(8000);
            ra.setInterpolator(new LinearInterpolator()); // 均速旋转
            ra.setRepeatCount(ValueAnimator.INFINITE); // 无限循环
            ra.setRepeatMode(ValueAnimator.INFINITE);
            ra.start();
        }
    }

    class LrcTask extends TimerTask {
        @Override
        public void run() {
            //获取歌曲播放的位置
            final long timePassed = mediaPlayer.getCurrentPosition();
            HomeActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //滚动歌词
                    mLrcView.seekLrcToTime(timePassed);
                }
            });

        }
    }


    public void sendlrcReqeuest(long songID, final ILrcView iLrcView, final ILrcBuilder builder) {
        final String[] lyrString = new String[1];
        String baseUrl = "http://elf.egos.hosigus.com/music/lyric?id=";
        HttpUtil.senHttpReqest(baseUrl + songID, new CallBackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("lrc");
                    lyrString[0] = jsonObject1.getString("lyric");
                    lrcToBePass = lyrString[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<LrcRow> rows = builder.getLrcRows(lyrString[0]);
                            iLrcView.setLrc(rows);

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(Exception e) {

            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}





