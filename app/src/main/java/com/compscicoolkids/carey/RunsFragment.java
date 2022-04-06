package com.compscicoolkids.carey;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunsFragment extends Fragment {

    private View runsView;
    private RecyclerView runsRecView;
    private RunRecViewAdapter adapter;
    private ArrayList<Run> runs = new ArrayList<>();


    public RunsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_runs.
     */
    // TODO: Rename and change types and number of parameters
    public static RunsFragment newInstance(String param1, String param2) {
        RunsFragment fragment = new RunsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        runs.add(new Run("24/03/2020", "5.4km", 30));
        runs.add(new Run("24/03/2020", "5.4km", 30));
        runs.add(new Run("24/03/2020", "5.4km", 30));

         */
    }

    public void onPause () {
        super.onPause();

    }

    private void readRuns(){

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(getArguments() != null){
            Run run = getArguments().getParcelable("run");
            runs.add(run);
        }

        runsView = inflater.inflate(R.layout.fragment_runs, container, false);
        adapter = new RunRecViewAdapter(this.getActivity());
        runsRecView = runsView.findViewById(R.id.runsRecView);

        runsRecView.setAdapter(adapter);
        runsRecView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        adapter.setRuns(runs);

        return runsView;
    }
}