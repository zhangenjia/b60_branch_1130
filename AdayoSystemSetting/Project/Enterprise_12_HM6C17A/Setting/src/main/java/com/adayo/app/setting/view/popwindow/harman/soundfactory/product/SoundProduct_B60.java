package com.adayo.app.setting.view.popwindow.harman.soundfactory.product;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


import com.adayo.app.setting.view.popwindow.harman.soundfactory.bean.SoundBean;
import com.adayo.app.base.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundProduct_B60 extends ASoundProduct {
    private static Map<String, List<SoundBean>> mMap = new HashMap<>();
    private static final int INTERVAL = 11;

    public SoundProduct_B60()
    {
        List<SoundBean> listA = new ArrayList<>();
        mMap.put("A", listA);

        List<SoundBean> listB = new ArrayList<>();
        mMap.put("B", listB);

        List<SoundBean> listC = new ArrayList<>();
        mMap.put("C", listC);

        List<SoundBean> listD = new ArrayList<>();
        mMap.put("D", listD);
    }
    @Override
    public void calculateAllSoundLocation() {
       LogUtil.debugD("harmon","");
        calculateARegion();
        calculateBRegion();
        calculateCRegin();
        calculateDRegion();
    }


    private void calculateARegion()
    {
        double step = (mSoundBean0.getmSreen_Y() - mSoundBean1.getmSreen_Y()) / (INTERVAL - 1);
        double [] yArray = new double[INTERVAL];
        double [] xArray = new double[INTERVAL];
        final int A_MIN = 10;
        List<SoundBean> aList = new ArrayList<>();
        mMap.put("A", aList);

       for (int i = 0; i < INTERVAL; i++)
        {
            yArray[i] = mSoundBean0.getmSreen_Y() - step * i;
            if (yArray[i] <= mSoundBean1.getmSreen_Y())
            {
                yArray[i] = mSoundBean1.getmSreen_Y();
                break;
            }
        }

       for (int i = 0; i < INTERVAL; i++)
        {
            xArray[i] = (yArray[i] - mSoundBean1.getmSreen_Y() + (mSoundBean1.getmSreen_X() * (mSoundBean3.getmSreen_Y() - mSoundBean1.getmSreen_Y())) / (mSoundBean3.getmSreen_X() - mSoundBean1.getmSreen_X())) * (mSoundBean3.getmSreen_X() - mSoundBean1.getmSreen_X()) / (mSoundBean3.getmSreen_Y() - mSoundBean1.getmSreen_Y());
            if (xArray[i] >= mSoundBean1.getmSreen_X())
            {
                xArray[i] = mSoundBean1.getmSreen_X();
            }

            double [] spaceXArray = new double[INTERVAL];
            double space = (mSoundBean0.getmSreen_X() - xArray[i]) / (INTERVAL - 1);
            for (int j = 0; j < INTERVAL; j++)
            {
                if (j == 0)
                {
                    spaceXArray[j] = xArray[i];
                }
                else if (j == INTERVAL - 1)
                {
                    spaceXArray[j] = mSoundBean0.getmSreen_X();
                }
                else
                {
                    spaceXArray[j] = xArray[i] + j * space;
                }
                SoundBean bean = new SoundBean(spaceXArray[j], yArray[i], A_MIN * (-1) + j, i);
                if (aList == null)
                {
                    aList = new ArrayList<>();
                }
                aList.add(bean);
            }
        }
    }


    private void calculateBRegion()
    {
        double step = (mSoundBean0.getmSreen_Y() - mSoundBean2.getmSreen_Y()) / (INTERVAL - 1);
        double [] yArray = new double[INTERVAL];
        double [] xArray = new double[INTERVAL];
        final int B_MIN = 10;
        List<SoundBean> bList = new ArrayList<>();
        mMap.put("B", bList);

       for (int i = 0; i < INTERVAL; i++)
        {
            yArray[i] = mSoundBean0.getmSreen_Y() - step * i;
            if (yArray[i] <= mSoundBean2.getmSreen_Y())
            {
                yArray[i] = mSoundBean2.getmSreen_Y();
                break;
            }
        }

       for (int i = 0; i < INTERVAL; i++)
        {
            xArray[i] = (yArray[i] - mSoundBean1.getmSreen_Y() + (mSoundBean2.getmSreen_X() * (mSoundBean3.getmSreen_Y() - mSoundBean1.getmSreen_Y())) / (mSoundBean4.getmSreen_X() - mSoundBean2.getmSreen_X())) * (mSoundBean4.getmSreen_X() - mSoundBean2.getmSreen_X()) / (mSoundBean3.getmSreen_Y() - mSoundBean1.getmSreen_Y());
            if (xArray[i] <= mSoundBean2.getmSreen_X())
            {
                xArray[i] = mSoundBean2.getmSreen_X();
            }

            double [] spaceXArray = new double[INTERVAL];
            double space = (xArray[i] - mSoundBean0.getmSreen_X()) / (INTERVAL - 1);
            for (int j = 0; j < INTERVAL; j++)
            {
                if (j == 0)
                {
                    spaceXArray[j] = xArray[i];
                }
                else if (j == INTERVAL - 1)
                {
                    spaceXArray[j] = mSoundBean0.getmSreen_X();
                }
                else
                {
                    spaceXArray[j] = xArray[i] + j * space * (-1);
                }
                SoundBean bean = new SoundBean(spaceXArray[j], yArray[i], B_MIN + j * (-1), i);
                if (bList == null)
                {
                    bList = new ArrayList<>();
                }
                bList.add(bean);
            }
        }
    }


    private void calculateCRegin()
    {
        double step = (mSoundBean3.getmSreen_Y() - mSoundBean0.getmSreen_Y()) / (INTERVAL - 1);
        double [] yArray = new double[INTERVAL];
        double [] xArray = new double[INTERVAL];
        final int C_MIN = 10;
        List<SoundBean> cList = new ArrayList<>();
        mMap.put("C", cList);

       for (int i = 0; i < INTERVAL; i++)
        {
            yArray[i] = mSoundBean3.getmSreen_Y() - step * i;
            if (yArray[i] <= mSoundBean0.getmSreen_Y())
            {
                yArray[i] = mSoundBean0.getmSreen_Y();
                break;
            }
        }

       for (int i = 0; i < INTERVAL; i++)
        {
            xArray[i] = (yArray[i] - mSoundBean1.getmSreen_Y() + (mSoundBean1.getmSreen_X() * (mSoundBean3.getmSreen_Y() - mSoundBean1.getmSreen_Y())) / (mSoundBean3.getmSreen_X() - mSoundBean1.getmSreen_X())) * (mSoundBean3.getmSreen_X() - mSoundBean1.getmSreen_X()) / (mSoundBean3.getmSreen_Y() - mSoundBean1.getmSreen_Y());
            double [] spaceXArray = new double[INTERVAL];
            double space = (mSoundBean0.getmSreen_X() - xArray[i]) / (INTERVAL - 1);
            for (int j = 0; j < INTERVAL; j++)
            {
                if (j == 0)
                {
                    spaceXArray[j] = xArray[i];
                }
                else if (j == INTERVAL - 1)
                {
                    spaceXArray[j] = mSoundBean0.getmSreen_X();
                }
                else
                {
                    spaceXArray[j] = xArray[i] + j * space;
                }
                SoundBean bean = new SoundBean(spaceXArray[j], yArray[i], C_MIN * (-1) + j, C_MIN * (-1) + i);
                if (cList == null)
                {
                    cList = new ArrayList<>();
                }
                cList.add(bean);
            }
        }
    }


    private void calculateDRegion()
    {
        double step = (mSoundBean4.getmSreen_Y() - mSoundBean0.getmSreen_Y()) / (INTERVAL - 1);
        double [] yArray = new double[INTERVAL];
        double [] xArray = new double[INTERVAL];
        final int D_MIN = 10;
        List<SoundBean> dList = new ArrayList<>();
        mMap.put("D", dList);

       for (int i = 0; i < INTERVAL; i++)
        {
            yArray[i] = mSoundBean4.getmSreen_Y() - step * i;
            if (yArray[i] <= mSoundBean0.getmSreen_Y())
            {
                yArray[i] = mSoundBean0.getmSreen_Y();
                break;
            }
        }

       for (int i = 0; i < INTERVAL; i++)
        {
            xArray[i] = (yArray[i] - mSoundBean1.getmSreen_Y() + (mSoundBean2.getmSreen_X() * (mSoundBean3.getmSreen_Y() - mSoundBean1.getmSreen_Y())) / (mSoundBean4.getmSreen_X() - mSoundBean2.getmSreen_X())) * (mSoundBean4.getmSreen_X() - mSoundBean2.getmSreen_X()) / (mSoundBean3.getmSreen_Y() - mSoundBean1.getmSreen_Y());
            double [] spaceXArray = new double[INTERVAL];
            double space = (xArray[i] - mSoundBean0.getmSreen_X()) / (INTERVAL - 1);
            for (int j = 0; j < INTERVAL; j++)
            {
                if (j == 0)
                {
                    spaceXArray[j] = xArray[i];
                }
                else if (j == INTERVAL - 1)
                {
                    spaceXArray[j] = mSoundBean0.getmSreen_X();
                }
                else
                {
                    spaceXArray[j] = xArray[i] + j * space * (-1);
                }
                SoundBean bean = new SoundBean(spaceXArray[j], yArray[i], D_MIN + j * (-1) , D_MIN * (-1) + i);
                if (dList == null)
                {
                    dList = new ArrayList<>();
                }
                dList.add(bean);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Log.i("harmon", "draw: ");
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        int circleRadius      = 5;
        paint.setAntiAlias(true);List<SoundBean> alist = mMap.get("A");
        for (int i = 0; i < alist.size(); i++)
        {
            SoundBean bean = alist.get(i);
            Log.i("harmon", "draw: x = " + bean.getmSreen_X() + " y = " + (float)bean.getmSreen_Y());
            canvas.drawCircle((float)bean.getmSreen_X(), (float)bean.getmSreen_Y(),circleRadius, paint);}

       Paint paintB = new Paint();
        paintB.setColor(Color.GREEN);
        paintB.setStyle(Paint.Style.FILL);
        paintB.setAntiAlias(true);List<SoundBean> blist = mMap.get("B");
        for (int i = 0; i < blist.size(); i++)
        {
            SoundBean bean = blist.get(i);
            Log.i("harmon", "draw: x = " + bean.getmSreen_X() + " y = " + (float)bean.getmSreen_Y());
            canvas.drawCircle((float)bean.getmSreen_X(), (float)bean.getmSreen_Y(),circleRadius, paintB);}

       Paint paintC = new Paint();
        paintC.setColor(Color.BLACK);
        paintC.setStyle(Paint.Style.FILL);
        paintC.setAntiAlias(true);List<SoundBean> clist = mMap.get("C");
        for (int i = 0; i < clist.size(); i++)
        {
            SoundBean bean = clist.get(i);
            Log.i("harmon", "draw: x = " + bean.getmSreen_X() + " y = " + (float)bean.getmSreen_Y());
            canvas.drawCircle((float)bean.getmSreen_X(), (float)bean.getmSreen_Y(),circleRadius, paintC);}

       Paint paintD = new Paint();
        paintD.setColor(Color.RED);
        paintD.setStyle(Paint.Style.FILL);
        paintD.setAntiAlias(true);List<SoundBean> dlist = mMap.get("D");
        for (int i = 0; i < dlist.size(); i++)
        {
            SoundBean bean = dlist.get(i);
            Log.i("harmon", "draw: x = " + bean.getmSreen_X() + " y = " + (float)bean.getmSreen_Y());
            canvas.drawCircle((float)bean.getmSreen_X(), (float)bean.getmSreen_Y(),circleRadius, paintD);}
    }

    @Override
    public boolean isValidLocation(double screenX, double screenY) {
        Log.i("harmon", "isValidLocation: screenX = " + screenX + " screenY = " + screenY);


        if (screenY < mSoundBean1.getmSreen_Y() || screenY > mSoundBean3.getmSreen_Y())
        {
            return false;
        }

        double leftX = (screenY - mSoundBean1.getmSreen_Y() + mSoundBean1.getmSreen_X() * (mSoundBean3.getmSreen_Y() - mSoundBean1.getmSreen_Y()) / (mSoundBean3.getmSreen_X() - mSoundBean1.getmSreen_X())) * (mSoundBean3.getmSreen_X() - mSoundBean1.getmSreen_X()) / (mSoundBean3.getmSreen_Y() - mSoundBean1.getmSreen_Y());
        double rightX = (screenY - mSoundBean1.getmSreen_Y() + mSoundBean2.getmSreen_X() * (mSoundBean3.getmSreen_Y() - mSoundBean1.getmSreen_Y()) / (mSoundBean4.getmSreen_X() - mSoundBean2.getmSreen_X())) * (mSoundBean4.getmSreen_X() - mSoundBean2.getmSreen_X()) / (mSoundBean3.getmSreen_Y() - mSoundBean1.getmSreen_Y());

        Log.i("harmon", "isValidLocation: leftX = " + leftX + " rightX = " + rightX);
        if (screenX >= leftX && screenX <= rightX)
        {
            return true;
        }

        return false;
    }

    @Override
    public SoundBean selectLocation(double screenX, double screenY) {
        Log.i("harmon", "selectLocation: screenX = " + screenX + " screenY = " + screenY);

       String region = null;
        if (screenX <= mSoundBean0.getmSreen_X() && screenY <= mSoundBean0.getmSreen_Y())
        {
            region = "A";
        }
        else if (screenX > mSoundBean0.getmSreen_X() && screenY <= mSoundBean0.getmSreen_Y())
        {
            region = "B";
        }
        else if (screenX <= mSoundBean0.getmSreen_X() && screenY <= mSoundBean3.getmSreen_Y())
        {
            region = "C";
        }
        else if (screenX > mSoundBean0.getmSreen_X() && screenY <= mSoundBean3.getmSreen_Y())
        {
            region = "D";
        }
        else
        {

        }

        Log.i("harmon", "selectLocation: region = " + region);
        if (region != null)
        {
            SoundBean bean = calcRegion(region, screenX, screenY);
            return bean;
        }

        return null;
    }

    private SoundBean calcRegion(final String regionId, double screenX, double screenY)
    {
        Log.i("harmon", "calcRegion: regionId = " + regionId + " screenX = " + screenX + " screenY = " + screenY);
        if (regionId == null)
        {
            return null;
        }

        SoundBean[][] soundBeanArray = new SoundBean[INTERVAL][INTERVAL];
        final List<SoundBean> list = mMap.get(regionId);
        for (int i = 0; i < list.size(); i++)
        {
            soundBeanArray[i / INTERVAL][i % INTERVAL] = list.get(i);
        }

        Log.i("harmon", "calcRegion: change soundBeanArray");
        double [] yArray = new double[INTERVAL];
        for (int index = 0; index < INTERVAL; index++)
        {
            yArray[index] = soundBeanArray[index][0].getmSreen_Y();
            Log.i("harmon", "calcRegion: yArray " + index + ":" + yArray[index]);
        }

       int lowYIndex = 0, highYIndex = 0;
        for (int index = yArray.length - 1; index >= 0; index--)
        {
            if (screenY >= yArray[index])
            {
                lowYIndex = index;
            }else if (screenY <= yArray[index])
            {
                highYIndex = index;
                break;
            }
            else
            {

            }
        }
        Log.i("harmon", "calcRegion: lowYIndex = " + lowYIndex + " highYIndex = " + highYIndex);

        SoundBean [] soundBeans = new SoundBean[4];
        if ("A".equals(regionId) || "C".equals(regionId)) {
           soundBeans[0] = soundBeans[1] = soundBeanArray[lowYIndex][0];
            for (int i = 0; i < INTERVAL - 1; i++) {
                Log.i("harmon", "calcRegion: AC_lowi = " + i);
                if (screenX >= soundBeanArray[lowYIndex][i].getmSreen_X() &&
                        screenX <= soundBeanArray[lowYIndex][i + 1].getmSreen_X()) {
                    soundBeans[0] = soundBeanArray[lowYIndex][i];
                    soundBeans[1] = soundBeanArray[lowYIndex][i + 1];
                    break;
                }
            }

            Log.i("harmon", "calcRegion: soundBeans[0] = " + soundBeans[0] + " soundBeans[1] = " + soundBeans[1]);

           soundBeans[2] = soundBeans[3] = soundBeanArray[highYIndex][0];
            for (int i = 0; i < INTERVAL - 1; i++) {
                Log.i("harmon", "calcRegion: AC_highi = " + i);
                if (screenX >= soundBeanArray[highYIndex][i].getmSreen_X() &&
                        screenX <= soundBeanArray[highYIndex][i + 1].getmSreen_X()) {
                    soundBeans[2] = soundBeanArray[highYIndex][i];
                    soundBeans[3] = soundBeanArray[highYIndex][i + 1];
                    break;
                }
            }

            Log.i("harmon", "calcRegion: soundBeans[2] = " + soundBeans[2] + " soundBeans[3] = " + soundBeans[3]);
        }
        else
        {
           soundBeans[0] = soundBeans[1] = soundBeanArray[lowYIndex][0];
            for (int i = 0; i < INTERVAL - 1; i++) {
                Log.i("harmon", "calcRegion: BD_lowi = " + i);
                if (screenX <= soundBeanArray[lowYIndex][i].getmSreen_X() &&
                        screenX >= soundBeanArray[lowYIndex][i + 1].getmSreen_X()) {
                    soundBeans[0] = soundBeanArray[lowYIndex][i];
                    soundBeans[1] = soundBeanArray[lowYIndex][i + 1];
                    break;
                }
            }
            Log.i("harmon", "calcRegion: soundBeans[0] = " + soundBeans[0] + " soundBeans[1] = " + soundBeans[1]);

           soundBeans[2] = soundBeans[3] = soundBeanArray[highYIndex][0];
            for (int i = 0; i < INTERVAL - 1; i++) {
                Log.i("harmon", "calcRegion: BD_highi = " + i);
                if (screenX <= soundBeanArray[highYIndex][i].getmSreen_X() &&
                        screenX >= soundBeanArray[highYIndex][i + 1].getmSreen_X()) {
                    soundBeans[2] = soundBeanArray[highYIndex][i];
                    soundBeans[3] = soundBeanArray[highYIndex][i + 1];
                    break;
                }
            }
            Log.i("harmon", "calcRegion: soundBeans[2] = " + soundBeans[2] + " soundBeans[3] = " + soundBeans[3]);
        }

       for (int i = 0; i < 4; i++)
        {
            if (soundBeans[i] == null)
            {
                Log.i("harmon", "calcRegion: soundBeans[" + i + "] is null :" + i);
            }
            else {
                Log.i("harmon", "calcRegion: soundBeans[" + i + "].x :" + soundBeans[i].getmSreen_X() + " .y = " + soundBeans[i].getmSreen_Y());
            }
        }

       double [] instance = new double[4];
        for (int i = 0; i < 4; i++)
        {
            instance[i] = Math.pow(Math.abs(screenY - soundBeans[i].getmSreen_Y()), 2) +
                    Math.pow(Math.abs(screenX - soundBeans[i].getmSreen_X()), 2);
            Log.i("harmon", "calcRegion: instance[" + i + " ] = " + instance[i]);
        }

        double s = instance[0];
        int k = 0;
        for (int i = 0; i < instance.length; i++)
        {
            if (instance[i] <= s)
            {
                s = instance[i];
                k = i;
            }
        }

        Log.i("harmon", "calcRegion: k = " + k);
        return soundBeans[k];
    }

    @Override
    public SoundBean getScreenLocationBySound(int x, int y) {
        String region = null;
        if (x <= 0 && y >= 0)
        {
            region = "A";
        }
        else if (x > 0 && y > 0)
        {
            region = "B";
        }
        else if (x <= 0 && y <= 0)
        {
            region = "C";
        }
        else if (x > 0 && y <= 0)
        {
            region = "D";
        }
        else
        {

        }

        SoundBean retBean = null;
        if (region != null)
        {
            SoundBean[][] soundBeanArray = new SoundBean[INTERVAL][INTERVAL];
            final List<SoundBean> list = mMap.get(region);
            for (int i = 0; i < list.size(); i++)
            {
                soundBeanArray[i / INTERVAL][i % INTERVAL] = list.get(i);
            }

            Log.i("harmon", "calcRegion: change soundBeanArray");
            int [] yArray = new int[INTERVAL];
            for (int index = 0; index < INTERVAL; index++)
            {
                yArray[index] = soundBeanArray[index][0].getmSound_Y();
                if (yArray[index] == y)
                {
                    for (int j = 0; j < INTERVAL; j++)
                    {
                        if (soundBeanArray[index][j].getmSound_X() == x)
                        {
                            retBean = soundBeanArray[index][j];
                            break;
                        }
                    }
                    break;
                }
                Log.i("harmon", "calcRegion: yArray " + index + ":" + yArray[index]);
            }
        }

        return retBean;
    }
}
