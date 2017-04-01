package app.par.com.recordaudiolib;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang_dafa on 2017/3/21.
 * 管理录音文件的类
 */
public class FileUtils {


    private static String rootPath = "pauseRecordDemo";
    //原始文件(不能播放)
    private final static String AUDIO_PCM_BASEPATH = "/" + rootPath + "/pcm/";
    //可播放的高质量音频文件
    private final static String AUDIO_WAV_BASEPATH = "/" + rootPath + "/wav/";

    final private static byte[] header = new byte[]{0x23, 0x21, 0x41, 0x4D, 0x52, 0x0A};

    private static void setRootPath(String rootPath) {
        FileUtils.rootPath = rootPath;
    }

    public static String getPcmFileAbsolutePath(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            throw new NullPointerException("fileName isEmpty");
        }
        if (!isSdcardExit()) {
            throw new IllegalStateException("sd card no found");
        }
        String mAudioRawPath = "";
        if (isSdcardExit()) {
            if (!fileName.endsWith(".pcm")) {
                fileName = fileName + ".pcm";
            }
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_PCM_BASEPATH;
            File file = new File(fileBasePath);
            //创建目录
            if (!file.exists()) {
                file.mkdirs();
            }
            mAudioRawPath = fileBasePath + fileName;
        }

        return mAudioRawPath;
    }

    public static String getWavFileAbsolutePath(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName can't be null");
        }
        if (!isSdcardExit()) {
            throw new IllegalStateException("sd card no found");
        }

        String mAudioWavPath = "";
        if (isSdcardExit()) {
            if (!fileName.endsWith(".wav")) {
                fileName = fileName + ".wav";
            }
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;
            File file = new File(fileBasePath);
            //创建目录
            if (!file.exists()) {
                file.mkdirs();
            }
            mAudioWavPath = fileBasePath + fileName;
        }
        return mAudioWavPath;
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取全部pcm文件列表
     *
     * @return
     */
    public static List<File> getPcmFiles() {
        List<File> list = new ArrayList<>();
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_PCM_BASEPATH;

        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {

            File[] files = rootFile.listFiles();
            for (File file : files) {
                list.add(file);
            }

        }
        return list;

    }

    /**
     * 获取全部wav文件列表
     *
     * @return
     */
    public static List<File> getWavFiles() {
        List<File> list = new ArrayList<>();
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;

        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                list.add(file);
            }

        }
        return list;
    }


    public static void systemWav2Amr(String inPath,String outPath){
        try {
            FileInputStream fileInputStream = new FileInputStream(inPath);
            FileOutputStream fileoutputStream = new FileOutputStream(outPath);
            // 获得Class
            Class<?> cls = Class.forName("android.media.AmrInputStream");
            // 通过Class获得所对应对象的方法
            Method[] methods = cls.getMethods();
            // 输出每个方法名
            fileoutputStream.write(header);
            Constructor<?> con = cls.getConstructor(InputStream.class);
            Object obj = con.newInstance(fileInputStream);
            for (Method method : methods) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if ("read".equals(method.getName())
                        && parameterTypes.length == 3) {
                    byte[] buf = new byte[1024];
                    int len = 0;
                    while ((len = (int) method.invoke(obj, buf, 0, 1024)) > 0) {
                        fileoutputStream.write(buf, 0, len);
                    }
                    break;
                }
            }
            for (Method method : methods) {
                if ("close".equals(method.getName())) {
                    method.invoke(obj);
                    break;
                }
            }
            fileoutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
