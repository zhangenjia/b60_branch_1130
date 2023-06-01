package com.adayo.app.camera.trackline.function;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.Log;

import com.adayo.crtrack.FloatPoint;


public class LineViewUtils {
    private static final String TAG = "LineViewUtils";

    public static void drawTyreLine(Canvas canvas, int color, FloatPoint[][] tyrePoints) {
        if (tyrePoints == null) {
            Log.e("AdayoCamera", TAG + " - drawTyreLine:tyrePoints is null");
            return;
        }

        for (int i = 0; i < 3; i = i + 2) {
            Paint paint1 = new Paint();
            paint1.setStrokeWidth(6);
            final int[] colors = new int[]{Color.argb(0, 0, 0, 0), color, Color.argb(0, 0, 0, 0)};
            Path fillPath1 = new Path();
            fillPath1.moveTo(tyrePoints[i][0].x, tyrePoints[i][0].y); //你的原点
            for (int j = 0; j < tyrePoints[i].length; j++) {
                fillPath1.lineTo(tyrePoints[i][j].x, tyrePoints[i][j].y);
            }
            fillPath1.lineTo(tyrePoints[i + 1][tyrePoints[1].length - 1].x, tyrePoints[i + 1][tyrePoints[1].length - 1].y);
            for (int j = tyrePoints[i + 1].length - 1; j > 0; j--) {
                fillPath1.lineTo(tyrePoints[i + 1][j].x, tyrePoints[i + 1][j].y);
            }
            fillPath1.lineTo(tyrePoints[i][0].x, tyrePoints[i][0].y); //相同的原点
            fillPath1.close();
            paint1.setShader(new LinearGradient(tyrePoints[i][0].x, tyrePoints[i][0].y, tyrePoints[i + 1][tyrePoints[i + 1].length - 1].x, tyrePoints[i + 1][tyrePoints[i + 1].length - 1].y, colors, null, Shader.TileMode.REPEAT));
//            paint1.setColor(Color.RED);
            canvas.drawPath(fillPath1, paint1);
        }
    }
}
