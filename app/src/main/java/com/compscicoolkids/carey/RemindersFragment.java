package com.compscicoolkids.carey;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;




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

    private TextView waterFrequency;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch waterSwitch;
    private int waterIntervals;

    private TextView postureFrequency;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch postureSwitch;
    private int postureIntervals;

    private TextView standFrequency;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch standSwitch;
    private int standIntervals;

    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFutureWater;
    private ScheduledFuture<?> scheduledFuturePosture;
    private ScheduledFuture<?> scheduledFutureStand;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor myEdit;
    View reminderView;
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
    public static RemindersFragment newInstance() {
        RemindersFragment fragment = new RemindersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        reminderView = inflater.inflate(R.layout.fragment_reminders, container, false);
        sharedPreferences = getContext().getSharedPreferences("CareySharedPreferen1eh",MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        waterInit();
        postureInit();
        standInit();
        return reminderView;
    }

    private void waterInit(){
        Button waterButton = reminderView.findViewById(R.id.testWaterButton);
        Slider waterSlider = reminderView.findViewById(R.id.waterSlider);
        waterFrequency = reminderView.findViewById(R.id.waterFrequency);
        TextView title = reminderView.findViewById(R.id.waterCheckTitle);
        title.setText("💧Water Checks:");
        if(sharedPreferences.getInt("waterReminderInterval", 0) == 0) {
            waterIntervals = 1;
            waterSlider.setValue(0);
            waterFrequency.setText("Every 1 minute");
        }else{
            waterIntervals = sharedPreferences.getInt("waterReminderInterval", 0);
            waterSlider.setValue(waterIntervals);
            waterFrequency.setText("Every " + waterIntervals + " minutes");
        }
        waterSwitch = reminderView.findViewById(R.id.waterSwitch);
        waterSwitch.setChecked(sharedPreferences.getBoolean("waterReminderEnabled", false));
        if(waterSwitch.isChecked()){
            scheduleWaterReminder();
        }

        // Enable & Disable listener
        waterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                scheduleWaterReminder();

            }else{
                cancelWaterReminder();
            }
            myEdit.putBoolean("waterReminderEnabled", waterSwitch.isChecked());
            myEdit.commit();
        });

        // Test water check button listener
        waterButton.setOnClickListener(x -> {
            sendWaterReminder();
        });

        waterSlider.addOnSliderTouchListener(touchListenerWater);
    }

    private void postureInit(){
        Button postureButton = reminderView.findViewById(R.id.testPostureButton);
        Slider postureSlider = reminderView.findViewById(R.id.postureSlider);
        postureFrequency = reminderView.findViewById(R.id.postureFrequency);
        TextView title = reminderView.findViewById(R.id.postureCheckTitle);
        title.setText("🧍‍Posture Checks:");
        if(sharedPreferences.getInt("postureReminderInterval", 0) == 0) {
            postureIntervals = 1;
            postureSlider.setValue(0);
            postureFrequency.setText("Every 1 minute");
        }else{
            postureIntervals = sharedPreferences.getInt("postureReminderInterval", 0);
            postureSlider.setValue(postureIntervals);
            postureFrequency.setText("Every " + postureIntervals + " minutes");
        }
        postureSwitch = reminderView.findViewById(R.id.postureSwitch);
        postureSwitch.setChecked(sharedPreferences.getBoolean("postureReminderEnabled", false));
        if(postureSwitch.isChecked()){
            schedulePostureReminder();
        }

        // Enable & Disable listener
        postureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                schedulePostureReminder();

            }else{
                cancelPostureReminder();
            }
            myEdit.putBoolean("postureReminderEnabled", postureSwitch.isChecked());
            myEdit.commit();
        });

        // Test water check button listener
        postureButton.setOnClickListener(x -> {
            sendPostureReminder();
        });

        postureSlider.addOnSliderTouchListener(touchListenerPosture);
    }

    private void standInit(){
        Button standButton = reminderView.findViewById(R.id.testStandButton);
        Slider standSlider = reminderView.findViewById(R.id.standSlider);
        standFrequency = reminderView.findViewById(R.id.standFrequency);
        TextView title = reminderView.findViewById(R.id.standCheckTitle);
        title.setText("🚶‍♂Stand Checks:");
        if(sharedPreferences.getInt("standReminderInterval", 0) == 0) {
            standIntervals = 1;
            standSlider.setValue(0);
            standFrequency.setText("Every 1 minute");
        }else{
            standIntervals = sharedPreferences.getInt("standReminderInterval", 0);
            standSlider.setValue(standIntervals);
            standFrequency.setText("Every " + standIntervals + " minutes");
        }
        standSwitch = reminderView.findViewById(R.id.standSwitch);
        standSwitch.setChecked(sharedPreferences.getBoolean("standReminderEnabled", false));
        if(standSwitch.isChecked()){
            scheduleStandReminder();
        }

        // Enable & Disable listener
        standSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                scheduleStandReminder();

            }else{
                cancelStandReminder();
            }
            myEdit.putBoolean("standReminderEnabled", standSwitch.isChecked());
            myEdit.commit();
        });

        // Test water check button listener
        standButton.setOnClickListener(x -> {
            sendStandReminder();
        });

        standSlider.addOnSliderTouchListener(touchListenerStand);
    }

    private Runnable getWaterRunnable(){
        Runnable runnable = new Runnable() {
            public void run() {
                sendWaterReminder();
                scheduleWaterReminder();            }
        };
        return runnable;
    }

    private Runnable getPostureRunnable(){
        Runnable runnable = new Runnable() {
            public void run() {
                sendPostureReminder();
                schedulePostureReminder();            }
        };
        return runnable;
    }

    private Runnable getStandRunnable(){
        Runnable runnable = new Runnable() {
            public void run() {
                sendStandReminder();
                scheduleStandReminder();            }
        };
        return runnable;
    }

    private void sendWaterReminder(){
        if( Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Water Reminder", "Water Reminder", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getActivity().getPackageName() + "/" + R.raw.waterdrop);
            channel.setSound(sound, attributes);
            manager.createNotificationChannel(channel);
            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder(getContext(), "Water Reminder");
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.droplet_foreground);

            notification
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Water Reminder:")
                    .setContentText("It's time to drink some water!")
                    .setSmallIcon(R.mipmap.droplet_foreground)
                    .setLargeIcon(largeIcon)
                    .setSound(sound)
                    .setNumber(3);

            NotificationManager notificationManager =
                    (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);

            assert notificationManager != null;
            notificationManager.notify(1, notification.build());
        }
    }


    private void sendPostureReminder(){
        if( Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Posture Reminder", "Posture Reminder", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getActivity().getPackageName() + "/" + R.raw.posture);
            channel.setSound(sound, attributes);
            manager.createNotificationChannel(channel);
            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder(getContext(), "Posture Reminder");
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.droplet_foreground);

            notification
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Posture Reminder:")
                    .setContentText("It's time to sit up straight!")
                    .setSmallIcon(R.mipmap.droplet_foreground)
                    .setLargeIcon(largeIcon)
                    .setSound(sound)
                    .setNumber(3);

            NotificationManager notificationManager =
                    (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);

            assert notificationManager != null;
            notificationManager.notify(1, notification.build());
        }
    }

    private void sendStandReminder(){
        if( Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Stand Reminder", "Stand Reminder", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getActivity().getPackageName() + "/" + R.raw.stand);
            channel.setSound(sound, attributes);
            manager.createNotificationChannel(channel);
            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder(getContext(), "Stand Reminder");
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.droplet_foreground);

            notification
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Stand Reminder:")
                    .setContentText("It's time to stand up and walk around!")
                    .setSmallIcon(R.mipmap.droplet_foreground)
                    .setLargeIcon(largeIcon)
                    .setSound(sound)
                    .setNumber(3);

            NotificationManager notificationManager =
                    (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);

            assert notificationManager != null;
            notificationManager.notify(1, notification.build());
        }
    }

    private void scheduleWaterReminder(){
        if(waterSwitch.isChecked()){
            scheduledFutureWater = worker.schedule(getWaterRunnable(), waterIntervals, TimeUnit.MINUTES);
        }
    }

    private void schedulePostureReminder(){
        if(postureSwitch.isChecked()){
            scheduledFuturePosture = worker.schedule(getPostureRunnable(), postureIntervals, TimeUnit.MINUTES);
        }
    }

    private void scheduleStandReminder(){
        if(standSwitch.isChecked()){
            scheduledFutureStand = worker.schedule(getStandRunnable(), standIntervals, TimeUnit.MINUTES);
        }
    }

    private void cancelWaterReminder(){
        if(waterSwitch.isChecked()) {
            scheduledFutureWater.cancel(false);
        }
    }

    private void cancelPostureReminder(){
        if(postureSwitch.isChecked()) {
            scheduledFuturePosture.cancel(false);
        }
    }

    private void cancelStandReminder(){
        if(standSwitch.isChecked()) {
            scheduledFutureStand.cancel(false);
        }
    }

    private final Slider.OnSliderTouchListener touchListenerWater =
            new Slider.OnSliderTouchListener() {
                @Override
                public void onStartTrackingTouch(Slider slider) {
                    cancelWaterReminder();
                }

                @Override
                public void onStopTrackingTouch(Slider slider) {
                    System.out.println(slider.getValue());

                    if(slider.getValue() == 0){
                        waterIntervals = 1;
                        myEdit.putInt("waterReminderInterval", 0);

                    }else{
                        waterIntervals = (int) slider.getValue();
                        myEdit.putInt("waterReminderInterval", waterIntervals);
                    }



                    myEdit.commit();

                    System.out.println(sharedPreferences.getBoolean("waterReminderEnabled", false));
                    System.out.println(sharedPreferences.getInt("waterReminderInterval", 0));

                    waterFrequency.setText("Every " + waterIntervals + " minutes");
                    scheduleWaterReminder();
                }

            };

    private final Slider.OnSliderTouchListener touchListenerPosture =
            new Slider.OnSliderTouchListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onStartTrackingTouch(@NonNull Slider slider) {
                    cancelPostureReminder();
                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onStopTrackingTouch(Slider slider) {
                    System.out.println(slider.getValue());

                    if(slider.getValue() == 0){
                        postureIntervals = 1;
                        myEdit.putInt("postureReminderInterval", 0);

                    }else{
                        postureIntervals = (int) slider.getValue();
                        myEdit.putInt("postureReminderInterval", postureIntervals);
                    }



                    myEdit.commit();

                    postureFrequency.setText("Every " + postureIntervals + " minutes");
                    scheduleWaterReminder();
                }

            };

    private final Slider.OnSliderTouchListener touchListenerStand =
            new Slider.OnSliderTouchListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onStartTrackingTouch(@NonNull Slider slider) {
                    cancelStandReminder();
                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onStopTrackingTouch(Slider slider) {
                    System.out.println(slider.getValue());

                    if(slider.getValue() == 0){
                        standIntervals = 1;
                        myEdit.putInt("standReminderInterval", 0);

                    }else{
                        standIntervals = (int) slider.getValue();
                        myEdit.putInt("standReminderInterval", standIntervals);
                    }



                    myEdit.commit();

                    standFrequency.setText("Every " + standIntervals + " minutes");
                    scheduleStandReminder();
                }

            };
}