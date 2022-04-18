package com.compscicoolkids.carey;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private ArrayList<Run> runs;
    private FileWriter writer;


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
        if (savedInstanceState != null) {
            runs = savedInstanceState.getParcelableArrayList("RUNS");
        } else {
            writer = new FileWriter(this.requireContext());
            runs = writer.bytesToRuns();
            if (runs == null) {
                runs = new ArrayList<Run>();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("RUNS", runs);
    }

    public void onPause () {
        super.onPause();
        if(runs.size() > 0) {
            writer.runsToBytes(runs);
        }
    }

    public void onResume() {
        super.onResume();
        adapter.setRuns(runs);
    }

    public void onDestroy() {
        super.onDestroy();
        if(runs.size() > 0) {
            writer.runsToBytes(runs);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        NavController navController = NavHostFragment.findNavController(this);
        MutableLiveData<Run> liveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("run");
        liveData.observe(getViewLifecycleOwner(), new Observer<Run>() {
            @Override
            public void onChanged(Run run) {
                boolean runAdded = false;
                for(Run r : runs){
                    runAdded = r.equals(run);
                }
                if(!runAdded) {
                    runs.add(run);
                }
            }
        });

        runsView = inflater.inflate(R.layout.fragment_runs, container, false);
        adapter = new RunRecViewAdapter(this.getActivity());
        runsRecView = runsView.findViewById(R.id.runsRecView);

        runsRecView.setAdapter(adapter);
        runsRecView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        adapter.setRuns(runs);

        return runsView;
    }
}