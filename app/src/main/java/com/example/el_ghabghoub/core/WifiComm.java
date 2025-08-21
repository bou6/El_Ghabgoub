package com.example.el_ghabghoub.core;

import android.content.Context;
import android.net.wifi.WifiManager;

public class WifiComm {
    private WifiManager wifiManager;
    private String device_ssid;
    private static WifiComm instance;

    private WifiComm(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiComm getInstance(Context context) {
        if (instance == null) {
            instance = new WifiComm(context);
        }
        return instance;
    }

    public String getSSID() {
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            device_ssid = wifiManager.getConnectionInfo().getSSID();
            //debug message
            System.out.println(">>> 1 SSID: " + device_ssid);
        }
        System.out.println(">>> 2 SSID: " + device_ssid);
       return device_ssid;
    }
}