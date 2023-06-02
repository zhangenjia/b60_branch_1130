package com.adayo.app.picture.ui.base;


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
