package com.example.uts_mobile_02995;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.uts_mobile_02995.databinding.ActivityMainBinding;
import com.example.uts_mobile_02995.utils.BadgeUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        navView.setOnItemSelectedListener(item -> {
            int destinationId = item.getItemId();

            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() != destinationId) {

                navController.popBackStack(navController.getGraph().getStartDestinationId(), false);
                navController.navigate(destinationId);
            }

            return true;
        });

        BadgeUtils.updateKeranjangBadge(this, navView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (navView != null) {
            BadgeUtils.updateKeranjangBadge(this, navView);
        }
    }
}
