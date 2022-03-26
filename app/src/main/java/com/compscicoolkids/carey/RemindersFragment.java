package com.compscicoolkids.carey;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RemindersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RemindersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button notifyBtn;
    Slider waterSlider;
    TextView waterFrequency;

    public RemindersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Reminders.
     */
    // TODO: Rename and change types and number of parameters
    public static RemindersFragment newInstance(String param1, String param2) {
        RemindersFragment fragment = new RemindersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);
        notifyBtn = view.findViewById(R.id.notify_btn);
        waterSlider = view.findViewById(R.id.waterSlider);
        waterFrequency = view.findViewById(R.id.waterFrequency);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Water Reminder", "Water Reminder", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = requireActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        notifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                NotificationCompat.Builder builder = new NotificationCompat.Builder(RemindersFragment.this.requireActivity(), "Water Reminder");
                builder.setContentTitle("My water reminder");
                builder.setContentText("Drink water bitch");
                builder.setSmallIcon(R.drawable.ic_launcher_background);
                builder.setAutoCancel(true);
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(RemindersFragment.this.requireActivity());
                managerCompat.notify(1, builder.build());
            }
        });
        waterFrequency.setText(getString(R.string.minute_interval, (int) waterSlider.getValue()));
        waterSlider.addOnSliderTouchListener(touchListener);
        return view;
    }

    private final Slider.OnSliderTouchListener touchListener =
            new Slider.OnSliderTouchListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onStartTrackingTouch(Slider slider) {

                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onStopTrackingTouch(Slider slider) {
                    waterFrequency.setText(getString(R.string.minute_interval, (int) waterSlider.getValue()));
                }

            };
}