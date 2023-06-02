package com.adayo.app.dvr.constant;

import android.content.pm.ApplicationInfo;

public class Constant {

    public static final String PACKAGE_NAME = "com.adayo.app.dvr";

    public static final int COMMON_ON = 1;
    public static final int COMMON_OFF = 0;

    //Page
    public static final int PAGE_RECORD = 0;
    public static final int PAGE_PLAYBACK = 1;
    public static final int PAGE_EDIT = 2;
    public static final int PAGE_REPLAY = 3;
    public static final int PAGE_SETTING = 4;
    public static final int PAGE_NOTICE = 5;
    public static final int PAGE_PLAPWILL = 6;
    public static final int PAGE_EDIT_WILL = 7;
    public static final int PAGE_OPEN_ON = 8;

    //显示模式
    public static final int PREVIEW_MODE = 1;
    public static final int PLAYBACK_MODE = 2;

    //防抖延迟秒
    public static final int ANTI_SHAKE = 500;

    //Player
    public static final String PLAY_TYPE = "type";
    public static final String PLAY_TYPE_PHOTO = "type_photo";
    public static final String PLAY_TYPE_VIDEO = "type_video";
    public static final String PLAY_TYPE_EMERGENCY_VIDEO = "type_emergency_video";
    public static final String PLAY_TYPE_ATWILL = "type_atwill";
    public static final String PLAY_TYPE_ATWILL_VIDEO = "type_atwill_video";
    public static final String PLAY_TYPE_ATWILL_PHOTO = "type_atwill_photo";

    public static final String MODE = "mode";
    public static final String NORMAL = "normal";
    public static final String EMERGENCY = "emergency";
    public static final String ATWILL = "atwill";

    public static final int DVR_CONNECT_STS_NOL = 0;
    public static final int DVR_CONNECT_STS_MIS = 1;
    public static final int DVR_CONNECT_STS_INIT = 2;



