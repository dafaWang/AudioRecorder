# AudioRecorder-Android


一个可以暂停-继续的音频录制库



## 特点:
1.可暂停录制/继续录制

2.默认pcm片转换为wav格式音频文件

3.FileUtils中添加了wav格式转amr格式方法

## 使用:

   [直接下载jar包](https://github.com/dafaWang/pausableAudioRecorder/releases/download/1.0/pausableAudioRecorder.jar)


 也可以
 
     compile 'com.wangdafa:PausableRecordLib:1.0.1'
     compile 'com.wangdafa:PausableRecordLib:1.0.1@aar'

或者clone下来项目直接做修改

//设置pcm临时文件路径


    if (mAudioRecorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY) {
                        //初始化录音

        tempAudioPath = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

        mAudioRecorder.createDefaultAudio(tempAudioPath);

    }
//开始录音
      
      mAudioRecorder.startRecord(null);
      
//暂停录音

      mAudioRecorder.pauseRecord();
      
//停止录音

      mAudioRecorder.stopRecord();


//临时文件转换wav格式音频文件


     mAudioPath = FileUtils.getWavFileAbsolutePath(tempAudioPath);



//wav格式文件转amr格式文件


     armFilePath = new File(mAudioPath.replace(".wav", ".amr"));

     FileUtils.systemWav2Amr(mAudioPath,armFilePath.getAbsolutePath());




### qq:_245258365_
### email:_mocen_dafa@163.com_




