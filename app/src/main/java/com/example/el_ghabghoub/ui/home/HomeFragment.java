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

import com.example.el_ghabghoub.databinding.FragmentHomeBinding;

import com.example.el_ghabghoub.core.WifiComm;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

        // Check location permission before accessing WiFi SSID
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            showWifiStatus();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showWifiStatus();
            } else {
                binding.statusConnection.setText("Location permission denied");
            }
        }
    }

    private void showWifiStatus() {
        /*#### make sure that every device has its own SSID #### */
        WifiComm wifiComm = WifiComm.getInstance(requireContext());
        String ssid = wifiComm.getSSID();
        if (ssid != Config.WATERING_DEVICE_SSID) {
            // Display the WiFi status
            binding.statusConnection.setText("You are Connected to " + ssid + ", please make sure to connect to: " + Config.WATERING_DEVICE_SSID);
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