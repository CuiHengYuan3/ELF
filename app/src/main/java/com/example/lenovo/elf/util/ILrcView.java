package com.example.lenovo.elf.util;

import java.util.List;

public interface ILrcView {
    void setLrc(List<LrcRow> lrcRows);

    /**
     * 音乐播放的时候调用该方法滚动歌词，高亮正在播放的那句歌词
     */
    void seekLrcToTime(long time);



}
