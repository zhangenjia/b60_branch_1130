package com.adayo.app.video.data.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoConst {
    public static final String FRAG_TAG_LO_VIDEO = "lo_video";
    public static final List<String> FRAG_TAG_MAIN_LIST;
    public static final String FRAG_TAG_LO_VIDEO_DETAIL = "lo_video_detail";
    public static final String FRAG_TAG_LO_VIDEO_BROWSE = "lo_video_browse";
    public static final String BDL_KEY_SOURCE_INFO = "map";
    public static final String SOURCE_MANAGER_KEY_NEED_AUTO_PLAY = "need_auto_play";
    public static final int AUDIO_ZONE_CODE = 0;

    static {
        List<String> fragTagMainList = new ArrayList<>();
        fragTagMainList.add(FRAG_TAG_LO_VIDEO);
        FRAG_TAG_MAIN_LIST = Collections.unmodifiableList(fragTagMainList);
    }
}