    //接口传值
    public static final int DVR_FUNC_VALUE_KEYOPERAVALUE_NO_REQUEST = 1000;
    public static final int DVR_FUNC_VALUE_KEYOPERAVALUE_PAGEDOWN = 1001;
    public static final int DVR_FUNC_VALUE_KEYOPERAVALUE_PAGEUP = 1002;
    public static final int DVR_FUNC_VALUE_DELETETHECORRENTDOCUMENT = 1003;
    public static final int DVR_FUNC_VALUE_DELETETHESELECTEDDOCUMENT = 1004;
    public static final int DVR_FUNC_VALUE_MOVECURRENTTOEMERGENCY = 1005;
    public static final int DVR_FUNC_VALUE_MOVESELECTTOEMERGENCY = 1006;
    public static final int DVR_FUNC_VALUE_MOVECURRENTTONORMAL = 1007;
    public static final int DVR_FUNC_VALUE_MOVESELECTTONORMAL = 1008;
    public static final int DVR_FUNC_VALUE_PREV = 1009;
    public static final int DVR_FUNC_VALUE_NEXT = 1010;
    public static final int DVR_FUNC_VALUE_PLAY = 1011;
    public static final int DVR_FUNC_VALUE_PAUSE = 1012;
    public static final int DVR_FUNC_VALUE_QUIT = 1013;
    public static final int DVR_FUNC_VALUE_FASTFORWARD = 1014;
    public static final int DVR_FUNC_VALUE_REWIND = 1015;
    public static final int DVR_FUNC_VALUE_LOOP = 1016;
    public static final int DVR_FUNC_VALUE_POWERON = 1017;
    public static final int DVR_FUNC_VALUE_POWEROFF = 1018;
    public static final int DVR_FUNC_VALUE_PHOTOGRAPH = 1019;
    public static final int DVR_FUNC_VALUE_PHOTOSATWILL = 1020;
    public static final int DVR_FUNC_VALUE_VIDEOATWILL = 1021;
    public static final int DVR_FUNC_VALUE_UPLOADATWILL = 1022;
    public static final int DVR_FUNC_VALUE_PHOTOATPOINTGRAPH = 1023;
    public static final int DVR_FUNC_VALUE_STARTATPOINTVIDEO = 1024;
    public static final int DVR_FUNC_VALUE_CANCELATPOINTVIDEO = 1025;
    public static final int DVR_FUNC_VALUE_ENDATPOINTVIDEO = 1026;
    public static final int DVR_FUNC_VALUE_STARTEMERGENCYVIDEO = 1027;
    public static final int DVR_FUNC_VALUE_ENTEREMERGENCYVIDEO = 1028;
    public static final int DVR_FUNC_VALUE_SETRECORDING_ON = 1029;
    public static final int DVR_FUNC_VALUE_SETRECORDING_OFF = 1030;
    public static final int DVR_FUNC_VALUE_ENTERVIDEO = 1031;
    public static final int DVR_FUNC_VALUE_SETDISPLAYMODE_OFF = 1032;
    public static final int DVR_FUNC_VALUE_SETDISPLAYMODE_PREVIEWMODE = 1033;
    public static final int DVR_FUNC_VALUE_SETDISPLAYMODE_PLAYBACKMODE = 1034;
    public static final int DVR_FUNC_VALUE_ENTEREDITMODE = 1035;
    public static final int DVR_FUNC_VALUE_EXITEDITMODE = 1036;
    public static final int DVR_FUNC_VALUE_SELECTALL = 1037;
    public static final int DVR_FUNC_VALUE_CANCELSELECTALL = 1038;
    public static final int DVR_FUNC_VALUE_SELECTTHUMBNAIL_1 = 1039;
    public static final int DVR_FUNC_VALUE_SELECTTHUMBNAIL_2 = 1040;
    public static final int DVR_FUNC_VALUE_SELECTTHUMBNAIL_3 = 1041;
    public static final int DVR_FUNC_VALUE_SELECTTHUMBNAIL_4 = 1042;
    public static final int DVR_FUNC_VALUE_SELECTTHUMBNAIL_5 = 1043;
    public static final int DVR_FUNC_VALUE_SELECTTHUMBNAIL_6 = 1044;
    public static final int DVR_FUNC_VALUE_SELECTTHUMBNAIL_7 = 1045;
    public static final int DVR_FUNC_VALUE_SELECTTHUMBNAIL_8 = 1046;
    public static final int DVR_FUNC_VALUE_SELECTTHUMBNAIL_9 = 1047;
    public static final int DVR_FUNC_VALUE_SETRECORDTIME_1MINUTE = 1048;
    public static final int DVR_FUNC_VALUE_SETRECORDTIME_3MINUTE = 1049;
    public static final int DVR_FUNC_VALUE_SETRECORDTIME_5MINUTE = 1050;
    public static final int DVR_FUNC_VALUE_SETMICROPHONE = 1051;
    public static final int DVR_FUNC_VALUE_SETRESOLUTION_1920 = 1052;
    public static final int DVR_FUNC_VALUE_SETRESOLUTION_1280 = 1053;
    public static final int DVR_FUNC_VALUE_SETPARKSS_HIGH = 1054;
    public static final int DVR_FUNC_VALUE_SETPARKSS_MIDDLE = 1055;
    public static final int DVR_FUNC_VALUE_SETPARKSS_LOW = 1056;
    public static final int DVR_FUNC_VALUE_SETDRIVESS_HIGH = 1057;
    public static final int DVR_FUNC_VALUE_SETDRIVESS_MIDDLE = 1058;
    public static final int DVR_FUNC_VALUE_SETDRIVESS_LOW = 1059;
    public static final int DVR_FUNC_VALUE_SETDVRSYSTEMOPERATY_TFFORMATE = 1060;
    public static final int DVR_FUNC_VALUE_SETDVRSYSTEMOPERATY_SYSTEMUPDATE = 1061;
    public static final int DVR_FUNC_VALUE_SETDVRSYSTEMOPERATY_QUERYSOFTWAREVERSIONNUMBER = 1062;
    public static final int DVR_FUNC_VALUE_SETDVRSYSTEMOPERATY_RESTOREFACTORYSETTINGS = 1063;
    public static final int DVR_FUNC_VALUE_SETDVRSYSTEMOPERATY_QUERYTFCAPACITY = 1064;
    public static final int DVR_FUNC_VALUE_QUERYDVRAREA_PHOTOAREA = 1065;
    public static final int DVR_FUNC_VALUE_QUERYDVRAREA_VIDEOAREA = 1066;
    public static final int DVR_FUNC_VALUE_QUERYDVRAREA_STORAGEAREA = 1067;
    public static final int DVR_FUNC_VALUE_QUERYDVRAREA_EMERGENCYVIDEOAREA = 1068;
    public static final int DVR_FUNC_VALUE_QUERYDVRAREA_FREEZONEAREA = 1069;
    public static final int DVR_FUNC_VALUE_GETRECORDING_OFF = 1070;
    public static final int DVR_FUNC_VALUE_GETRECORDING_ON = 1071;
    public static final int DVR_FUNC_VALUE_GETDISPLAYMODE_PREVIEWMODE = 1072;
    public static final int DVR_FUNC_VALUE_GETDISPLAYMODE_PLAYBACKMODE = 1073;
    public static final int DVR_FUNC_VALUE_GETRECORDTIME_1MINUTE = 1074;
    public static final int DVR_FUNC_VALUE_GETRECORDTIME_3MINUTE = 1075;
    public static final int DVR_FUNC_VALUE_GETRECORDTIME_5MINUTE = 1076;
    public static final int DVR_FUNC_VALUE_GETRESOLUTION_1920 = 1077;
    public static final int DVR_FUNC_VALUE_GETRESOLUTION_1280 = 1078;
    public static final int DVR_FUNC_VALUE_GETPARKSS_HIGH = 1079;
    public static final int DVR_FUNC_VALUE_GETPARKSS_MIDDLE = 1080;
    public static final int DVR_FUNC_VALUE_GETPARKSS_LOW = 1081;
    public static final int DVR_FUNC_VALUE_GETDRIVESS_HIGH = 1082;
    public static final int DVR_FUNC_VALUE_GETDRIVESS_MIDDLE = 1083;
    public static final int DVR_FUNC_VALUE_GETDRIVESS_LOW = 1084;
    public static final int DVR_FUNC_VALUE_GETDVRSYSTEMUPDATESTATE_NOSD = 1085;
    public static final int DVR_FUNC_VALUE_GETDVRSYSTEMUPDATESTATE_NOUPDATESOFTWARE = 1086;
    public static final int DVR_FUNC_VALUE_GETDVRSYSTEMUPDATESTATE_START = 1087;
    public static final int DVR_FUNC_VALUE_GETDVRSYSTEMUPDATESTATE_SUCCESS = 1088;
    public static final int DVR_FUNC_VALUE_GETDVRSYSTEMUPDATESTATE_FAILURE = 1089;
    public static final int DVR_FUNC_VALUE_GETDVRSDCAPACITY_8G = 1090;
    public static final int DVR_FUNC_VALUE_GETDVRSDCAPACITY_16G = 1091;
    public static final int DVR_FUNC_VALUE_GETDVRSDCAPACITY_32G = 1092;
    public static final int DVR_FUNC_VALUE_GETDVRSDCAPACITY_64G = 1093;
    public static final int DVR_FUNC_VALUE_GETDVRSDCAPACITY_128G = 1094;
    public static final int DVR_FUNC_VALUE_GETDVRSDINTEGER = 1095;
    public static final int DVR_FUNC_VALUE_GETDVRSDDECIMAL = 1096;

