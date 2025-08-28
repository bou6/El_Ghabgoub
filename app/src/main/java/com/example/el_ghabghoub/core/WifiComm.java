package com.example.el_ghabghoub.core;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.MalformedURLException;

public class WifiComm {
    private enum Protocol { HTTP, HTTPS };
    private WifiManager wifiManager;
    private String device_ssid;
    private static WifiComm instance;
    private Protocol protocol=Protocol.HTTP;

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
        }
        // Remove quotes and trim whitespace from SSID
        if (device_ssid != null) {
            device_ssid = device_ssid.replace("\"", "").trim();
        }
        System.out.println(">>>  SSID: " + device_ssid);
       return device_ssid;
    }

    /**
     * Callback interface for HTTP responses.
     */
    public interface HttpCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public String httpGetSync(String urlString, int timeoutMillis) throws IOException, MalformedURLException {
        java.net.URL url = new java.net.URL(urlString);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(timeoutMillis); // connection timeout
        conn.setReadTimeout(timeoutMillis);    // read timeout
        int responseCode = conn.getResponseCode();
        if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            conn.disconnect();
            return response.toString();
        } else {
            conn.disconnect();
            throw new IOException("HTTP error code: " + responseCode);
        }
}
}