package com.example.el_ghabghoub.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.el_ghabghoub.R;
import com.example.el_ghabghoub.core.Config;
import com.example.el_ghabghoub.core.Response;
import com.example.el_ghabghoub.databinding.FragmentHomeBinding;

import com.example.el_ghabghoub.core.WifiComm;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.el_ghabghoub.core.Commands;
import com.example.el_ghabghoub.ui.planner.PlannerFragment;

import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable periodicTask = new Runnable() {
        @Override
        public void run() {
            manageUi();
            handler.postDelayed(this, 1000);
        }
    };

    public void startPeriodicTask() {
        handler.post(periodicTask);
    }

    public void stopPeriodicTask() {
        handler.removeCallbacks(periodicTask);
    }

    private String convertTimeToString(int time)
    {
        // convert the time received to days:hours:min
        int days = time / 1440;
        int hours = (time % 1440)/60;
        int minutes = time % 60;
        return String.format("Day(s): %2d \nHour(s): %02d\nMin(s): %02d ", days, hours, minutes);
    }

    private void handleNotConnectedState(String ssid)
    {
        handler.post(()->{
            binding.statusConnection.setText(getString(R.string.connected_to,ssid));
            binding.statusWatering.setVisibility(View.GONE);
            binding.statusWateringText.setText(R.string.please_make_sure_to_connect_to);
            //blind the other fields
            binding.statusStart.setVisibility(View.GONE);
            binding.statusStartTime.setVisibility(View.GONE);
            binding.statusWateringOn.setVisibility(View.GONE);
            binding.statusWateringOnTime.setVisibility(View.GONE);
            binding.statusWateringOff.setVisibility(View.GONE);
            binding.statusWateringOffTime.setVisibility(View.GONE);
            binding.statusCycles.setVisibility(View.GONE);
            binding.statusRemainingCycles.setVisibility(View.GONE);
            binding.buttonStop.setVisibility(View.GONE);
            binding.buttonSchedule.setVisibility(View.GONE);
        });
    }

    private void handleIdleState(String ssid, Response response)
    {
        handler.post(()->{
            binding.statusConnection.setText(getString(R.string.connected_to,ssid));
            binding.statusWatering.setVisibility(View.VISIBLE);
            binding.statusWateringText.setText(response.state);
            binding.statusStart.setVisibility(View.GONE);
            binding.statusStartTime.setVisibility(View.GONE);
            binding.statusWateringOnTime.setVisibility(View.GONE);
            binding.statusWateringOffTime.setVisibility(View.GONE);
            binding.statusRemainingCycles.setVisibility(View.GONE);
            binding.statusWateringOn.setVisibility(View.GONE);
            binding.statusWateringOff.setVisibility(View.GONE);
            binding.statusCycles.setVisibility(View.GONE);
            binding.statusRemainingCycles.setVisibility(View.GONE);
            binding.buttonStop.setVisibility(View.GONE);
            binding.buttonSchedule.setVisibility(View.VISIBLE);
        });
    }

    private void handleWateringState(String ssid, Response response)
    {
        handler.post(()->{
            binding.statusConnection.setText(getString(R.string.connected_to,ssid));
            binding.statusWatering.setVisibility(View.VISIBLE);

            if (response.start_time>0)
                binding.statusWateringText.setText(R.string.watering_planned);
            else if (response.current_watering_on>0)
                binding.statusWateringText.setText(R.string.watering_on);
            else if (response.current_watering_off>0)
                binding.statusWateringText.setText(R.string.watering_off);

            binding.statusStart.setVisibility(View.VISIBLE);
            binding.statusStartTime.setVisibility(View.VISIBLE);
            binding.statusWateringOnTime.setVisibility(View.VISIBLE);
            binding.statusWateringOffTime.setVisibility(View.VISIBLE);
            binding.statusRemainingCycles.setVisibility(View.VISIBLE);
            binding.statusCycles.setVisibility(View.VISIBLE);

            binding.statusStartTime.setText(convertTimeToString(response.start_time));
            binding.statusWateringOnTime.setText(convertTimeToString(response.current_watering_on));
            binding.statusWateringOffTime.setText(convertTimeToString(response.current_watering_off));
            binding.statusRemainingCycles.setText(String.valueOf(response.cycles));

            binding.buttonStop.setVisibility(View.VISIBLE);
            binding.buttonSchedule.setVisibility(View.GONE);

        });
    }

    private void handleUnknownError(String ssid, Response response)
    {
        handler.post(()->{
            binding.statusConnection.setText(getString(R.string.connected_to,ssid));
            binding.statusWatering.setVisibility(View.GONE);
            binding.statusWateringText.setText(R.string.unknown_error);
            binding.statusStartTime.setVisibility(View.GONE);
            binding.statusWateringOnTime.setVisibility(View.GONE);
            binding.statusWateringOffTime.setVisibility(View.GONE);
            binding.statusRemainingCycles.setVisibility(View.GONE);
            binding.statusStart.setVisibility(View.GONE);
            binding.statusStartTime.setVisibility(View.GONE);
            binding.statusWateringOnTime.setVisibility(View.GONE);
            binding.statusWateringOffTime.setVisibility(View.GONE);
            binding.statusCycles.setVisibility(View.GONE);
            binding.statusRemainingCycles.setVisibility(View.GONE);
            binding.buttonStop.setVisibility(View.GONE);
            binding.buttonSchedule.setVisibility(View.GONE);
        });
    }

    private void manageUi()
    {
    WifiComm wifiComm = WifiComm.getInstance(requireContext());
    // check if status is Idle
    new Thread(() -> {
        // check if connected to the correct WiFi

        String ssid = wifiComm.getSSID();

        if (!Objects.equals(ssid, Config.WATERING_DEVICE_SSID)) {
            handleNotConnectedState(ssid);
            return;
        }

        Response response = Commands.statusCmd(requireContext());
        if ((response == null) || (!response.success)) {
            handleUnknownError(ssid,response);
            return;
        }

        if (Objects.equals(response.state, "Idle")) {
            handleIdleState(ssid,response);
            return;
        }
        handleWateringState(ssid,response);

    }).start();
}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    public void scheduleBtnHandler() {
        // Create the new fragment
        PlannerFragment plannerFragment = new PlannerFragment();

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_homeFragment_to_plannerFragment);
    }

    public void stopBtnHandler() {
        WifiComm wifiComm = WifiComm.getInstance(requireContext());
        new Thread(() -> {
            // Stop the watering (network call)
            Response response = Commands.stopCmd(requireContext());
            // Update UI on main thread
            handler.post(() -> {
                if (response != null && response.success) {
                    Toast.makeText(requireContext(), "Watering stopped", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to stop watering", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSchedule.setOnClickListener(v -> {
            scheduleBtnHandler();
        });

        binding.buttonStop.setOnClickListener(v -> {
            stopBtnHandler();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        startPeriodicTask() ;
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