    //Callback
    public static final int DVR_FUNC_CALLBACK_VALUE_PHOTOGRAGH_RESULT_OFF = 1097;
    public static final int DVR_FUNC_CALLBACK_VALUE_PHOTOGRAGH_RESULT_SUCCESS = 1098;
    public static final int DVR_FUNC_CALLBACK_VALUE_KEYOPERA_RESULT_SUCCESS = 1099;
    public static final int DVR_FUNC_CALLBACK_VALUE_KEYOPERA_RESULT_FAIL = 1100;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_PAGEDOWN = 1101;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_PAGEUP = 1102;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_TURNTHEPAGETOTHETOP = 1103;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_TURNTHEPAGETOTHEBOTTM = 1104;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_PLAY = 1105;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_DELETETHECORRENTDOCUMENT = 1106;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_MOVECORRENTDOCUMENTTOTHENONEDELETEAREA = 1107;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_DELETETHESELECTEDDOCUMENT = 1108;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_MOVETHESELECTEDDOCUMENTTOTHENONEDELETEAREA = 1109;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_DELETEALLDOCUMENTOFCORRENTDOCUMENTSTORE = 1110;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_PAUSE = 1111;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_LASTVIDEOORPHOTO = 1112;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_NEXTVIDEOORPHOTO = 1113;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_QUIT = 1114;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_FASTFORWARD = 1115;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_REWIND = 1116;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_LOOPTHECURRENTDOCUMENT = 1117;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_PHOTOGRAPH = 1118;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_POWERON = 1119;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_POWEROFF = 1120;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_EMERGENCYVIDEO = 1121;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_VIDEO = 1122;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_MOVECORRENTDOCUMENTTOTHENORMALAREA = 1123;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_MOVETHESELECTEDDOCUMENTTOTHENORMALAREA = 1124;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_ENTEREDITMODE = 1125;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_EXITEDITMODE = 1126;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_SELECTALLDOCUMENTOFCORRENTDOCUMENTSTORE = 1127;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_CANCELSELECTALLTHESELECTEDDOCUMENTOFCORRENTDOCUMENTSTORE = 1128;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_MICROPHONESWITCH = 1129;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_VIDEOATWILL = 1130;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_PHOTOSATWILL = 1131;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_UPLOADATWILL = 1132;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_PHOTOATPOINTGRAPH = 1133;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_STARTATPOINTVIDEO = 1134;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_CANCELATPOINTVIDEO = 1135;
    public static final int DVR_FUNC_CALLBACK_VALUE_OPERAVALUE_RESULT_ENDATPOINTVIDEO = 1136;
    public static final int DVR_FUNC_CALLBACK_VALUE_THUMBNAILOPERA_RESULT_CANCELSUCCESS = 1137;
    public static final int DVR_FUNC_CALLBACK_VALUE_THUMBNAILOPERA_RESULT_SELECTSUCCESS = 1138;
    public static final int DVR_FUNC_CALLBACK_VALUE_THUMBNAILOPERA_RESULT_OPERATIONFAIL = 1139;
    public static final int DVR_FUNC_CALLBACK_VALUE_RECORDTIME_RESULT_1MINUTE = 1140;
    public static final int DVR_FUNC_CALLBACK_VALUE_RECORDTIME_RESULT_3MINUTE = 1141;
    public static final int DVR_FUNC_CALLBACK_VALUE_RECORDTIME_RESULT_5MINUTE = 1142;
    public static final int DVR_FUNC_CALLBACK_VALUE_SYSTEMUPDATE_RESULT_NOSD = 1143;
    public static final int DVR_FUNC_CALLBACK_VALUE_SYSTEMUPDATE_RESULT_NOUPDATESOFTWARE = 1144;
    public static final int DVR_FUNC_CALLBACK_VALUE_SYSTEMUPDATE_RESULT_START = 1145;
    public static final int DVR_FUNC_CALLBACK_VALUE_SYSTEMUPDATE_RESULT_SUCCESS = 1146;
    public static final int DVR_FUNC_CALLBACK_VALUE_SYSTEMUPDATE_RESULT_FAILURE = 1147;
    public static final int DVR_FUNC_CALLBACK_VALUE_TFCAPACITY_RESULT_8G = 1148;
    public static final int DVR_FUNC_CALLBACK_VALUE_TFCAPACITY_RESULT_16G = 1149;
    public static final int DVR_FUNC_CALLBACK_VALUE_TFCAPACITY_RESULT_32G = 1150;
    public static final int DVR_FUNC_CALLBACK_VALUE_TFCAPACITY_RESULT_64G = 1151;
    public static final int DVR_FUNC_CALLBACK_VALUE_TFCAPACITY_RESULT_128G = 1152;
    public static final int DVR_FUNC_CALLBACK_VALUE_RFS_RESULT_SUCCESS = 1153;
    public static final int DVR_FUNC_CALLBACK_VALUE_RFS_RESULT_FAIL = 1154;
    public static final int DVR_FUNC_CALLBACK_VALUE_RECORDING_RESULT_OFF = 1155;
    public static final int DVR_FUNC_CALLBACK_VALUE_RECORDING_RESULT_ON = 1156;
    public static final int DVR_FUNC_CALLBACK_VALUE_DRIVESS_RESULT_HIGH = 1157;
    public static final int DVR_FUNC_CALLBACK_VALUE_DRIVESS_RESULT_MIDDLE = 1158;
    public static final int DVR_FUNC_CALLBACK_VALUE_DRIVESS_RESULT_LOW = 1159;
    public static final int DVR_FUNC_CALLBACK_VALUE_TFFORMATE_RESULT_NOFORMATE = 1160;
    public static final int DVR_FUNC_CALLBACK_VALUE_TFFORMATE_RESULT_SUCCESS = 1161;
    public static final int DVR_FUNC_CALLBACK_VALUE_TFPTY_RESULT_PHOTOAREA = 1162;
    public static final int DVR_FUNC_CALLBACK_VALUE_TFPTY_RESULT_VIDEOAREA = 1163;
    public static final int DVR_FUNC_CALLBACK_VALUE_TFPTY_RESULT_STORAGEAREA = 1164;
    public static final int DVR_FUNC_CALLBACK_VALUE_SVNHB_RESULT_H = 1165;
    public static final int DVR_FUNC_CALLBACK_VALUE_PARKSS_RESULT_HIGHL = 1166;
    public static final int DVR_FUNC_CALLBACK_VALUE_PARKSS_RESULT_MIDDLE = 1167;
    public static final int DVR_FUNC_CALLBACK_VALUE_PARKSS_RESULT_LOW = 1168;
    public static final int DVR_FUNC_CALLBACK_VALUE_SVNMB_RESULT_M = 1169;
    public static final int DVR_FUNC_CALLBACK_VALUE_SVNLB_RESULT_L = 1170;
    public static final int DVR_FUNC_CALLBACK_VALUE_THUMBNAILSELECT_RESULT_1 = 1171;
    public static final int DVR_FUNC_CALLBACK_VALUE_THUMBNAILSELECT_RESULT_2 = 1172;
    public static final int DVR_FUNC_CALLBACK_VALUE_THUMBNAILSELECT_RESULT_3 = 1173;
    public static final int DVR_FUNC_CALLBACK_VALUE_THUMBNAILSELECT_RESULT_4 = 1174;
    public static final int DVR_FUNC_CALLBACK_VALUE_THUMBNAILSELECT_RESULT_5 = 1175;
    public static final int DVR_FUNC_CALLBACK_VALUE_THUMBNAILSELECT_RESULT_6 = 1176;
    public static final int DVR_FUNC_CALLBACK_VALUE_THUMBNAILSELECT_RESULT_7 = 1177;
    public static final int DVR_FUNC_CALLBACK_VALUE_THUMBNAILSELECT_RESULT_8 = 1178;
    public static final int DVR_FUNC_CALLBACK_VALUE_THUMBNAILSELECT_RESULT_9 = 1179;
    public static final int DVR_FUNC_CALLBACK_VALUE_CURRENTTHUMBNAIL_RESULT_1 = 1180;
    public static final int DVR_FUNC_CALLBACK_VALUE_CURRENTTHUMBNAIL_RESULT_2 = 1181;
    public static final int DVR_FUNC_CALLBACK_VALUE_CURRENTTHUMBNAIL_RESULT_3 = 1182;
    public static final int DVR_FUNC_CALLBACK_VALUE_CURRENTTHUMBNAIL_RESULT_4 = 1183;
    public static final int DVR_FUNC_CALLBACK_VALUE_CURRENTTHUMBNAIL_RESULT_5 = 1184;
    public static final int DVR_FUNC_CALLBACK_VALUE_CURRENTTHUMBNAIL_RESULT_6 = 1185;
    public static final int DVR_FUNC_CALLBACK_VALUE_CURRENTTHUMBNAIL_RESULT_7 = 1186;
    public static final int DVR_FUNC_CALLBACK_VALUE_CURRENTTHUMBNAIL_RESULT_8 = 1187;
    public static final int DVR_FUNC_CALLBACK_VALUE_CURRENTTHUMBNAIL_RESULT_9 = 1188;
    public static final int DVR_FUNC_CALLBACK_VALUE_TFPC_RESULT = 1189;
    public static final int DVR_FUNC_CALLBACK_VALUE_CP_RESULT_NOREQUEST = 1190;
    public static final int DVR_FUNC_CALLBACK_VALUE_CP_RESULT_REQUEST = 1191;
    public static final int DVR_FUNC_CALLBACK_VALUE_TFPC_DECIMAL_RESULT = 1192;
    public static final int DVR_FUNC_CALLBACK_VALUE_PHOTOKEYOPERA_RESULT_PHOTOSUCCESS = 1193;
    public static final int DVR_FUNC_CALLBACK_VALUE_PHOTOKEYOPERA_RESULT_VIDEOSTART = 1194;
    public static final int DVR_FUNC_CALLBACK_VALUE_PHOTOKEYOPERA_RESULT_VIDEOSUCCESS = 1195;
    public static final int DVR_FUNC_CALLBACK_VALUE_PHOTOKEYOPERA_RESULT_OPERATIONFAIL = 1196;
    public static final int DVR_FUNC_CALLBACK_VALUE_MEDIAUPLOAD_RESULT_PHOTOGATHERUPLOADSUCCESS = 1197;
    public static final int DVR_FUNC_CALLBACK_VALUE_MEDIAUPLOAD_RESULT_VIDEOGATHERUPLOADSUCCESS = 1198;
    public static final int DVR_FUNC_CALLBACK_VALUE_MEDIAUPLOAD_RESULT_OPERATIONFAIL = 1199;
    public static final int DVR_FUNC_CALLBACK_VALUE_DISPLAYMODE_RESULT_OFF = 1200;
    public static final int DVR_FUNC_CALLBACK_VALUE_DISPLAYMODE_RESULT_PREVIEWMODE = 1201;
    public static final int DVR_FUNC_CALLBACK_VALUE_DISPLAYMODE_RESULT_PLAYBACKMODE = 1202;
    public static final int DVR_FUNC_CALLBACK_VALUE_DISPLAYMODE_RESULT_RESERVE = 1203;

