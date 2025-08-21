package com.example.el_ghabghoub.core;

public class Commands {
    // get the status of watering
    public static String getStatusCmd() {
        return "status";
    }

    public boolean startCmd(int start_day, int start_hour, int start_min, int duration_day, int duration_hour, int duration_min, int next_day, int next_hour, int next_min, int cycles) {
        return true;
    }

    public boolean stopCmd() {
        return true;
    }
}
