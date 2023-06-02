package com.adayo.app.setting.utils;

import com.adayo.app.base.LogUtil;

import java.util.Calendar;
import java.util.Map;

public class DateTimeUtils {

    public static void getDayOfMonthFormat(int year, int month, Map<Integer,String> days) {
        LogUtil.debugD("DateTimeUtils","year ="+year+"month ="+month);
        int daysOfMonth = getDaysOfMonth(year, month);
        for (int i = 1; i <= daysOfMonth; i++) {
            String Lunar =LunarCalendar.getLunarText(year, month, i);
days.put(i, Lunar);
        }
    }

        public static int getYear() {
            return Calendar.getInstance().get(Calendar.YEAR);
        }


        public static int getMonth() {
            return Calendar.getInstance().get(Calendar.MONTH) + 1;
        }


        public static int getCurrentDayOfMonth() {
            LogUtil.debugD("DateTimeUtils","getCurrentDayOfMonth ="+Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }

        public static int getHour(boolean is24HourClockMode) {
            if (is24HourClockMode == true) {
                return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);} else {
                return Calendar.getInstance().get(Calendar.HOUR);}
        }


        public static int getMinute() {
            return Calendar.getInstance().get(Calendar.MINUTE);
        }


        public static int getSecond() {
            return Calendar.getInstance().get(Calendar.SECOND);
        }


        public static int getApm() {
            return Calendar.getInstance().get(Calendar.AM_PM);
        }


        public static int getLastDaysOfMonth(int year, int month) {
            int lastDaysOfMonth = 0;
            if (month == 1) {
                lastDaysOfMonth = getDaysOfMonth(year - 1, 12);
            } else {
                lastDaysOfMonth = getDaysOfMonth(year, month - 1);
            }
            return lastDaysOfMonth;
        }

        public static boolean isLeap(int year) {
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                return true;
            }
            return false;
        }

        private static int getDaysOfMonth(int year, int month) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    return 31;
                case 2:
                    if (isLeap(year)) {
                        return 29;
                    } else {
                        return 28;
                    }
                case 4:
                case 6:
                case 9:
                case 11:
                    return 30;
                default:
                    break;
            }
            return -1;
        }
}
