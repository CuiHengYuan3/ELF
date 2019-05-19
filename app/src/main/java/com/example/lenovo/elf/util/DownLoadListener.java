package com.example.lenovo.elf.util;

public interface DownLoadListener {
    void  onProgress(int progress);
    void onFailed();
    void onSucccess();
    void onPaused();
    void onCancled();

}
