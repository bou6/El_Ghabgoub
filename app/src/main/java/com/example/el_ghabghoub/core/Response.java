package com.example.el_ghabghoub.core;

import org.json.JSONObject;
import  com.example.el_ghabghoub.core.Config;

/**
 * Holds the parsed status info fields from the JSON response.
 */
public class Response {
    public int cycles;
    public String state;
    public int current_watering_on;
    public int start_time;
    public int current_watering_off;
    public boolean success;

    public Response(boolean success, String state, int cycles, int current_watering_on, int start_time, int current_watering_off) {
        this.success = success;
        this.state = state;
        this.cycles = cycles;
        this.current_watering_on = current_watering_on;
        this.start_time = start_time;
        this.current_watering_off = current_watering_off;
    }

    public Response(boolean success) {
        this.success = success;
        this.state = "Idle";
        this.cycles = 0;
        this.current_watering_on = 0;
        this.start_time = 0;
        this.current_watering_off = 0;
    }

    public static Response parseResponse(String json) {
        Response response = null;
        try {
            JSONObject obj = new JSONObject(json);
            String success_string = obj.optString("status");
            boolean success = "success".equals(success_string);
            String state = obj.optString("state");
            int cycles = obj.optInt("cycles");
            int current_watering_on = obj.optInt("current_watering_on");
            int start_time = obj.optInt("start_time");
            int current_watering_off = obj.optInt("current_watering_off");
            response = new Response(success, state, cycles, current_watering_on, start_time, current_watering_off);
        } catch (Exception e) {
            e.printStackTrace();
            return response;
        }
        return response;
    }
}