    //Off On
    public static final int COMMON_RECORDING_OFF = DVR_FUNC_VALUE_SETRECORDING_OFF;
    public static final int COMMON_RECORDING_ON = DVR_FUNC_VALUE_SETRECORDING_ON;

    //Display mode set
    public static final int DISPLAY_MODE_SET_NO_REQUEST = 0;
    public static final int DISPLAY_MODE_SET_OFF = DVR_FUNC_VALUE_SETDISPLAYMODE_OFF;
    public static final int DISPLAY_MODE_SET_PREVIEW = DVR_FUNC_VALUE_SETDISPLAYMODE_PREVIEWMODE;
    public static final int DISPLAY_MODE_SET_PLAYBACK = DVR_FUNC_VALUE_SETDISPLAYMODE_PLAYBACKMODE;
    public static final int DISPLAY_MODE_SET_OTHER = -1;

    //SD卡总容量
    public static final String DVR_VALUE_TFCAPACITY_RESULT_8G = "8";
    public static final String DVR_VALUE_TFCAPACITY_RESULT_16G = "16";
    public static final String DVR_VALUE_TFCAPACITY_RESULT_32G = "32";
    public static final String DVR_VALUE_TFCAPACITY_RESULT_64G = "64";
    public static final String DVR_VALUE_TFCAPACITY_RESULT_128G = "128";


