package com.adayo.app.systemui.configs;

import android.car.VehicleAreaSeat;
import android.car.VehiclePropertyIds;
import android.car.hardware.hvac.AAOP_HvacConstant;

import com.adayo.app.systemui.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/9 14:04
 */
public class HvacContent {
    /**
     * Send and receive hvac can data vehicle ID
     */
    public static final int HVAC_POWER_ON = VehiclePropertyIds.HVAC_POWER_ON;//空调开关
    public static final int HVAC_AC_ON = VehiclePropertyIds.HVAC_AC_ON;//压缩机开关
    public static final int HVAC_AUTO_ON = VehiclePropertyIds.HVAC_AUTO_ON;//自动空调开关
    public static final int HVAC_RECIRC_ON = VehiclePropertyIds.HVAC_RECIRC_ON;//内循环开关
    public static final int HVAC_DEFROSTER = VehiclePropertyIds.HVAC_DEFROSTER;//除霜开关
    public static final int HVAC_TEMPERATURE_SET = VehiclePropertyIds.HVAC_TEMPERATURE_SET;//温度
    public static final int HVAC_FAN_SPEED = VehiclePropertyIds.HVAC_FAN_SPEED;//风量
    public static final int HVAC_FAN_DIRECTION = VehiclePropertyIds.HVAC_FAN_DIRECTION;//吹风模式
    public static final int HVAC_UNLOCK_FRESH_AIR = VehiclePropertyIds.HVAC_UNLOCK_FRESH_AIR;//解锁新风开关
    public static final int HVAC_ANION_SWITCH = VehiclePropertyIds.HVAC_ANION_SWITCH;//负离子开关
    public static final int HVAC_AIR_CLEAN_CONTROL = VehiclePropertyIds.HVAC_AIR_CLEAN_CONTROL;//AQS开关
    public static final int HVAC_AIRPURI_AND_AUTO_LINK = VehiclePropertyIds.HVAC_AIRPURI_AND_AUTO_LINK;//AQS与AUTO联动
    public static final int HVAC_PM25_DISPLAY = VehiclePropertyIds.HVAC_PM25_DISPLAY;//PM2.5开关
    public static final int HVAC_SEAT_TEMPERATURE = VehiclePropertyIds.HVAC_SEAT_TEMPERATURE;//座椅加热
    public static final int HVAC_SEAT_VENTILATION = VehiclePropertyIds.HVAC_SEAT_VENTILATION;//座椅通风
    public static final int HVAC_STEERING_WHEEL_HEAT = VehiclePropertyIds.STEERING_WHEEL_HEAT_HVAC;//方向盘加热
    public static final int BCM_SEAT_MASSAGE_MODE_CTRL = VehiclePropertyIds.BCM_SEAT_MASSAGE_MODE;//座椅按摩模式
    public static final int BCM_SEAT_MASSAGE_INTENSITY_CTRL = VehiclePropertyIds.BCM_SEAT_MASSAGE_INTENSITY;//座椅按摩强度

    /**
     * Send or receive hvac can data vehicle ID
     */
    public static final int HVAC_FRONT_STATUS = VehiclePropertyIds.HVAC_FRONT_STATUS;//前除霜开关
    public static final int HVAC_DUAL_ZONE_SET = VehiclePropertyIds.HVAC_DUAL_ZONE_SET;//分区控制设定
    public static final int HVAC_DUAL_MODE_STATUS = VehiclePropertyIds.HVAC_DUAL_MODE_STATUS;//分区控制接收
    public static final int HVAC_INSIDE_PM25_VALUE = VehiclePropertyIds.HVAC_INSIDE_PM25_VALUE;//PM2.5接收
    public static final int AC2_ST_DISP_REQUEST = VehiclePropertyIds.AC2_ST_DISP_REQUEST;//空调显示请求设置接收
    public static final int HVAC_DISPLAY_CONTROL_REQ = VehiclePropertyIds.HVAC_DISPLAY_CONTROL_REQ;//空调显示请求设置
    public static final int VEHICLE_CAN_TO_IVI_BAT_VOLTAGE = VehiclePropertyIds.VEHICLE_CAN_TO_IVI_BAT_VOLTAGE;//电压

