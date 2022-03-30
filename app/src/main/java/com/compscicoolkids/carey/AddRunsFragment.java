package com.compscicoolkids.carey;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRunsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRunsFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MapView mapView;
    private View addRunsView;
    private LatLng start;
    private LatLng end;
    private int routeStep;

    public AddRunsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddRunsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddRunsFragment newInstance(String param1, String param2) {
        AddRunsFragment fragment = new AddRunsFragment();
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
        runDate.setText(String.format("%d/%d/%d", calendar.get(Calendar.DAY_OF_MONTH),(calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.YEAR)));

        mapView = addRunsView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return addRunsView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if(routeStep < 2){
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    map.addMarker(markerOptions);
                    if(routeStep == 0){
                        start = latLng;
                    }else{
                        end = latLng;
                        new FetchURL(addRunsView).execute(getUrl());
                    }
                    routeStep++;
                }else{
                    routeStep = 0;
                    map.clear();
                    TextView txtDistance = addRunsView.findViewById(R.id.distance_ran);
                    txtDistance.setText("0 km");
                }
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @SuppressLint("DefaultLocale")
    private String getCoord(LatLng coord){
        return String.format("%.4f, %.4f", coord.latitude, coord.longitude);
    }

    private String getUrl() {
        String str_origin = "origins=" + start.latitude + "," + start.longitude;
        String str_dest = "destinations=" + end.latitude + "," + end.longitude;
        String mode = "mode=walking";
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/distancematrix/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }
}