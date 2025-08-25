package com.example.el_ghabghoub.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.el_ghabghoub.R;
import com.example.el_ghabghoub.core.Config;
import com.example.el_ghabghoub.core.Response;
import com.example.el_ghabghoub.databinding.FragmentHomeBinding;

import com.example.el_ghabghoub.core.WifiComm;
import android.os.Handler;
import android.os.Looper;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.el_ghabghoub.core.Commands;

import java.io.IOException;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable periodicTask = new Runnable() {
        @Override
        public void run() {
            performPeriodicCommand();
            handler.postDelayed(this, 1000);
        }
    };

    /**
     * Call this method to start the periodic task from your activity or fragment.
     */

    public void startPeriodicTask() {
        handler.post(periodicTask);
    }

    /**
     * Call this method to stop the periodic task.
     */

    public void stopPeriodicTask() {
        handler.removeCallbacks(periodicTask);
    }

    private String convertTimeToString(int time)
    {
        // convert the time received to days:hours:min
        int days = time / 86400;
        int hours = (time % 86400) / 3600;
        int minutes = (time % 3600) / 60;
        return String.format("Day(s): %2d \nHour(s): %02d\nMin(s): %02d ", days, hours, minutes);
    }

    /**
     * The command to be called every second. Update your frame or logic here.
     */

    private void performPeriodicCommand() {
        new Thread(() -> {
            Response response = Commands.statusCmd(requireContext());
            // Update UI on main thread
            handler.post(() -> {
                if (response != null && response.success) {
                    if (Objects.equals(response.state, "Idle"))
                        binding.statusWateringText.setText(response.state);
                    else{
                        if (response.start_time>0)
                            binding.statusWateringText.setText(R.string.watering_planned);
                        if (response.current_watering_on>0)
                            binding.statusWateringText.setText(R.string.watering_on);
                        if (response.current_watering_off>0)
                            binding.statusWateringText.setText(R.string.watering_off);
                    }

                    binding.statusStartTime.setText(convertTimeToString(response.start_time));
                    binding.statusWateringOnTime.setText(convertTimeToString(response.current_watering_on));
                    binding.statusWateringOffTime.setText(convertTimeToString(response.current_watering_off));
                    binding.statusRemainingCycles.setText(String.valueOf(response.cycles));
                }
                else {
                    binding.statusConnection.setText("Connection failed");
                }
            });
        }).start();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        WifiComm wifiComm = WifiComm.getInstance(requireContext());
        String ssid = wifiComm.getSSID();
        // Remove quotes and trim whitespace from SSID
        if (ssid != null) {
            ssid = ssid.replace("\"", "").trim();
        }
        if (!Objects.equals(ssid, Config.WATERING_DEVICE_SSID)) {
            // Display the WiFi status using string resource with placeholder
            String msg = getString(R.string.connected_to, ssid) + ", " + getString(R.string.please_make_sure_to_connect_to) + " " + Config.WATERING_DEVICE_SSID;
            binding.statusConnection.setText(msg);
            //blind the other fields
            binding.statusStart.setVisibility(View.GONE);
            binding.statusStartTime.setVisibility(View.GONE);
            binding.statusWateringOn.setVisibility(View.GONE);
            binding.statusWateringOnTime.setVisibility(View.GONE);
            binding.statusWateringOff.setVisibility(View.GONE);
            binding.statusWateringOffTime.setVisibility(View.GONE);
            binding.statusCycles.setVisibility(View.GONE);
            binding.statusRemainingCycles.setVisibility(View.GONE);
            binding.button.setVisibility(View.GONE);
        } else {
            binding.statusConnection.setText(getString(R.string.connected_to, ssid));
            startPeriodicTask();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopPeriodicTask();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    stopPeriodicTask();
    binding = null;
    }
}