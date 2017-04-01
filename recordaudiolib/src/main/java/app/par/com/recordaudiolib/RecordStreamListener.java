package app.par.com.recordaudiolib;

/**
 * Created by wang_dafa on 2017/3/21.
 * 获取录音的音频流,用于拓展的处理
 */
public interface RecordStreamListener {
    void recordOfByte(byte[] data, int begin, int end);
}
