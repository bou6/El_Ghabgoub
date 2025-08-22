package com.example.el_ghabghoub.core;

import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;

import com.example.el_ghabghoub.core.Response;


/// ####### to be fixed usesCleartextTraffic, this has been added to allow claer text traffic
public class Commands {
    /**
     * Holds the parsed status info fields from the JSON response.
     */
    public static class StatusResponse {
        public String status;
        public int cycles;
        public String state;
        public int current_watering_on;
        public int start_time;
        public int current_watering_off;
    }

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
            return new Response(false, e.getMessage(), null);
        }
        // print received message
        System.out.println("Received response: " + response);
        return new Response(true, "Success", response);
    }

    public boolean startCmd(int start_day, int start_hour, int start_min, int duration_day, int duration_hour, int duration_min, int next_day, int next_hour, int next_min, int cycles) {
        return true;
    }

    public boolean stopCmd() {
        return true;
    }

    /**
     * Parses the JSON response and returns a StatusResponse object with the fields.
     */
    public static StatusResponse parseStatusResponse(String json) {
        StatusResponse info = new StatusResponse();
        try {
            JSONObject obj = new JSONObject(json);
            info.status = obj.optString("status");
            info.cycles = obj.optInt("cycles");
            info.state = obj.optString("state");
            info.current_watering_on = obj.optInt("current_watering_on");
            info.start_time = obj.optInt("start_time");
            info.current_watering_off = obj.optInt("current_watering_off");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

}