    /**
     * Send or receive hvac can data air ID
     */
    public static final int AREA_GLOBAL = 0;//无
    public static final int AREA_SEAT_DRIVER = AAOP_HvacConstant.AREA_SEAT_DRIVER;//主驾
    public static final int AREA_SEAT_PASSENGER = AAOP_HvacConstant.AREA_SEAT_PASSENGER;//副驾
    public static final int AREA_SEAT_HVAC_FRONT = AAOP_HvacConstant.AREA_SEAT_HVAC_FRONT;//前排
    public static final int AREA_SEAT_HVAC_REAR = AAOP_HvacConstant.AREA_SEAT_HVAC_REAR;//后排
    public static final int AREA_SEAT_REAR_LEFT = AAOP_HvacConstant.AREA_SEAT_REAR_LEFT;//后左
    public static final int AREA_SEAT_REAR_RIGHT = AAOP_HvacConstant.AREA_SEAT_REAR_RIGHT;//后右
    public static final int AREA_SEAT_HVAC_LEFT = AAOP_HvacConstant.AREA_SEAT_HVAC_LEFT;//左侧
    public static final int AREA_SEAT_HVAC_RIGHT = AAOP_HvacConstant.AREA_SEAT_HVAC_RIGHT;//右侧
    public static final int AREA_SEAT_HVAC_ALL = AAOP_HvacConstant.AREA_SEAT_HVAC_ALL;//所有位置

    public static final int MASSAGE_INTENSITY_CLOSE = 1;//关闭
    public static final int MASSAGE_INTENSITY_LOW = 2;//低
    public static final int MASSAGE_INTENSITY_MIDDLE = 3;//中
    public static final int MASSAGE_INTENSITY_HIGH = 4;//高

    public static final int MASSAGE_MODE_PULSE = 1;//脉冲
    public static final int MASSAGE_MODE_WAVE = 2;//波浪
    public static final int MASSAGE_MODE_CATWALK = 3;//猫步
    public static final int MASSAGE_MODE_SINGLE_ROW = 4;//单排
    public static final int MASSAGE_MODE_CROSS = 5;//交叉
    public static final int MASSAGE_MODE_BUTTERFLY = 6;//蝶形
    public static final int MASSAGE_MODE_LOIN = 7;//腰部
    public static final int MASSAGE_MODE_SERPENTINE = 8;//蛇形

    public static final int[] HVAC_IDS = {
            HVAC_POWER_ON,
            HVAC_AC_ON,
            HVAC_AUTO_ON,
            HVAC_RECIRC_ON,
            HVAC_DEFROSTER,
            HVAC_TEMPERATURE_SET,
            HVAC_FAN_SPEED,
            HVAC_FAN_DIRECTION,
            HVAC_UNLOCK_FRESH_AIR,
            HVAC_ANION_SWITCH,
            HVAC_AIR_CLEAN_CONTROL,
            HVAC_AIRPURI_AND_AUTO_LINK,
            HVAC_PM25_DISPLAY,
            HVAC_SEAT_TEMPERATURE,
            HVAC_SEAT_VENTILATION,
            HVAC_FRONT_STATUS,
            HVAC_DUAL_ZONE_SET,
            HVAC_DUAL_MODE_STATUS,
            HVAC_INSIDE_PM25_VALUE,
            HVAC_STEERING_WHEEL_HEAT,
            BCM_SEAT_MASSAGE_MODE_CTRL,
            BCM_SEAT_MASSAGE_INTENSITY_CTRL
    };

    public static final int[] HVAC_AIR_IDS = {
            AREA_SEAT_DRIVER,
            AREA_SEAT_PASSENGER,
            AREA_SEAT_HVAC_FRONT,
            AREA_SEAT_HVAC_REAR,
            AREA_SEAT_HVAC_LEFT,
            AREA_SEAT_HVAC_RIGHT,
            AREA_SEAT_REAR_LEFT,
            AREA_SEAT_REAR_RIGHT,
            AREA_SEAT_HVAC_ALL,
            AREA_GLOBAL
    };

    public static final int SEAT_ROW_1_LEFT = VehicleAreaSeat.SEAT_ROW_1_LEFT;
    public static final int SEAT_ROW_1_RIGHT = VehicleAreaSeat.SEAT_ROW_1_RIGHT;
    public static final int SEAT_ROW_2_LEFT = VehicleAreaSeat.SEAT_ROW_2_LEFT;
    public static final int SEAT_ROW_2_RIGHT = VehicleAreaSeat.SEAT_ROW_2_RIGHT;

    public static final int HVAC_FRONT = 0;
    public static final int HVAC_REAR = 1;
    public static final int HVAC_THREE = 2;
    public static final int HVAC_FOUR = 3;

