package com.example.licenseview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LicenseActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    Activity a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(CaptureFragment.newInstance("", ""));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        a = this;
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_profile:
                        openFragment(ProfileFragment.newInstance("", ""));
                        return true;
                    case R.id.navigation_capture:
                        openFragment(CaptureFragment.newInstance("", ""));
                        return true;
                    case R.id.navigation_history:
                        openFragment(HistoryFragment.newInstance("", ""));
                        return true;
                }
                return false;
            };

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}