    //返回值
    public static final String result_value = "Result_Value";
    public static final String value = "value";
    public static final String DVR_Common_Cb_Func = "DVR_Common_Cb_Func";
    public static final String DVR_St_Recording = "DVR_St_Recording";
    public static final String DVR_St_DisplayMode = "DVR_St_DisplayMode";
    public static final String callback_recording_result="callback_recording_result";
    public static final String callback_entergraph_result = "callback_entergraph_result";
    public static final String callback_startemergencyvideo_result = "callback_startemergencyvideo_result";
    public static final String callback_enterEmergencyVideo_result = "callback_enterEmergencyVideo_result";
    public static final String callback_poweroff_result = "callback_poweroff_result";
    public static final String callback_poweron_result = "callback_poweron_result";
    public static final String callback_entereditmode_result = "callback_entereditmode_result";
    public static final String callback_exiteditmode_result = "callback_exiteditmode_result";
    public static final String callback_selectall_result = "callback_selectall_result";
    public static final String callback_cancelselectall_result = "callback_cancelselectall_result";
    public static final String callback_selectthumbnail_result = "callback_selectthumbnail_result";
    public static final String callback_cancelthumbnail_result = "callback_cancelthumbnail_result";

    public static final String DVR_Thumbnail_Cb_Func = "DVR_Thumbnail_Cb_Func";
    public static final String DVR_St_CurrentThumbnail = "DVR_St_CurrentThumbnail";
    public static final String DVR_St_ThumbnailSel = "DVR_St_ThumbnailSel";
    public static final String DVR_St_ThumbnailOpera = "DVR_St_ThumbnailOpera";

