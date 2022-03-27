package com.compscicoolkids.carey;

import static android.view.Gravity.CENTER;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardFragment extends Fragment {
    private static final long LEADERBOARD_LIMIT = 10;
    public static final String TAG = "LeaderboardFragment";

    private View leaderboardView;
    private TableLayout tableLayout;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Leaderboard.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaderboardFragment newInstance() {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        leaderboardView = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        tableLayout = leaderboardView.findViewById(R.id.leaderboard_layout);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(LEADERBOARD_LIMIT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int rank = 1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //TableRow row = new TableRow();
                                addLeaderboardRow(rank, document);
                                rank++;
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        return leaderboardView;
    }

    private int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void addLeaderboardRow(int rank, QueryDocumentSnapshot document) {
        Map<String, Object> map = document.getData();
        TableRow row = new TableRow(tableLayout.getContext());

        {
            TextView rankText = new TextView(row.getContext());
            rankText.setText("#" + rank);
            rankText.setMaxWidth(dpToPx(45, rankText.getContext()));
            rankText.setMinWidth(dpToPx(45, rankText.getContext()));
            rankText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
            rankText.setGravity(Gravity.CENTER);
            row.addView(rankText);
        }

        {
            TextView usernameText = new TextView(row.getContext());
            String username = (String)map.get("displayName");
            usernameText.setText(username);
            usernameText.setMaxWidth(dpToPx(240, usernameText.getContext()));
            usernameText.setMinWidth(dpToPx(240, usernameText.getContext()));
            usernameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
            usernameText.setGravity(Gravity.CENTER);
            row.addView(usernameText);
        }

        {
            TextView pointsText = new TextView(row.getContext());
            long points = (long)map.get("points");
            pointsText.setText(String.valueOf(points));
            pointsText.setMaxWidth(dpToPx(128, pointsText.getContext()));
            pointsText.setMinWidth(dpToPx(128, pointsText.getContext()));
            pointsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
            pointsText.setGravity(Gravity.CENTER);
            row.addView(pointsText);
        }

        tableLayout.addView(row);
    }
}