package com.example.el_ghabghoub.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.el_ghabghoub.core.Config;
import com.example.el_ghabghoub.databinding.FragmentHomeBinding;

import com.example.el_ghabghoub.core.WifiComm;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WifiComm wifiComm = WifiComm.getInstance(requireContext());
        String ssid = wifiComm.getSSID();
        // Remove quotes and trim whitespace from SSID
        if (ssid != null) {
            ssid = ssid.replace("\"", "").trim();
        }
        if (!Objects.equals(ssid, Config.WATERING_DEVICE_SSID)) {
            // Display the WiFi status
            binding.statusConnection.setText("You are Connected to " + ssid + ", please make sure to connect to: " + Config.WATERING_DEVICE_SSID);
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

        }
        else {
            binding.statusConnection.setText("You are connected to: " + Config.WATERING_DEVICE_SSID);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}