    public static final String DVR_Play = "DVR_Play";
    public static final String DVR_Pause = "DVR_Pause";
    public static final String DVR_NextVideoOrPhoto = "DVR_NextVideoOrPhoto";
    public static final String DVR_LastVideoOrPhoto = "DVR_LastVideoOrPhoto";

    public static final String DVR_Page_Num_Cb_Func="DVR_Page_Num_Cb_Func";
    public static final String DVR_St_TotalPage = "DVR_St_TotalPage";
    public static final String DVR_St_CurrentPage = "DVR_St_CurrentPage";

    public static final String callback_micswitch_result = "DVR_MicSwitch";
    public static final String callback_playback_recording_result = "callback_playback_recording_result";



    public static final String callback_pageup_result = "callback_pageup_result";
    public static final String callback_pagedown_result = "callback_pagedown_result";
    public static final String callback_deletecurrentdoc_result = "callback_deletecurrentdoc_result";
    public static final String callback_deleteselectdoc_result = "callback_deleteselectdoc_result";


    public static final String callback_movecurrenttoemergency_result = "callback_movecurrenttoemergency_result";
    public static final String callback_moveselecttoemergency_result = "callback_moveselecttoemergency_result";
    public static final String callback_movecurrenttonormal_result = "callback_movecurrenttonormal_result";
    public static final String callback_moveselecttonormal_result = "callback_moveselecttonormal_result";

