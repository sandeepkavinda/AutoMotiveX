package lk.javainstitute.automotivex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set default fragment
        BottomNavigationView bottomNavigationView = findViewById(R.id.adminBottomNavigationView);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragment_container, new AdminDashboardFragment())
                .commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.admin_nav_dashboard) {
                    selectedFragment = new AdminDashboardFragment();
                } else if (item.getItemId() == R.id.admin_nav_product) {
                    selectedFragment = new AdminProductFragment();
                } else if (item.getItemId() == R.id.admin_nav_orders) {
                    selectedFragment = new AdminOrdersFragment();
                } else if (item.getItemId() == R.id.admin_nav_appointment) {
                    selectedFragment = new AdminAppontmentsFragment();
                }

//                else if (item.getItemId() == R.id.admin_nav_profile) {
//                    selectedFragment = new ProfileFragment();
//                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.admin_fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            }
        });


    }

}