    public static final int HVAC_DEFAULT_VALUE = 255;
    public static final int SWITCH_ON_REFERENCE_VALUE = 1;
    public static final int SWITCH_OFF_REFERENCE_VALUE = 0;
    public static final int HVAC_FAN_DIRECTION_REAR_FACE = 1;
    public static final int HVAC_FAN_DIRECTION_REAR_FOOT = 3;
    public static final int HVAC_FAN_DIRECTION_REAR_FACE_FOOT = 2;
    public static final int HVAC_FAN_DIRECTION_FACE = 1;
    public static final int HVAC_FAN_DIRECTION_FOOT = 2;
    public static final int HVAC_FAN_DIRECTION_FACE_FOOT = 3;
    public static final int HVAC_FAN_DIRECTION_FOOT_WIND = 4;
    public static final int HVAC_FAN_DIRECTION_WIND = 5;
    public static final int HVAC_CYCLE_EXTERNAL = 1;
    public static final int HVAC_CYCLE_INTERNAL = 2;
    public static final int HVAC_CYCLE_AUTO = 3;
    public static final int HVAC_MIN_BLOWING_VOLUME = 0;
    public static final int HVAC_MAX_BLOWING_VOLUME = 8;
    public static final int HVAC_MAX_REAR_BLOWING_VOLUME = 5;
    public static final float HVAC_TEMPERATURE_LO = 18.0f;
    public static final float HVAC_TEMPERATURE_HI = 32.0f;
    public static final float HVAC_TEMPERATURE_STEP = 0.5f;
    public static final int HVAC_SEAT_HEATING_CLOSE = 0;
    public static final int HVAC_SEAT_HEATING_ONE = 1;
    public static final int HVAC_SEAT_HEATING_TWO = 2;
    public static final int HVAC_SEAT_HEATING_THREE = 3;
    public static final int AIR_QUALITY_SWITCH_ON = 1;
    public static final int AIR_QUALITY_SWITCH_OFF = 0;
    public static final int HVAC_POWER_SWITCH_ON = 2;
    public static final int HVAC_POWER_SWITCH_OFF = 1;
    public static final int HVAC_SEND_DISPLAY_TYPE_FRONT = 1;
    public static final int HVAC_SEND_DISPLAY_TYPE_REAR = 2;
    public static final int HVAC_SEND_DISPLAY_TYPE_CLOSE = 3;

    public static final int HVAC_RECEIVE_DISPLAY_TYPE_FRONT = 2;
    public static final int HVAC_RECEIVE_DISPLAY_TYPE_REAR = 3;
    public static final int HVAC_RECEIVE_DISPLAY_TYPE_CLOSE = 0;

    public static final int HVAC_AIR_QUALITY_MIN = 0;
    public static final int HVAC_AIR_QUALITY_EXCELLENT_MAX = 50;
    public static final int HVAC_AIR_QUALITY_GOOD_MAX = 100;
    public static final int HVAC_AIR_QUALITY_LIGHT_POLLUTION_MAX = 150;
    public static final int HVAC_AIR_QUALITY_MODERATE_POLLUTION_MAX = 200;
    public static final int HVAC_AIR_QUALITY_SEVERE_POLLUTION_MAX = 300;
    public static final int HVAC_AIR_QUALITY_SERIOUS_POLLUTION_MAX = 1000;

    public static final String[] TEMPERATURES = {"HI", "31.5", "31", "30.5", "30", "29.5", "29", "28.5",
            "28", "27.5", "27", "26.5", "26", "25.5", "25", "24.5", "24", "23.5", "23", "22.5", "22",
            "21.5", "21", "20.5", "20", "19.5", "19", "18.5", "LO"};

    public static final String[] MASSAGE_JSON_DRIVER = {
            "", "massage_driver_lwo.json", "massage_driver_middle.json", "massage_driver_high.json"
    };
    public static final String[] MASSAGE_JSON_PASSENGER = {
            "", "massage_passenger_lwo.json", "massage_passenger_middle.json", "massage_passenger_high.json"
    };
    public static final String[] MASSAGE_JSON_REAR_RIGHT = {
            "", "massage_rear_right_lwo.json", "massage_rear_right_middle.json", "massage_rear_right_high.json"
    };
    public static final String[] MASSAGE_JSON_REAR_LEFT = {
            "", "massage_rear_left_lwo.json", "massage_rear_left_middle.json", "massage_rear_left_high.json"
    };
}
