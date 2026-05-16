package com.example.swiftdelivery.agent;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.swiftdelivery.R;
import com.google.android.material.navigation.NavigationView;

public class AgentNavHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    FragmentManager fragmentManager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agent_nav_home);

        toolbar = findViewById(R.id.toolbar_agent);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout_agent);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_drawer_agent);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        openFragment(new AgentHomeFragment());
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home_agent)
        {
            openFragment(new AgentHomeFragment());
        }
        else if (itemId == R.id.nav_myacnt_agent)
        {
            openFragment(new AgentAccountFragment());
        }
        else if (itemId == R.id.nav_availdelv_agent)
        {
            openFragment(new AgentAvailableDeliveryFragment());
        }
        else if (itemId == R.id.nav_active_agent)
        {
            openFragment(new AgentActiveDeliveryFragment());
        }
        else if (itemId == R.id.nav_support_agent)
        {
            openFragment(new AgentSupportFragment());
        }
        else if (itemId == R.id.nav_history_agent)
        {
            openFragment(new AgentDeliveryHistoryFragment());
        }
        else if (itemId == R.id.nav_logout_agent)
        {
            Intent int_agent_logout = new Intent(AgentNavHomeActivity.this, AgentLoginActivity.class);
            startActivity(int_agent_logout);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container_agent, fragment);
        transaction.commit();
    }
}