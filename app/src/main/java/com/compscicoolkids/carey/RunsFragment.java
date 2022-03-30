package com.compscicoolkids.carey;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View runsView;
    private RecyclerView runsRecView;
    private RunRecViewAdapter adapter;


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
        runsView = inflater.inflate(R.layout.fragment_runs, container, false);
        adapter = new RunRecViewAdapter(this.getActivity());
        runsRecView = runsView.findViewById(R.id.runsRecView);

        runsRecView.setAdapter(adapter);
        runsRecView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        ArrayList<Run> runs = new ArrayList<>();
        runs.add(new Run(0, new LatLng(-33.852, 151.211), new LatLng(-33.852, 151.211), 5.4, 30));
        runs.add(new Run(1, new LatLng(-33.852, 151.211), new LatLng(-33.852, 151.211), 5.4, 30));
        runs.add(new Run(2, new LatLng(-33.852, 151.211), new LatLng(-33.852, 151.211), 5.4, 30));
        adapter.setRuns(runs);

        return runsView;
    }
}