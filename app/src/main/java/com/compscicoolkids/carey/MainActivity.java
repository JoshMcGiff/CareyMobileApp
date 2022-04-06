package com.compscicoolkids.carey;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        FirebaseApp.initializeApp(this);

        navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            // Create and launch sign-in intent
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build();
            signInLauncher.launch(signInIntent);
        }

    }

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            assert response != null;
            if (response.isNewUser()) { //if just signed up
                assert user != null;
                String userID = user.getUid();
                String userName = user.getDisplayName();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userInfo = db.collection("users").document(userID); //creates a new document, name is userID


                Map<String, Object> profile = new HashMap<>();
                profile.put("points", 0); //start with 0 points
                assert userName != null;
                profile.put("displayName", userName.isEmpty() ? "Anonymous" : userName);

                userInfo.set(profile).addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Set up User Profile!", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Setup User Profile Failed!", Toast.LENGTH_LONG).show());
            }

        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(getApplicationContext(), R.string.failed_login, Toast.LENGTH_LONG).show();
        }
    }

    public void addRun(View view){
        navController.navigate(R.id.action_runs_to_add_runs);
    }

    public void toRuns(View view){
        navController.navigate(R.id.action_add_runs_to_runs);
    }

    public void addRun(Run run){
        Bundle bundle = new Bundle();
        bundle.putParcelable("run", run);
        navController.navigate(R.id.action_add_runs_to_runs, bundle);
    }
}