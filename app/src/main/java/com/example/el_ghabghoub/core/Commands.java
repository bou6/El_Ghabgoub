package com.example.el_ghabghoub.core;

import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;

import com.example.el_ghabghoub.core.Response;


/// ####### to be fixed usesCleartextTraffic, this has been added to allow claer text traffic
public class Commands {


    // get the status of watering
    public static Response statusCmd(Context context)  {
        String url = "http://192.168.4.1/";
        String command = "status";
        WifiComm wifiComm = WifiComm.getInstance(context);
        String response = null;
        try {
            response = wifiComm.httpGetSync(url + command, 100);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(false);
        }
        // print received message
        System.out.println("Received response: " + response);
        return Response.parseResponse(response);
    }

    public boolean startCmd(int start_day, int start_hour, int start_min, int duration_day, int duration_hour, int duration_min, int next_day, int next_hour, int next_min, int cycles) {
        return true;
    }

    public boolean stopCmd() {
        return true;
    }
}
