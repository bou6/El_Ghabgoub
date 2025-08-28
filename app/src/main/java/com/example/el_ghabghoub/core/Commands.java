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

    public static Response startCmd(Context context, int startDay, int startHour, int startMin, int wateringOnDay, int wateringOnHour, int wateringOnMin, int wateringOffDay, int wateringOffHour, int wateringOffMin, int cycles) {
        String url = "http://192.168.4.1/";
        String command = "start?command=";
        JSONObject json = new JSONObject();
        try {
            json.put("start_day", startDay);
            json.put("start_hour", startHour);
            json.put("start_min", startMin);
            json.put("duration_day", wateringOnDay);
            json.put("duration_hour", wateringOnHour);
            json.put("duration_min", wateringOnMin);
            json.put("next_day", wateringOffDay);
            json.put("next_hour", wateringOffHour);
            json.put("next_min", wateringOffMin);
            json.put("cycles", cycles);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false);
        }
        WifiComm wifiComm = WifiComm.getInstance(context);
        String response = null;
        try {
            // replace the placeholder by the
            System.out.println("Sending command: " + url + command + json.toString());
            response = wifiComm.httpGetSync(url + command + json.toString(), 100);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(false);
        }
        // print received message
        System.out.println("Received response: " + response);
        return Response.parseResponse(response);
    }

    public static Response stopCmd(Context context) {
        String url = "http://192.168.4.1/";
        String command = "stop";
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
}
