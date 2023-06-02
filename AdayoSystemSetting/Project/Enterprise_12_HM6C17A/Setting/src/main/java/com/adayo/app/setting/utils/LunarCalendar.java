package com.adayo.app.setting.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.adayo.app.setting.R;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("all")
public final class LunarCalendar {

   private static final int lunarYear[] = {
            0x04974F, 0x064B44, 0x36A537, 0x0EA54A, 0x86B2BF, 0x05AC53, 0x0AB647, 0x5936BC, 0x092E50, 0x0C9645,
            0x4D4AB8, 0x0D4A4C, 0x0DA541, 0x25AAB6, 0x056A49, 0x7AADBD, 0x025D52, 0x092D47, 0x5C95BA, 0x0A954E,
            0x0B4A43, 0x4B5537, 0x0AD54A, 0x955ABF, 0x04BA53, 0x0A5B48, 0x652BBC, 0x052B50, 0x0A9345, 0x474AB9,
            0x06AA4C, 0x0AD541, 0x24DAB6, 0x04B64A, 0x69573D, 0x0A4E51, 0x0D2646, 0x5E933A, 0x0D534D, 0x05AA43,
            0x36B537, 0x096D4B, 0xB4AEBF, 0x04AD53, 0x0A4D48, 0x6D25BC, 0x0D254F, 0x0D5244, 0x5DAA38, 0x0B5A4C,
            0x056D41, 0x24ADB6, 0x049B4A, 0x7A4BBE, 0x0A4B51, 0x0AA546, 0x5B52BA, 0x06D24E, 0x0ADA42, 0x355B37,
            0x09374B, 0x8497C1, 0x049753, 0x064B48, 0x66A53C, 0x0EA54F, 0x06B244, 0x4AB638, 0x0AAE4C, 0x092E42,
            0x3C9735, 0x0C9649, 0x7D4ABD, 0x0D4A51, 0x0DA545, 0x55AABA, 0x056A4E, 0x0A6D43, 0x452EB7, 0x052D4B,
            0x8A95BF, 0x0A9553, 0x0B4A47, 0x6B553B, 0x0AD54F, 0x055A45, 0x4A5D38, 0x0A5B4C, 0x052B42, 0x3A93B6,
            0x069349, 0x7729BD, 0x06AA51, 0x0AD546, 0x54DABA, 0x04B64E, 0x0A5743, 0x452738, 0x0D264A, 0x8E933E,
            0x0D5252, 0x0DAA47, 0x66B53B, 0x056D4F, 0x04AE45, 0x4A4EB9, 0x0A4D4C, 0x0D1541, 0x2D92B5};

    static  int beginYear = 1991;private static String[] MONTH_STR = null;private static String[] TRADITION_FESTIVAL_STR = null;private static String[] DAY_STR = null;private static String[] SPECIAL_FESTIVAL_STR = null;private static final Map<Integer, String[]> SPECIAL_FESTIVAL = new HashMap<>();
    private static String[] SOLAR_CALENDAR = null;private static final Map<Integer, String[]> SOLAR_TERMS = new HashMap<>();

    static  int days[][] = {
            {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334},
            {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335}};

   public static void init(Context context) {
        if (MONTH_STR != null) {
            return;
        }
        SolarTermUtil.init(context);
        MONTH_STR = context.getResources().getStringArray(R.array.lunar_first_of_month);
        TRADITION_FESTIVAL_STR = context.getResources().getStringArray(R.array.tradition_festival);
        DAY_STR = context.getResources().getStringArray(R.array.lunar_str);
        SPECIAL_FESTIVAL_STR = context.getResources().getStringArray(R.array.special_festivals);
        SOLAR_CALENDAR = context.getResources().getStringArray(R.array.solar_festival);
    }

    public static String getLunarText(int year, int month, int day) {
       String termText = getSolarTerm(year, month, day);
       String solar = gregorianFestival(month, day);
       String special = getSpecialFestival(year, month, day);
        if (!TextUtils.isEmpty(solar))
            return solar;
        if (!TextUtils.isEmpty(special))
            return special;
        if (!TextUtils.isEmpty(termText))
            return termText;
       int[] lunar = solarToLunar(year, month, day);
       String festival = getTraditionFestival(lunar[0], lunar[1], lunar[2]);
        if (!TextUtils.isEmpty(festival))
            return festival;
        return numToChinese(lunar[1], lunar[2], lunar[3]);
    }



    private static String getTraditionFestival(int year, int month, int day) {
        if (month == 12) {
            int count = daysInLunarMonth(year, month);
            if (day == count) {
                return TRADITION_FESTIVAL_STR[0];}
        }
        String text = getString(month, day);
        String festivalStr = "";
        for (String festival : TRADITION_FESTIVAL_STR) {
            if (festival.contains(text)) {
                festivalStr = festival.replace(text, "");
                break;
            }
        }
        return festivalStr;
    }



    private static String numToChineseMonth(int month, int leap) {
        if (leap == 1) {
            return "闰" + MONTH_STR[month - 1];
        }
        return MONTH_STR[month - 1];
    }


    private static String numToChinese(int month, int day, int leap) {
        if (day == 1) {
            return numToChineseMonth(month, leap);
        }
        return DAY_STR[day - 1];
    }



