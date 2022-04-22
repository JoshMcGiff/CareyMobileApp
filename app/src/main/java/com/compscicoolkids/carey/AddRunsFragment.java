package com.compscicoolkids.carey;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AddRunsFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private View addRunsView;
    private LatLng start;
    private LatLng end;
    private int routeStep;

    public AddRunsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routeStep = 0;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        addRunsView = inflater.inflate(R.layout.fragment_add_runs, container, false);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        TextView runDate = addRunsView.findViewById(R.id.runDate);
        //set date on screen to current date
        runDate.setText(String.format("%d/%d/%d", calendar.get(Calendar.DAY_OF_MONTH),(calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)));
        mapView = addRunsView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        EditText minutesInput = addRunsView.findViewById(R.id.minutes_ran);
        minutesInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0 && routeStep == 2){
                    //set add run button to visible if minutes is valid
                    Button addRun = addRunsView.findViewById(R.id.add_run);
                    addRun.setClickable(true);
                    addRun.setVisibility(View.VISIBLE);
                }
            }
        });

        Button addRun = addRunsView.findViewById(R.id.add_run);
        addRun.setOnClickListener(v -> {
            //get information needed to create run object
            int minutes = Integer.parseInt(minutesInput.getText().toString());
            TextView lengthDisplay = addRunsView.findViewById(R.id.distance_ran);
            String length = lengthDisplay.getText().toString();
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            String date = String.format("%d/%d/%d", c.get(Calendar.DAY_OF_MONTH),(c.get(Calendar.MONTH)+1), c.get(Calendar.YEAR));
            Run run = new Run(date, length, minutes);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if(user != null) {
                Map<String, Object> points = new HashMap<>();

                db.collection("users").document(user.getUid())
                    .get()
                    .addOnCompleteListener(queryDocumentSnapshots -> {
                        long oldPoints = 0;
                        if (queryDocumentSnapshots.getResult().getData() != null) {
                            //noinspection ConstantConditions
                            oldPoints = (long) queryDocumentSnapshots.getResult().getData().get("points");
                        }
                        points.put("displayName", user.getDisplayName());

                        //speed*distance, 10km done in 60 minutes -> 100 points
                        double distance = Double.parseDouble(length.substring(0, length.length() - 3));
                        double additionalPoints = (distance*distance)/((double)minutes/60);
                        points.put("points", oldPoints + (long)additionalPoints);
                        db.collection("users").document(user.getUid())
                                .set(points);
                    });
            }
            MainActivity activity = (MainActivity) requireActivity();
            //get main activity to add a run
            activity.addRun(run);


        });
        return addRunsView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //latitude and longitude object with ULs coordinates
        LatLng ul = new LatLng(52.6721418, -8.5734881);
        //zoom into UL
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(ul, 13));
        map.setOnMapClickListener(latLng -> {
            if(routeStep < 2){
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                //move to marker
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                map.addMarker(markerOptions);
                if(routeStep == 0){
                    start = latLng;
                }else{
                    end = latLng;
                    //get distance between start and end
                    new FetchURL(addRunsView).execute(getUrl());
                }
                routeStep++;
            }else{
                //user tapped on map again, reset everything
                routeStep = 0;
                map.clear();
                TextView txtDistance = addRunsView.findViewById(R.id.distance_ran);
                txtDistance.setText(getString(R.string.run_distance, 0));
                Button addRun = addRunsView.findViewById(R.id.add_run);
                addRun.setClickable(false);
                addRun.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private String getUrl() {
        //distance matrix api url
        String str_origin = "origins=" + start.latitude + "," + start.longitude;
        String str_dest = "destinations=" + end.latitude + "," + end.longitude;
        String mode = "mode=walking";
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/distancematrix/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }
}