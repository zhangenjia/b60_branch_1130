package com.adayo.app.launcher.communicationbase;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SouceUtil {

    private static final String TAG = "SouceUtil";

    public static void fileCopyToLocalResources(String filename, Context context) {

        String dexPath = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        File file = null;
        String external = "";
        System.out.println(external);
        if (sdCardExist) {
            dexPath = "sdcard/adayo/home/bg/" + filename;///sdcard/123.jpg
            Log.d(TAG, "fileCopyToLocalResources: "+dexPath);
            file = new File(dexPath);
            if (!file.exists()) {
                Log.d(TAG, "fileCopyToLocalResources:aaaaa ");
                return;
            } else {
                Log.d(TAG, "fileCopyToLocalResources: bbbbb");
            }
        }


        if (null == file) {
            System.out.println("文件不存在！");
            external = System.getenv("EXTERNAL_STORAGE");
            file = new File(external +"/"+ filename);
        }
        Log.d(TAG, "fileCopyToLocalResources: cccccc");
        String absoultepath = context.getFilesDir().getParentFile().getAbsolutePath() + "/app_" + "inDir" + "/"+filename;
        Log.d(TAG, "fileCopyToLocalResources: "+absoultepath);
        File sofiledir = context.getDir("inDir", context.MODE_PRIVATE);
        File sofile = null;
        try {
            System.out.println("开始复制文件" + file.getAbsolutePath() + ":" + file.length() + "字节");
            FileInputStream fi = new FileInputStream(file);
            sofile = new File(sofiledir.getAbsolutePath() +"/"+ filename);
            if (!sofile.exists()) {
                sofile.createNewFile();
            } else {
                sofile.delete();
                sofile = new File(sofiledir.getAbsolutePath() +"/" +filename);
                sofile.createNewFile();
            }
            FileOutputStream fo = new FileOutputStream(sofile);
            byte[] buffer = new byte[1024];
            int t = 0;
            while ((t = fi.read(buffer)) != -1) {
                fo.write(buffer, 0, t);
            }
            fo.flush();
            fo.close();
            fi.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "fileCopyToLocalResources: ccccccccccccc");
        if (sofile.length() == file.length()) {
            System.out.println("复制成功！" + filename + "文件大小为:" + sofile.length());
            Log.d(TAG, "fileCopyToLocalResources: dddddddd"+sofile.getAbsolutePath()+"  "+sofile.length());
        } else {
            System.out.println("复制失败！");
            Log.d(TAG, "fileCopyToLocalResources: eeeeeeee");
        }

    }

    public static String getLocalResourcePath(Context context) {
        File sofiledir = context.getDir("inDir", context.MODE_PRIVATE);
        Log.d(TAG, "getBitMap: " + sofiledir.getAbsolutePath());
        return sofiledir.getAbsolutePath();

    }
}
