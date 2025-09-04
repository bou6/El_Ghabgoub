package com.example.el_ghabghoub.ui.planner;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.el_ghabghoub.R;
import com.example.el_ghabghoub.core.Commands;
import com.example.el_ghabghoub.core.Response;
import com.example.el_ghabghoub.databinding.FragmentPlannerBinding;
import com.example.el_ghabghoub.ui.home.HomeFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class PlannerFragment extends Fragment {
    private FragmentPlannerBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPlannerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        return root;
    }

    void handleStartSuccess(Response response)
    {
        handler.post(() -> {
            // open Home Fragment
            HomeFragment homeFragment = new HomeFragment();
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_plannerFragment_to_homeFragment);
        });
    }

    void handleStartError(Response response)
    {
        handler.post(() -> {
            // show error message
            String errorMessage = getString(R.string.unknown_error);
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NumberPicker startDayPicker = binding.npStartDay;
        NumberPicker startHourPicker = binding.npStartHour;
        NumberPicker startMinPicker = binding.npStartMin;
        NumberPicker wateringOnDayPicker = binding.npWatDurDay;
        NumberPicker wateringOnHourPicker = binding.npWatDurHour;
        NumberPicker wateringOnMinPicker = binding.npWatDurMin;
        NumberPicker wateringOffDayPicker = binding.npWatOffDay;
        NumberPicker wateringOffHourPicker = binding.npWatOffHour;
        NumberPicker wateringOffMinPicker = binding.npWatOffMin;
        NumberPicker cyclesPicker = binding.npNbCycle;
        Button btnPickRange = binding.btnPickRange;
        TextView tvSelectedRange = binding.tvSelectedRange;
        Button btnPickTime = binding.btnPickTime;
        TextView tvSelectedTime = binding.tvSelectedTime;

        startDayPicker.setMinValue(0);
        startDayPicker.setMaxValue(30);
        startHourPicker.setMinValue(0);
        startHourPicker.setMaxValue(23);
        startMinPicker.setMinValue(0);
        startMinPicker.setMaxValue(59);
        wateringOnDayPicker.setMinValue(0);
        wateringOnDayPicker.setMaxValue(30);
        wateringOnHourPicker.setMinValue(0);
        wateringOnHourPicker.setMaxValue(23);
        wateringOnMinPicker.setMinValue(0);
        wateringOnMinPicker.setMaxValue(59);
        wateringOffDayPicker.setMinValue(0);
        wateringOffDayPicker.setMaxValue(30);
        wateringOffHourPicker.setMinValue(0);
        wateringOffHourPicker.setMaxValue(23);
        wateringOffMinPicker.setMinValue(0);
        wateringOffMinPicker.setMaxValue(59);
        cyclesPicker.setMinValue(1);
        cyclesPicker.setMaxValue(10);
        

        Context context = requireContext();

        // add listener to the start button
        binding.startButton.setOnClickListener(v -> {
            // get the values given by the user
            int startDay = startDayPicker.getValue();
            int startHour = startHourPicker.getValue();
            int startMin = startMinPicker.getValue();

            int wateringOnDay = wateringOnDayPicker.getValue();
            int wateringOnHour = wateringOnHourPicker.getValue();
            int wateringOnMin = wateringOnMinPicker.getValue();

            int wateringOffDay = wateringOffDayPicker.getValue();
            int wateringOffHour = wateringOffHourPicker.getValue();
            int wateringOffMin = wateringOffMinPicker.getValue();

            int cycles = cyclesPicker.getValue();

            Thread thread = new Thread(() -> {
                Response response = Commands.startCmd(context,startDay, startHour, startMin, wateringOnDay, wateringOnHour, wateringOnMin, wateringOffDay, wateringOffHour, wateringOffMin, cycles);
                if (response != null && response.success) {
                    handleStartSuccess(response);
                } else {
                    handleStartError(response);
                }
            });
            thread.start();

        });

        
        btnPickRange.setOnClickListener(v -> {
            // Build the Material Date Range Picker
            MaterialDatePicker.Builder<Pair<Long, Long>> dateRangeBuilder =
                    MaterialDatePicker.Builder.dateRangePicker();

            dateRangeBuilder.setTitleText("Select Date Range");

            final MaterialDatePicker<Pair<Long, Long>> materialDatePicker = dateRangeBuilder.build();

            // Show picker
            materialDatePicker.show(getParentFragmentManager(), "DATE_RANGE");

            // Handle selection
            materialDatePicker.addOnPositiveButtonClickListener(
                    (MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>) selection -> {
                        // selection returns a Pair<Long, Long>
                        String selected = materialDatePicker.getHeaderText();
                        tvSelectedRange.setText("Selected Range: " + selected);
                    }
            );
        });

         btnPickTime.setOnClickListener(v -> {
            // Build the Material Time Picker
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H) // 24-hour format
                    .setHour(12)  // default hour
                    .setMinute(0) // default minute
                    .setTitleText("Select Time")
                    .build();

            // Show picker
            picker.show(getParentFragmentManager(), "DATE_RANGE");

            // Handle selection
            picker.addOnPositiveButtonClickListener(dialog -> {
                int hour = picker.getHour();
                int minute = picker.getMinute();
                String selectedTime = String.format("%02d:%02d", hour, minute);
                tvSelectedTime.setText("Selected Time: " + selectedTime);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
