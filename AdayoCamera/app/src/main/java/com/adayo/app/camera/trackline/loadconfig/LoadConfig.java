package com.adayo.app.camera.trackline.loadconfig;

import android.util.Log;

import com.adayo.app.camera.trackline.function.FileUtil;
import com.adayo.app.camera.trackline.loadconfig.bean.CalibrateTrack;
import com.adayo.crtrack.IntergePoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class LoadConfig {
    private static final String TAG = "LoadConfig";
    private String mPath;
    private static LoadConfig instance;
    private CalibrateTrack calibrationTackConfig = null;
    public static final String X = "x";
    public static final String Y = "y";
    private IntergePoint[] mWorldPoints;


    public enum Point {
        A(0), B(1), C(2), D(3);

        private final int mPointCode;

        Point(int point) {
            mPointCode = point;
        }

        public int code() {
            return mPointCode;
        }

        static Point[] POINTS = new Point[]{A, B, C, D};

        public static Point intToPoint(int pointCode) {
            return POINTS[pointCode];
        }
    }

    public static LoadConfig getInstance() {
        if (instance == null) {
            synchronized (LoadConfig.class) {
                if (instance == null) {
                    instance = new LoadConfig();
                }
            }
        }
        return instance;
    }


    public CalibrateTrack getCRTrack(String crtackBeanPath) {
        if (FileUtil.isFileExist(crtackBeanPath)) {
            //读取json内容，并过滤掉//注释
            String json = FileUtil.readFile(new File(crtackBeanPath)).replaceAll("///*.+", "");
            try {
                Gson gson = new GsonBuilder().create();
                Log.d("AdayoCamera", TAG + " - config parse json," + json.toString());


                Log.d("AdayoCamera", TAG + " - config parse toJson," + gson.toJson(new CalibrateTrack()));
                calibrationTackConfig = gson.fromJson(json, CalibrateTrack.class);
                Log.d("AdayoCamera", TAG + " - config parse end," + calibrationTackConfig.toString());
            } catch (Exception e) {
                Log.e("AdayoCamera", TAG + " - parse json error:" + e.getMessage());
                e.printStackTrace();
            }
        }
        return calibrationTackConfig;
    }

    public IntergePoint[] getWorldPoints(String path) {
        Log.d("AdayoCamera", TAG + " - getWorldPoints() called with: path = [" + path + "]");
        JsonParser parser = new JsonParser();
        try {
            JsonObject object = (JsonObject) parser.parse(new FileReader(path));
            JsonObject calibratePoint = object.get("calibrate_point").getAsJsonObject();
            JsonArray worldPoints = calibratePoint.get("world_coordinate").getAsJsonArray();
            Log.d("AdayoCamera", TAG + " - getWorldPoints: worldPoints = " + worldPoints.toString());
            if (mWorldPoints == null) {
                mWorldPoints = new IntergePoint[worldPoints.size()];
            }
            for (int i = 0; i < worldPoints.size(); i++) {
                JsonObject worldPoint = worldPoints.get(i).getAsJsonObject();
                if (mWorldPoints[i] == null) {
                    mWorldPoints[i] = new IntergePoint();
                }
                mWorldPoints[i].x = worldPoint.get("x").getAsInt();
                mWorldPoints[i].y = worldPoint.get("y").getAsInt();
                Log.d("AdayoCamera", TAG + " - getWorldPoints: mWorldPoints i = " + i + " | x = " + mWorldPoints[i].x);
                Log.d("AdayoCamera", TAG + " - getWorldPoints: mWorldPoints i = " + i + " | y = " + mWorldPoints[i].y);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return mWorldPoints;
    }

    public IntergePoint[] getScreenPoints(String path) {
        Log.d("AdayoCamera", TAG + " - getScreenPoints() called with: path = [" + path + "]");
        IntergePoint[] screenCoordinatePoints = null;
        JsonParser parser = new JsonParser();
        try {
            JsonObject object = (JsonObject) parser.parse(new FileReader(path));
            JsonObject calibratePoint = object.get("calibrate_point").getAsJsonObject();
            JsonArray screenPoints = calibratePoint.get("screen_coordinate").getAsJsonArray();
            Log.d("AdayoCamera", TAG + " - getScreenPoints: screenPoints = " + screenPoints.toString());
            screenCoordinatePoints = new IntergePoint[screenPoints.size()];
            for (int i = 0; i < screenPoints.size(); i++) {
                JsonObject worldPoint = screenPoints.get(i).getAsJsonObject();
                if (screenCoordinatePoints[i] == null) {
                    screenCoordinatePoints[i] = new IntergePoint();
                }
                screenCoordinatePoints[i].x = worldPoint.get("x").getAsInt();
                screenCoordinatePoints[i].y = worldPoint.get("y").getAsInt();
                Log.d("AdayoCamera", TAG + " - getScreenPoints: screenPoints i" + i + "x = " + screenCoordinatePoints[i].x);
                Log.d("AdayoCamera", TAG + " - getScreenPoints: screenPoints i" + i + "y = " + screenCoordinatePoints[i].y);
            }
        } catch (FileNotFoundException e) {
            Log.e("AdayoCamera", TAG + " - getScreenPoints: error", e);
        }

        return screenCoordinatePoints;
    }
}
