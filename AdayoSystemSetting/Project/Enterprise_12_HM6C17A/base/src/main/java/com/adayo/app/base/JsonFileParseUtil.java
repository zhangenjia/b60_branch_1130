package com.adayo.app.base;


import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JsonFileParseUtil {
    private static final String TAG = "JsonFileParseUtil";
    public static JsonFileParseUtil mInstance = new JsonFileParseUtil();

    public String readStringFromAssets(Context context,String fileName) {
        //通过设备管理对象 获取Asset的资源路径
        AssetManager assetManager = context.getAssets();

        InputStream inputStream = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        StringBuffer sb = new StringBuffer();
        try {
            inputStream = assetManager.open(fileName);
            isr = new InputStreamReader(inputStream);
            br = new BufferedReader(isr);

            sb.append(br.readLine());
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append("\n" + line);
            }

            br.close();
            isr.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * brief: read json  file
     */
    public String ReadJsonFile(String filename) {
        if (filename == null) {
            return null;
        }
        File file = new File(filename);
        if (!file.exists()) { //文件不存在
            LogUtil.d(TAG, TAG + "ReadJsonFile() " + "Error :file is not exist! -- " + filename);
            return null;
        }

        int len = (int) file.length();
        byte readBuf[] = new byte[len];
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(readBuf);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String json = null;
        try {
            json = new String(readBuf, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LogUtil.d(TAG, TAG + "ReadJsonFile() " + "Error:  file's encode is not utf-8 --" + filename);
            e.printStackTrace();
        }

        return json;
    }

    public String getJsonByAPath(String path) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();

        File file = new File(path);
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            is = new FileInputStream(file);
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return stringBuilder.toString();
    }

}
