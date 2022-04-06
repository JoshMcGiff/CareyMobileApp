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
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        Preference button = findPreference(getString(R.string.logout));
        Preference usernameButton = findPreference(getString(R.string.username));
        assert button != null;
        assert usernameButton != null;
        usernameButton.setOnPreferenceChangeListener((preference, newValue) -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newValue.toString())
                    .build();

            assert user != null;
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Username updated", Toast.LENGTH_SHORT).show();
                        }
                    });
            return true;
        });
        button.setOnPreferenceClickListener(preference -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().recreate();
            return true;
        });
    }
}