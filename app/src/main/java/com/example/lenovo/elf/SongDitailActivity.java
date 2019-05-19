package com.example.lenovo.elf;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.lenovo.elf.util.CallBackListener;
import com.example.lenovo.elf.util.Collection;
import com.example.lenovo.elf.util.DefaultLrcBuilder;
import com.example.lenovo.elf.util.HttpUtil;
import com.example.lenovo.elf.util.ILrcBuilder;
import com.example.lenovo.elf.util.ILrcView;
import com.example.lenovo.elf.util.Loader;
import com.example.lenovo.elf.util.LrcRow;
import com.example.lenovo.elf.util.SongListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SongDitailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SongDitailActivity";
    private static final String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/my/ELF";
    private ImageView im_download;
    private ImageView im_collect;
    private ImageView im_thumb;
    private ImageView im_comment;
    private TextView tx_courent;
    private TextView tx_total;
    private boolean isSeekBarChanging;
    private Timer mTimer;
    private TimerTask mTask;
    public SeekBar seekBar;
    ImageView picImage;
    ImageView im_backSong;
    ImageView im_forwardSong;
    ImageView im_playOrPause;
    ILrcView mlrcview;
    public myMadiaPlayer mediaPlayer;
    int Duration;
    private int time = 0;
    String lrcString;
    Timer timer;
    ILrcBuilder builder = new DefaultLrcBuilder();
    String songName;
    String picUrl;
    List<SongListModel> modelList = new ArrayList<>();
    int courrentNum;
    Loader loader;
    android.support.v7.widget.Toolbar toolbar;

private  Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
    switch (msg.what){
        case 1:
            Toast.makeText(SongDitailActivity.this,"开始下载",Toast.LENGTH_SHORT).show();
    break;
        case  2:
            Toast.makeText(SongDitailActivity.this,"下载完成",Toast.LENGTH_SHORT).show();

    }

    }
};
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_ditail);
        toolbar = findViewById(R.id.detai_toolbar);
        intiView();

        loader = new Loader(SongDitailActivity.this);
        Intent intent = getIntent();
        lrcString = intent.getStringExtra("lrcString");
        songName = intent.getStringExtra("songName");
        picUrl = intent.getStringExtra("picUrl");
        modelList = (List<SongListModel>) intent.getSerializableExtra("list");
        courrentNum = intent.getIntExtra("courrent", 0);
       SongListModel  songListModelone1 = modelList.get(courrentNum);
        if (songListModelone1.isBeCollected() == true) {
            im_collect.setImageResource(R.drawable.ic_star_on);
        }
        int songId = songListModelone1.getSongID();
        String baseUrl = "http://music.163.com/song/media/outer/url?id=";
        mediaPlayer = new myMadiaPlayer();
        try {
            mediaPlayer.setDataSource(baseUrl + songId + ".mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.prepareAsync();
        List<LrcRow> rows = builder.getLrcRows(lrcString);
        mlrcview.setLrc(rows);
        loader.bindBitmap(picUrl, picImage);
        toolbar.setTitle(songName);
        Duration = mediaPlayer.getDuration();
        seekBar.setMax(Duration);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                if (mTimer == null) {
                    mTimer = new Timer();
                    mTask = new LrcTask();
                    mTimer.scheduleAtFixedRate(mTask, 0, 1000);
                }

            }
        });
        //        time=mediaPlayer.getCurrentPosition();不知道为什么time和Durataion是一样的
