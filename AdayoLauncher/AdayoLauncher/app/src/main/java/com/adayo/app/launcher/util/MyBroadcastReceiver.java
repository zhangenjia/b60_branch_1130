package com.adayo.app.launcher.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.adayo.app.launcher.communicationbase.SouceUtil;
import com.adayo.app.launcher.communicationbase.WrapperUtil;
import java.io.File;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private String fileUrl = "";
    private static final String TAG = "MyBroadcastReceiver";
    private boolean isSdCardExist;
    private String stringsdpath;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        if ("com.broadcast.home.bgchange".equals(intent.getAction())) {
            String filePath = intent.getStringExtra("photoPath");
            String fileName = intent.getStringExtra("photoName");
            String needResume = intent.getStringExtra("needResume");
            Log.d(TAG, "onReceive: filePath " + filePath + " fileName " + fileName + " needResume " + needResume);

            if ("resume".equals(needResume)) {
                WrapperUtil.getInstance().resumeDefault();
                SystemPropertiesUtil.getInstance().setProperty("persist.wallpaperType", "");
            } else {
                SouceUtil.fileCopyToLocalResources(fileName,context);//复制
                SystemPropertiesUtil.getInstance().setProperty("persist.wallpaperType", fileName);//保存到sp

                String localResourcePath = SouceUtil.getLocalResourcePath(context);
                Log.d(TAG, "onReceive: "+localResourcePath);
                Bitmap bm = BitmapFactory.decodeFile(SouceUtil.getLocalResourcePath(context)+"/"+fileName);
                //将图片显示到ImageView中

                WrapperUtil.getInstance().setWallPaper(bm);


            }
        }
    }
}