    public static final String DVR_SystemInfo = "DVR_SystemInfo";
    public static final String Resolution = "Resolution";
    public static final String RecordTime = "RecordTime";
    public static final String DriveMode = "DriveMode";
    public static final String ParkMode = "ParkMode";
    public static final String TotalTfcapacity = "TotalTfcapacity";

    public static final String PhotoCapacity = "PhotoCapacity";
    public static final String VideoCapacity = "VideoCapacity";
    public static final String EmergencyVideoCapacity = "EmergencyVideoCapacity";
    public static final String StorageCapacity = "StorageCapacity";
    public static final String SystemVersion = "SystemVersion";
    public static final String TakePhotoatwillCapacity = "TakePhotoatwillCapacity";



    public static final String tag_video = "VIDEO";
    public static final String tag_photo = "PHOTO";
    public static final String tag_atwill = "ATWILL";

    public static final int DVR_SETTING_SD_INFO = 1;
    public static final int SRC_SHAREINFO_NUM = 74;

    public static final String DVR_PageDownCbFunc = "DVR_PageDownCbFunc";
    public static final String DVR_PageUpCbFunc = "DVR_PageUpCbFunc";
    public static final String DVR_TurnPageToTopCbFunc = "DVR_TurnPageToTopCbFunc";
    public static final String DVR_TurnPageToBottomCbFunc = "DVR_TurnPageToBottomCbFunc";
    public static final String DVR_DeleteCurDocument = "DVR_DeleteCurDocument";
    public static final String DVR_MoveCurDocumentToNoneDeleteArea = "DVR_MoveCurDocumentToNoneDeleteArea";
    public static final String DVR_DeleteTheSelectedDocument = "DVR_DeleteTheSelectedDocument";
    public static final String DVR_MoveSelectedDocToDeleteArea = "DVR_MoveSelectedDocToDeleteArea";
    public static final String DVR_DeleteAllDocument = "DVR_DeleteAllDocument";
    public static final String DVR_Quit = "DVR_Quit";
    public static final String DVR_FastForward = "DVR_FastForward";
    public static final String DVR_Rewind = "DVR_Rewind";
    public static final String DVR_LoopTheCurDocument = "DVR_LoopTheCurDocument";
    public static final String DVR_Photograph = "DVR_Photograph";
    public static final String DVR_Power_On = "DVR_Power_On";
    public static final String DVR_Power_OFF = "DVR_Power_OFF";
    public static final String DVR_EmergencyVideo = "DVR_EmergencyVideo";
    public static final String DVR_Video = "DVR_Video";
    public static final String DVR_MoveCurDocuToTheNorArea = "DVR_MoveCurDocuToTheNorArea";
    public static final String DVR_MoveSelectedDocToNorArea = "DVR_MoveSelectedDocToNorArea";
    public static final String DVR_EnterEditMode = "DVR_EnterEditMode";
    public static final String DVR_ExitEditMode = "DVR_ExitEditMode";
    public static final String DVR_SelectAllDocCorDocStore = "DVR_SelectAllDocCorDocStore";
    public static final String DVR_CancelSelAllSelectedDoc = "DVR_CancelSelAllSelectedDoc";
    public static final String DVR_MicSwitch = "DVR_MicSwitch";
    public static final String DVR_TakeVideoAtWill = "DVR_TakeVideoAtWill";
    public static final String DVR_TakePhotosAtWill = "DVR_TakePhotosAtWill";
    public static final String DVR_UploadAsYouLike = "DVR_UploadAsYouLike";
    public static final String DVR_PhotoHitPoint = "DVR_PhotoHitPoint";
    public static final String DVR_VideoHitPointBegin = "DVR_VideoHitPointBegin";
    public static final String DVR_VideoHitPointCancel = "DVR_VideoHitPointCancel";
    public static final String DVR_VideoHitPointEnd = "DVR_VideoHitPointEnd";
    public static final String DVR_TakeVideoAtWillByVoice = "DVR_TakeVideoAtWillByVoice";
    public static final String DVR_TakePhotosAtWillByVoice = "DVR_TakePhotosAtWillByVoice";
    public static final String DVR_St_Select_ThumbnailSel = "DVR_St_Select_ThumbnailSel";
    public static final String DVR_St_Cancel_ThumbnailSel = "DVR_St_Cancel_ThumbnailSel";

