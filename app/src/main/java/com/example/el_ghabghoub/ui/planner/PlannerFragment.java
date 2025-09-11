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
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.TimeZone;

public class PlannerFragment extends Fragment {
    private FragmentPlannerBinding binding;
    private long selectedStartDayinMin;
    private long selectedEndDayinMin;
    private int selectedStartHour; 
    private int selectedStartMin;
    private NumberPicker duration_hour;
    private NumberPicker duration_min;
    private NumberPicker repeat_after;
    private Button btnPickRange;
    private TextView tvSelectedRange;
    private Button btnPickTime;
    private TextView tvSelectedTime;
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

    boolean validateInputs(long startDay, long endDay, int startHour, int startMin, int durationHour, int durationMin, int repeatAfter) {
        if (startDay == 0 || endDay == 0) {
            Toast.makeText(requireContext(), "Please select a valid date range.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (repeatAfter <= 0) {
            return false;
        }

        // get the current time and test if it is less than the selected start time if start day is today
        long today = MaterialDatePicker.todayInUtcMilliseconds()/(1000*60);
        if (startDay == today) {
            Calendar now = Calendar.getInstance(); // Use device's default timezone
            int currentHour = now.get(Calendar.HOUR_OF_DAY);
            int currentMin = now.get(Calendar.MINUTE);

            // add debug print
            System.out.println("Current time: " + currentHour + ":" + currentMin);
            System.out.println("Selected start time: " + startHour + ":" + startMin);
            if (startHour < currentHour || (startHour == currentHour && startMin <= currentMin)) {
                Toast.makeText(requireContext(), "Start time must be in the future.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // check that duration hour and min are valid
        if (durationHour == 0 && durationMin == 0) {
            Toast.makeText(requireContext(), "Please select a valid duration.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        duration_hour= binding.npDurationHour;
        duration_min= binding.npDurationMin;
        repeat_after= binding.npRepeatAfterDays;
        btnPickRange = binding.btnPickRange;
        tvSelectedRange = binding.tvSelectedRange;
        btnPickTime = binding.btnPickTime;
        tvSelectedTime = binding.tvSelectedTime;

        duration_hour.setMinValue(0);
        duration_hour.setMaxValue(23);
        duration_min.setMinValue(0);
        duration_min.setMaxValue(59);
        repeat_after.setMinValue(1);
        repeat_after.setMaxValue(90);

        Context context = requireContext();

        // add listener to the start button
        binding.startButton.setOnClickListener(v -> {
            ///  #### convertion from long to int should be seen again, should make checking so that no overflow happens
            int durationHour = duration_hour.getValue();
            int durationMin = duration_min.getValue();
            int repeatAfter = repeat_after.getValue();
            int endHour = 24;
            int endMin = 0;

            long todayinMin = MaterialDatePicker.todayInUtcMilliseconds()/(1000*60);

            // calculate start time in minutes
            long startTimeMin = selectedStartDayinMin + (selectedStartHour * 60) + selectedStartMin;
            long endTimeMin = selectedEndDayinMin + (endHour * 60) + endMin;
            // calculate current time in minutes
            long currentTimeMin = todayinMin + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60) + Calendar.getInstance().get(Calendar.MINUTE);
            
            // validate inputs
            if (!validateInputs(selectedStartDayinMin, selectedEndDayinMin, selectedStartHour, selectedStartMin, durationHour, durationMin, repeatAfter)) {
                return;
            }

            int startDay = (int)(startTimeMin - currentTimeMin) / (24 * 60);
            int startHour = (int)((startTimeMin - currentTimeMin) % (24 * 60)) / 60;
            int startMin = (int)((startTimeMin - currentTimeMin) % (24 * 60)) % 60;
            System.out.println(">>> selectedStartDayinMin: " + selectedStartDayinMin + ", todayinMin: " + todayinMin);
            System.out.println(">>>> selectedEndDayinMin :"+ selectedEndDayinMin + ", endTimeMin: " + endTimeMin);
            System.out.println(">>> Calculated startDay: " + startDay + ", startHour: " + startHour + ", startMin: " + startMin);
            // log the calculated values
            System.out.println(">> Start Day: " + startDay + ", currentTimeMin: " + currentTimeMin + ", startTimeMin: " + startTimeMin);
            System.out.println(">> todayinMin: " + todayinMin + ", selectedStartDayinMin: " + selectedStartDayinMin + ", selectedStartHour: " + selectedStartHour + ", selectedStartMin: " + selectedStartMin);

            int cycles = (int)((selectedEndDayinMin - selectedStartDayinMin) / (24*60*repeatAfter)) + 1;

            System.out.println(">>>>> cycles: " + cycles);

            int wateringOffinMin = repeatAfter*24*60 - durationHour*60 - durationMin;
            if (wateringOffinMin < 0) wateringOffinMin = 0;

            // calculate watering off day, hour, min
            int wateringOffDay = wateringOffinMin / (24*60);
            int wateringOffHour = wateringOffinMin % (24*60) / 60;
            int wateringOffMin = wateringOffinMin % 60; 

            Thread thread = new Thread(() -> {
                Response response = Commands.startCmd(context,startDay, startHour, startMin, 0, durationHour, durationMin, wateringOffDay, wateringOffHour, wateringOffMin, cycles);
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
            long today = MaterialDatePicker.todayInUtcMilliseconds();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(today);
            calendar.add(Calendar.YEAR, 1);
            long oneYearAfter = calendar.getTimeInMillis();

            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                    .setStart(today)
                    .setEnd(oneYearAfter)
                    .setValidator(DateValidatorPointForward.from(today));

            MaterialDatePicker.Builder<Pair<Long, Long>> dateRangeBuilder =
                    MaterialDatePicker.Builder.dateRangePicker()
                    .setCalendarConstraints(constraintsBuilder.build())
                    .setTitleText("Select Date Range");

            final MaterialDatePicker<Pair<Long, Long>> materialDatePicker = dateRangeBuilder.build();

            // Show picker
            materialDatePicker.show(getParentFragmentManager(), "DATE_RANGE");

            // Handle selection
            materialDatePicker.addOnPositiveButtonClickListener(
                    (MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>) selection -> {
                        // selection returns a Pair<Long, Long>
                        String selected = materialDatePicker.getHeaderText();
                        tvSelectedRange.setText("Selected Range: " + selected);
                        selectedStartDayinMin = materialDatePicker.getSelection().first/(1000*60);// selected day in minutes
                        selectedEndDayinMin = materialDatePicker.getSelection().second/(1000*60);// selected day in minutes
                    }
            );
        });

         btnPickTime.setOnClickListener(v -> {

             // get the current time
            Calendar now = Calendar.getInstance(TimeZone.getDefault());
            // Build the Material Time Picker
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H) // 24-hour format
                    .setHour(now.get(Calendar.HOUR_OF_DAY))  // default hour
                    .setMinute(now.get(Calendar.MINUTE)+1) // default minute
                    .setTitleText("Select Time")
                    .build();

            // Show picker
            picker.show(getParentFragmentManager(), "DATE_RANGE");

            // Handle selection
            picker.addOnPositiveButtonClickListener(dialog -> {
                selectedStartHour = picker.getHour();
                selectedStartMin = picker.getMinute();
                String selectedTime = String.format("%02d:%02d", selectedStartHour, selectedStartMin);
                tvSelectedTime.setText("Selected Time: " + selectedTime);
            });

            picker.addOnNegativeButtonClickListener(dialog -> {
                // Handle negative button click
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
