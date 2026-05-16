package com.example.swiftdelivery.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.swiftdelivery.R;
import com.example.swiftdelivery.user.UserHomeFragment;
import com.google.android.material.navigation.NavigationView;

public class AdminNavHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    FragmentManager fragmentManager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_nav_home);

        toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout_admin);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_drawer_admin);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        openFragment(new AdminHomeFragment());
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home_admin) {
            openFragment(new AdminHomeFragment());
        } else if (itemId == R.id.nav_mngUsers_admin) {
            openFragment(new AdminManageUserFragment());
        } else if (itemId == R.id.nav_mngAgents_admin) {
            openFragment(new AdminManageAgentFragment());
        } else if (itemId == R.id.nav_mngDeliveries_admin) {
            openFragment(new AdminManageDeliveryFragment());
        } else if (itemId == R.id.nav_support_admin) {
            openFragment(new AdminSupportFragment());
        } else if (itemId == R.id.nav_insights_admin) {
            openFragment(new AdminAppInsightsFragment());
        } else if (itemId == R.id.nav_reports_admin) {
            openFragment(new AdminReportsFragment());
        } else if (itemId == R.id.nav_logout_admin) {
            Intent int_logout = new Intent(AdminNavHomeActivity.this, AdminLoginActivity.class);
            startActivity(int_logout);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed () {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container_admin, fragment);
        transaction.commit();
    }
}