    public static final String ADAYO_AVM_CAMERA_FRAME_READY = "ADAYO_AVM_CAMERA_FRAME_READY";
    public static final String ADAYO_AVM_CAMERA_SIGNAL_STATE = "ADAYO_AVM_CAMERA_SIGNAL_STATE";

    public static final int SHARER_ID_SOURCE = 14;
    public static final int SHARER_AVM_RVC_SOURCE = 16;
    public static final String SHARER_KEY_UID_OF_SOURCE = "UID";
    public static final int SHARER_ID_GENERAL = 11;
    //注意, 此处, true: 已关闭行车视频(已开启行车警告); false: 已开启行车视频(已关闭行车警告)
    public static final String SHARER_KEY_DRIVING_WARNING_SWITCH_OF_GENERAL = "driving_watch_video_switch";
    public static final String SHARER_KEY_OVER_SPEED_OF_GENERAL = "is_speeding";
    public static final String DVR_Will_EnterEditMode = "DVR_Will_EnterEditMode";
    public static final String DVR_will_ExitEditMode = "DVR_will_ExitEditMode";
    public static final String DVR_CB_KEY_ST_PLAYVIDEO = "DVR_CB_KEY_ST_PLAYVIDEO";
    public static final String DVR_SdcardFormat = "DVR_SdcardFormat";
    public static final String DVR_ReStoreFactory = "DVR_ReStoreFactory";
    public static final String ON_CONFIGURATION_CHANGED = "ON_CONFIGURATION_CHANGED";
    public static final String ACTION_TEST_RECEIVER = "com.adayo.broadcast.test.msg";
    public static final String DVR_CB_KEY_PHOTOKEYOPERA = "DVR_CB_KEY_PHOTOKEYOPERA";

}
