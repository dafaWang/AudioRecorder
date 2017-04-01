package app.par.com.pausableaudiorecorder;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.par.com.recordaudiolib.AudioRecorder;
import app.par.com.recordaudiolib.FileUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AudioRecorder.OnRecordingListener {

    private ProgressBar progress;
    private Button mRecordStart, mRecordStop, mPlayStart,mPlayStartAmr;
    private static int MAX_DURATION = 240000;
    private AudioRecorder mAudioRecorder;
    private boolean isRecording = false;
    private String tempAudioPath;
    private boolean isRecordFinish = false;
    private String mAudioPath;
    private MediaPlayer mPlayer;
    private File armFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (ProgressBar) findViewById(R.id.progress);
        mRecordStart = (Button) findViewById(R.id.start);
        mRecordStop = (Button) findViewById(R.id.complete);
        mPlayStart = (Button) findViewById(R.id.play);
        mPlayStartAmr = (Button) findViewById(R.id.playamr);
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayer.stop();
                mPlayer.reset();
            }
        });
        mAudioRecorder = AudioRecorder.getInstance();
        mAudioRecorder.setmMaxDuration(MAX_DURATION);
        mAudioRecorder.setOnRecordingListener(this);
        mRecordStart.setOnClickListener(this);
        mRecordStop.setOnClickListener(this);
        mPlayStart.setOnClickListener(this);
        mPlayStartAmr.setOnClickListener(this);
        progress.setMax(MAX_DURATION);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                if (!isRecording) {
                    //开始录制
                    isRecordFinish = false;
                    if (mAudioRecorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY) {
                        //初始化录音
                        tempAudioPath = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                        mAudioRecorder.createDefaultAudio(tempAudioPath);
                    }
                    mAudioRecorder.startRecord(null);
                    mRecordStart.setText("暂停");
                    isRecording = true;
                } else {
//                    暂停录制
                    mAudioRecorder.pauseRecord();
                    mRecordStart.setText("继续");
                    isRecording = false;
                }
                break;
            case R.id.complete:
                if(mAudioRecorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY || mAudioRecorder.getStatus() == AudioRecorder.Status.STATUS_READY){
                    Toast.makeText(MainActivity.this, "录音尚未开始", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    mAudioRecorder.stopRecord();
                    mRecordStart.setText("开始");
                }
                isRecording = false;
                isRecordFinish = true;
                mAudioPath = FileUtils.getWavFileAbsolutePath(tempAudioPath);
                break;
            case R.id.play:
                //播放wav
                if(mPlayer.isPlaying()){
                    mPlayer.reset();
                }
                if(isRecordFinish){
                    if(TextUtils.isEmpty(mAudioPath)){
                        Toast.makeText(MainActivity.this, "为找到录制文件", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        try {
                            mPlayer.setDataSource(mAudioPath);
                            mPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this, "还没有完成录制", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.playamr:
                //播放amr
                if(mPlayer.isPlaying()){
                    mPlayer.reset();
                }
                if(isRecordFinish){
                    if(TextUtils.isEmpty(armFilePath.getAbsolutePath())){
                        Toast.makeText(MainActivity.this, "为找到录制文件", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        armFilePath = new File(mAudioPath.replace(".wav", ".amr"));
                        FileUtils.systemWav2Amr(mAudioPath,armFilePath.getAbsolutePath());
                        try {
                            mPlayer.setDataSource(armFilePath.getAbsolutePath());
                            mPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this, "还没有完成录制", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }

    @Override
    public void onRecording(int duration) {
        //监听录制进度
        progress.setProgress(duration);
    }
}
