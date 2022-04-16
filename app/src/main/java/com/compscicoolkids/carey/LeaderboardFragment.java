package com.compscicoolkids.carey;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        updateAllRows("");

        EditText editText = leaderboardView.findViewById(R.id.editText_searchUser);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //Log.d(TAG, "Pressed search!");
                    if (v.getText().length() > 0) {
                        updateAllRows(v.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() == 0) {
                    updateAllRows("");
                }
                else {
                    updateAllRows(s.toString());
                }
            }
        });

        TextView infoButton = leaderboardView.findViewById(R.id.leaderboardHeader);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Earn points and compete!",Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(),"Points are generated based on your run distance and time!",Toast.LENGTH_LONG).show();
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

    private void removeAllRows() {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View child = tableLayout.getChildAt(i);

            if (child instanceof TableRow) {
                tableLayout.removeView(child);
                i--; //have to decrement i as the indexes for next View gets shifted
            }
        }
    }

    private void updateAllRows(@NonNull String userName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("users");

        if (!userName.isEmpty()) {
            query = query.whereGreaterThanOrEqualTo("displayName", userName)
                    .whereLessThan("displayName", userName + "z");
            query = query.orderBy("displayName", Query.Direction.DESCENDING);
        }

        query.orderBy("points", Query.Direction.DESCENDING)
            .limit(LEADERBOARD_LIMIT)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        removeAllRows();
                        int rank = 1;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            addLeaderboardRow(rank, document);
                            rank++;
                        }
                    }
                }
            });
    }
}