//        seekBar.setProgress(time);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //mediaPlayer传过来后有BUG,只好这样做
                time += Duration / 400;
                seekBar.setProgress(time);
                Log.d(TAG, String.valueOf(time));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tx_courent.setText(ShowTime(time));
                    }
                });

            }
        }, 5, 500);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int duration2 = mediaPlayer.getDuration() / 1000;
                //获取音乐当前播放的位置
                int position = mediaPlayer.getCurrentPosition();
                //开始时间
                tx_courent.setText(ShowTime(position / 1000));
                //结束时间
                tx_total.setText(ShowTime(duration2));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //在当前位置播放
                mediaPlayer.seekTo(seekBar.getProgress());
                //停止时播放时间
                //开始时间
                tx_courent.setText(ShowTime(mediaPlayer.getCurrentPosition() / 1000));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playOrPause:
                playOrPause();
                break;
            case R.id.forwordSong:
                courrentNum++;
                toNextSong(courrentNum);
                break;
            case R.id.backSong:
                courrentNum--;
                toNextSong(courrentNum);
                break;
            case R.id.detail_collect:
                im_collect.setImageResource(R.drawable.ic_star_on);
                Toast.makeText(SongDitailActivity.this, "已收藏", Toast.LENGTH_SHORT).show();
                SongListModel CollectModelone = modelList.get(courrentNum);
                CollectModelone.setBeCollected(true);
                Collection.listModels.add(CollectModelone);
            break;
                case R.id.detai_comment:
                generalActivity.actionStartByTag(SongDitailActivity.this, "commentFragment");
                break;
            case R.id.Give_like:
                im_thumb.setImageResource(R.drawable.ic_like_on);
                break;
            case R.id.download:
                long id = modelList.get(courrentNum).getSongID();
                String urlString = "http://music.163.com/song/media/outer/url?id=" + id + ".mp3";
                download(urlString);
        }
    }

    void intiView() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        picImage = findViewById(R.id.detail_imag);
        im_download = findViewById(R.id.download);
        im_collect = findViewById(R.id.detail_collect);
        im_thumb = findViewById(R.id.Give_like);
        im_comment = findViewById(R.id.detai_comment);
        seekBar = findViewById(R.id.SeekBar);
        im_backSong = findViewById(R.id.backSong);
        im_forwardSong = findViewById(R.id.forwordSong);
        im_playOrPause = findViewById(R.id.playOrPause);
        tx_courent = findViewById(R.id.courrent);
        tx_total = findViewById(R.id.total);
        mlrcview = findViewById(R.id.detail_lrcView);
        im_download.setOnClickListener(this);
        im_collect.setOnClickListener(this);
        im_thumb.setOnClickListener(this);
        im_comment.setOnClickListener(this);
        im_backSong.setOnClickListener(this);
        im_forwardSong.setOnClickListener(this);
        im_playOrPause.setOnClickListener(this);
    }

    void playOrPause() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            time = mediaPlayer.getCurrentPosition();
            im_playOrPause.setImageResource(R.drawable.ic_play_pause);
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
            mediaPlayer.seekTo(time);
            im_playOrPause.setImageResource(R.drawable.ic_play_running);
        }
    }

    public String ShowTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    class LrcTask extends TimerTask {
        @Override
        public void run() {
            //获取歌曲播放的位置
            long timePassed = 0;
            if (mediaPlayer != null) {
                timePassed = mediaPlayer.getCurrentPosition();
            }
            final long finalTimePassed = timePassed;
            SongDitailActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //滚动歌词
                    mlrcview.seekLrcToTime(finalTimePassed);
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
        mediaPlayer.release();
        if (mTask != null) {
            mTask.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void toNextSong(int courent) {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mTask != null) {
            mTask.cancel();
        }
        if (courent < 0) {
            Toast.makeText(SongDitailActivity.this, "这是第一首歌哦", Toast.LENGTH_SHORT).show();
            return;
        }
      SongListModel    songListModelone = modelList.get(courent);
        if (songListModelone.isBeCollected() == true) {
            im_collect.setImageResource(R.drawable.ic_star_on);
        }
        String url = songListModelone.getPicUrl();
        picUrl = url;
        songName = songListModelone.getSongName();
        loader.bindBitmap(url, picImage);
        int songId = songListModelone.getSongID();
        sendlrcReqeuest(songId, mlrcview, builder);
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
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTask = new myLrcTask();
                        mTimer.scheduleAtFixedRate(mTask, 0, 1000);
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
if (mTimer!=null&&mTask!=null) {
    mTimer.cancel();
    mTask.cancel();
}
            }
        });
    }

    class myLrcTask extends TimerTask {
        @Override
        public void run() {
            //获取歌曲播放的位置
            final long timePassed = mediaPlayer.getCurrentPosition();
            SongDitailActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //滚动歌词

                    mlrcview.seekLrcToTime(timePassed);
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

    private void download(final String urlString) {
       new Thread(new Runnable() {
           @Override
           public void run() {

               Message message=Message.obtain();
               message.what=1;
               handler.sendMessage(message);

               try {
                   URL url = new URL(urlString);
                   URLConnection conn = url.openConnection();
                   InputStream is = conn.getInputStream();
                   int contentLength = conn.getContentLength();
                   Log.e(TAG, "contentLength = " + contentLength);
                   String dirName =MD5Encoder.encode(urlString);
                   File file = new File(CACHE_PATH,dirName);
                   if (!file.exists()) {
                       file.mkdir();
                   }
                   String fileName = dirName + "ELF.apk";
                   File file1 = new File(fileName);
                   if (file1.exists()) {
                       file1.delete();
                   }
                   byte[] bs = new byte[1024];
                   int len;
                   OutputStream os = new FileOutputStream(fileName);
                   while ((len = is.read(bs)) != -1) {
                       os.write(bs, 0, len);
                   }
                   Log.e(TAG, "download-finish");
                   os.close();
                   is.close();
                 Message message1=Message.obtain();
                message.what=2;
                   handler.sendMessage(message1);
               } catch (Exception e) {
                   e.printStackTrace();

               }
           }
       }).start();

    }

    static class MD5Encoder {
        public static String encode(String string) throws Exception {
            byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) {
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        }
    }
}