package com.adayo.app.setting.utils.wifiAP;

import java.util.ArrayList;




public interface FinishScanListener {

	public void onFinishScan(ArrayList<ClientScanResult> clients);
}