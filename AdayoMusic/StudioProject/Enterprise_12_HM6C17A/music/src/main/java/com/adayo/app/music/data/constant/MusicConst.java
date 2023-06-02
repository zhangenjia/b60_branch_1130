package com.adayo.app.music.data.constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicConst {
    public static final String FRAG_TAG_LO_MUSIC = "lo_music";
    public static final String FRAG_TAG_BT_MUSIC = "bt_music";
    public static final List<String> FRAG_TAG_MAIN_LIST;
    public static final String FRAG_TAG_LO_MUSIC_DETAIL = "lo_music_detail";
    public static final String FRAG_TAG_LO_MUSIC_BROWSE = "lo_music_browse";
    public static final String FRAG_TAG_LO_MUSIC_BROWSE_ALL = "lo_music_browse_all";
    public static final String FRAG_TAG_LO_MUSIC_BROWSE_DIR = "lo_music_browse_dir";
    public static final String FRAG_TAG_LO_MUSIC_BROWSE_ALBUM = "lo_music_browse_album";
    public static final String FRAG_TAG_LO_MUSIC_BROWSE_ARTIST = "lo_music_browse_artist";
    public static final List<String> FRAG_TAG_BROWSE_LIST;
    public static final String BDL_KEY_SOURCE_INFO = "map";
    public static final String SOURCE_MANAGER_KEY_SOURCE_TYPE = "SourceType";
    public static final String SOURCE_MANAGER_KEY_NEED_AUTO_PLAY = "need_auto_play";
    public static final int AUDIO_ZONE_CODE = 0;

    static {
        List<String> fragTagMainList = new ArrayList<>();
        fragTagMainList.add(FRAG_TAG_BT_MUSIC);
        fragTagMainList.add(FRAG_TAG_LO_MUSIC);
        FRAG_TAG_MAIN_LIST = Collections.unmodifiableList(fragTagMainList);
        List<String> fragTagBrowseList = new ArrayList<>();
        fragTagBrowseList.add(FRAG_TAG_LO_MUSIC_BROWSE_ALL);
        fragTagBrowseList.add(FRAG_TAG_LO_MUSIC_BROWSE_DIR);
        fragTagBrowseList.add(FRAG_TAG_LO_MUSIC_BROWSE_ALBUM);
        fragTagBrowseList.add(FRAG_TAG_LO_MUSIC_BROWSE_ARTIST);
        FRAG_TAG_BROWSE_LIST = Collections.unmodifiableList(fragTagBrowseList);
    }
}
