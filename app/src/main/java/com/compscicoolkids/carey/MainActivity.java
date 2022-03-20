package com.compscicoolkids.carey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity {

    Button notifyBtn;
    Slider waterSlider;
    TextView waterFrequency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notifyBtn = findViewById(R.id.notify_btn);
        waterSlider = findViewById(R.id.waterSlider);
        waterFrequency = findViewById(R.id.waterFrequency);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Water Reminder", "Water Reminder", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        notifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "Water Reminder");
                builder.setContentTitle("My water reminder");
                builder.setContentText("Drink water bitch");
                builder.setSmallIcon(R.drawable.ic_launcher_background);
                builder.setAutoCancel(true);
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                managerCompat.notify(1, builder.build());
            }
        });
        waterFrequency.setText("Every " + (int) waterSlider.getValue() + " minutes");
        waterSlider.addOnSliderTouchListener(touchListener);
    }

    private final Slider.OnSliderTouchListener touchListener =
            new Slider.OnSliderTouchListener() {
                @Override
                public void onStartTrackingTouch(Slider slider) {

                }

                @Override
                public void onStopTrackingTouch(Slider slider) {
                    waterFrequency.setText("Every " + (int) slider.getValue() + " minutes");
                }

            };
}