package com.compscicoolkids.carey;

import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Build the settings page with the preferences
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        // Get the logout button
        Preference logoutButton = findPreference(getString(R.string.logout));
        // Get the username button
        Preference usernameButton = findPreference(getString(R.string.username));
        assert logoutButton != null;
        assert usernameButton != null;
        // Add a listener for when the user finishes editing the username
        usernameButton.setOnPreferenceChangeListener((preference, newValue) -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // takes the new username and updates the user's profile
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newValue.toString())
                    .build();

            assert user != null;
            // Update the user's profile and display a toast if successful
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Username updated", Toast.LENGTH_SHORT).show();
                        }
                    });
            return true;
        });
        // Add a listener for when the user clicks the logout button
        logoutButton.setOnPreferenceClickListener(preference -> {
            FirebaseAuth.getInstance().signOut();
            // Recreate the activity to bring the user back to the login screen
            requireActivity().recreate();
            return true;
        });
    }
}