    private static int daysInLunarMonth(int year, int month) {
       if (((lunarYear[year-beginYear] >> (19 - (month - 1))) & 0x1) == 1) {
            return 30;
        } else {
            return 29;
        }
    }


    private static String gregorianFestival(int month, int day) {
        String text = getString(month, day);
        String solar = "";
        for (String aMSolarCalendar : SOLAR_CALENDAR) {
            if (aMSolarCalendar.contains(text)) {
                solar = aMSolarCalendar.replace(text, "");
                break;
            }
        }
        return solar;
    }

    private static String getString(int month, int day) {
        return (month >= 10 ? String.valueOf(month) : "0" + month) + (day >= 10 ? day : "0" + day);
    }



    private static String getSolarTerm(int year, int month, int day) {
        if (!SOLAR_TERMS.containsKey(year)) {
            SOLAR_TERMS.put(year, SolarTermUtil.getSolarTerms(year));
        }
        String[] solarTerm = SOLAR_TERMS.get(year);
        String text = year + getString(month, day);
        String solar = "";
        assert solarTerm != null;
        for (String solarTermName : solarTerm) {
            if (solarTermName.contains(text)) {
                solar = solarTermName.replace(text, "");
                break;
            }
        }
        return solar;
    }


    private static String getSpecialFestival(int year, int month, int day) {
        if (!SPECIAL_FESTIVAL.containsKey(year)) {
            SPECIAL_FESTIVAL.put(year, getSpecialFestivals(year));
        }
        String[] specialFestivals = SPECIAL_FESTIVAL.get(year);
        String text = year + getString(month, day);
        String solar = "";
        assert specialFestivals != null;
        for (String special : specialFestivals) {
            if (special.contains(text)) {
                solar = special.replace(text, "");
                break;
            }
        }
        return solar;
    }



    private static String[] getSpecialFestivals(int year) {
        String[] festivals = new String[3];
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(year, 4, 1);
        int week = date.get(java.util.Calendar.DAY_OF_WEEK);
        int startDiff = 7 - week + 1;
        if (startDiff == 7) {
            festivals[0] = dateToString(year, 5, startDiff + 1) + SPECIAL_FESTIVAL_STR[0];
        } else {
            festivals[0] = dateToString(year, 5, startDiff + 7 + 1) + SPECIAL_FESTIVAL_STR[0];
        }
        date.set(year, 5, 1);
        week = date.get(java.util.Calendar.DAY_OF_WEEK);
        startDiff = 7 - week + 1;
        if (startDiff == 7) {
            festivals[1] = dateToString(year, 6, startDiff + 7 + 1) + SPECIAL_FESTIVAL_STR[1];
        } else {
            festivals[1] = dateToString(year, 6, startDiff + 7 + 7 + 1) + SPECIAL_FESTIVAL_STR[1];
        }

        date.set(year, 10, 1);
        week = date.get(java.util.Calendar.DAY_OF_WEEK);
        startDiff = 7 - week + 1;
        if (startDiff <= 2) {
            festivals[2] = dateToString(year, 11, startDiff + 21 + 5) + SPECIAL_FESTIVAL_STR[2];
        } else {
            festivals[2] = dateToString(year, 11, startDiff + 14 + 5) + SPECIAL_FESTIVAL_STR[2];
        }
        return festivals;
    }

    private static String dateToString(int year, int month, int day) {
        return year + getString(month, day);
    }

   private static int isleap(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
            return 1;
        } else {
            return 0;
        }
    }

    private static int[] solarToLunar(int y, int m, int d) {
        int[] lunarInfo = new int[4];
        int lunarMonth;
        int monthDay;
        int index = y - beginYear;                       int springMonth = (lunarYear[index] & 0x60) >> 5;int springDay = lunarYear[index] & 0x1f;         int todaySolar = days[isleap(y)][m - 1] + d; int springLunar = days[isleap(y)][springMonth - 1] + springDay;int gap = todaySolar - springLunar + 1;if (gap <= 0){
            index--;
            if (index < 0)
                return lunarInfo;                         springMonth = (lunarYear[index] & 0x60) >> 5;springDay = lunarYear[index] & 0x1f;         springLunar = days[isleap(y)][springMonth - 1] + springDay;
            int yearToday = days[isleap(y - 1)][11] + 31;gap = todaySolar + yearToday - springLunar + 1;
        }
        for (lunarMonth = 1; lunarMonth <= 13; lunarMonth++)
        {
           monthDay = daysInLunarMonth(index + beginYear, lunarMonth);
            if (gap <= monthDay)
                break;
            gap -= monthDay;
        }
        if (lunarMonth>13) {
            lunarMonth--;
        }
        int leapMonth = (lunarYear[index] >> 20) & 0xf;
       if (leapMonth > 0 && leapMonth <= lunarMonth) {
            if (lunarMonth <= leapMonth) {
               lunarInfo[1] = lunarMonth;
                lunarInfo[3] = 0;
            } else if (lunarMonth == (leapMonth + 1)) {
               lunarInfo[1] = lunarMonth - 1;
                lunarInfo[3] = 1;
            } else {
               lunarInfo[1] = lunarMonth - 1;
                lunarInfo[3] = 0;
            }

        } else {
           lunarInfo[1] = lunarMonth;
            lunarInfo[3] = 0;
        }

        lunarInfo[0] = index + beginYear;
        lunarInfo[2] = gap;
        return lunarInfo;
    }

}
