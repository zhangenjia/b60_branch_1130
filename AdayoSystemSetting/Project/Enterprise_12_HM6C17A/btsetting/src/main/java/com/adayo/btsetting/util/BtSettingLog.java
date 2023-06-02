package com.adayo.btsetting.util;

import android.util.Log;

/**
 * @author Y4134
 */
public class BtSettingLog {

	private static final String TAG = "BTSettingLog_0713";

	public static void logD(String className, String msg){
		Log.d(TAG, "==>>[" + className + "]" + msg);
	}

	public static void logD(String className, String msg, String msg2) {
		Log.d(TAG, "==>>[" + className + "]" + msg + " --- " + msg2);
	}

	public static void logD(String className, String msg, String msg2, String msg3) {
		Log.d(TAG, "==>>[" + className + "]" + msg + " --- " + msg2+ " --- " + msg3);
	}

	public static void logD(String className, String msg, String msg2, String msg3, String msg4) {
		Log.d(TAG, "==>>[" + className + "]" + msg + " --- " + msg2+ " --- " + msg3+ " --- " + msg4);
	}

	/**
	 * SpeechLog.logD
	 * 
	 * @param className
	 * 				类名
	 * 
	 * @param msg
	 * 				message
	 * 
	 * @return null
	 */
	public static void logI(String className, String msg) {
		Log.i(TAG, "==>>[" + className + "]" + msg);
	}
	/**
	 * Log.e
	 * 
	 * @param className
	 * 				类名
	 * 
	 * @param msg
	 * 				message
	 * 
	 * @return null
	 */
	public static void logE(String className, String msg) {
		Log.e(TAG, "==>>[" + className + "]" + msg);
	}
	/**
	 * SpeechLog.logD
	 * 
	 * @param className
	 * 				类名
	 * 
	 * @param msg
	 * 				message
	 * 
	 *  
	 * @param msg2
	 * 				message2
	 * 
	 * @return null
	 */
	public static void logI(String className, String msg, String msg2) {
		Log.i(TAG, "==>>[" + className + "]" + msg + " --- " + msg2);
	}
	/**
	 * SpeechLog.logD
	 * 
	 * @param className
	 * 				类名
	 * 
	 * @param msg
	 * 				message
	 * 
	 *  
	 * @param msg2
	 * 				message2
	 *
	 *   
	 * @param msg3
	 * 				message3
	 * @return null
	 */
	public static void logI(String className, String msg, String msg2, String msg3) {
		Log.i(TAG, "==>>[" + className + "]" + msg + " --- " + msg2+ " --- " + msg3);
	}
	
	/**
	 * SpeechLog.logD
	 * 
	 * @param className
	 * 				类名
	 * 
	 * @param msg
	 * 				message
	 * 
	 *  
	 * @param msg2
	 * 				message2
	 *  
	 * @param msg3
	 * 				message3
	 *  
	 * @param msg4
	 * 				message4
	 * @return null
	 */
	public static void logI(String className, String msg, String msg2, String msg3, String msg4) {
		Log.i(TAG, "==>>[" + className + "]" + msg + " --- " + msg2+ " --- " + msg3+ " --- " + msg4);
	}
	/**
	 * SpeechLog.logD
	 * 
	 * @param className
	 * 				类名
	 * 
	 * @param msg
	 * 				message
	 * 
	 *  
	 * @param msg2
	 * 				message2
	 * 
	 * @return null
	 */
	public static void logE(String className, String msg, String msg2) {
		Log.e(TAG, "==>>[" + className + "]" + msg + " --- " + msg2);
	}
	/**
	 * SpeechLog.logD
	 * 
	 * @param className
	 * 				类名
	 * 
	 * @param msg
	 * 				message
	 * 
	 *  
	 * @param msg2
	 * 				message2
	 *
	 *   
	 * @param msg3
	 * 				message3
	 * @return null
	 */
	public static void logE(String className, String msg, String msg2, String msg3) {
		Log.e(TAG, "==>>[" + className + "]" + msg + " --- " + msg2+ " --- " + msg3);
	}
	
	/**
	 * SpeechLog.logD
	 * 
	 * @param className
	 * 				类名
	 * 
	 * @param msg
	 * 				message
	 * 
	 *  
	 * @param msg2
	 * 				message2
	 *  
	 * @param msg3
	 * 				message3
	 *  
	 * @param msg4
	 * 				message4
	 * @return null
	 */
	public static void logE(String className, String msg, String msg2, String msg3, String msg4) {
		Log.e(TAG, "==>>[" + className + "]" + msg + " --- " + msg2+ " --- " + msg3+ " --- " + msg4);
	}

	public static void logV(String className, String msg){
		Log.v(TAG, "==>>[" + className + "]" + msg);
	}

	public static void logV(String className, String msg, String msg2) {
		Log.v(TAG, "==>>[" + className + "]" + msg + " --- " + msg2);
	}

	public static void logV(String className, String msg, String msg2, String msg3) {
		Log.v(TAG, "==>>[" + className + "]" + msg + " --- " + msg2+ " --- " + msg3);
	}

	public static void logV(String className, String msg, String msg2, String msg3, String msg4) {
		Log.v(TAG, "==>>[" + className + "]" + msg + " --- " + msg2+ " --- " + msg3+ " --- " + msg4);
	}
}
