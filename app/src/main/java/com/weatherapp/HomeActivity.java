package com.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.weatherapp.common.AppConstants;
import com.weatherapp.fragments.weathermanagement.WeatherForecastFragment;
import com.weatherapp.http.AppLoader;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "HomeActivity";

    private String name = "", email = "";

    //UI Reference
    private AppCompatTextView txtName, txtEmail;
    AppLoader appLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (getIntent().hasExtra("app_account_info")) {
            Bundle bundle = getIntent().getBundleExtra("app_account_info");
            name = bundle.getString("name");
            email = bundle.getString("email");
        }else if (getIntent().hasExtra("google_account_info")) {
            GoogleSignInAccount acct = getIntent().getParcelableExtra("google_account_info");
            name = acct.getDisplayName();
            email = acct.getEmail();
        }

        txtName = (AppCompatTextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        txtName.setText(name);
        txtEmail = (AppCompatTextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        txtEmail.setText(email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     *
     * Sing out success callback
     */
    private void signOut() {
        AppConstants.googleApiClient.disconnect();
        Intent loginIntent = new Intent(this, UserActivity.class);
        startActivity(loginIntent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.nav_weather_today: {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.weather_today));
                if (fragment != null && fragment.isVisible()) {
                    // Fragment already displayed
                } else {
                    setTitle(getString(R.string.weather_today));
                    getSupportFragmentManager().popBackStack();
                }
            }
            break;
            case R.id.nav_weather_history: {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.weather_history));
                if (fragment != null && fragment.isVisible()) {
                    // Fragment already displayed
                } else {
                    setTitle(getString(R.string.weather_history));
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_weather_container, new WeatherForecastFragment(), getString(R.string.weather_history));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
            break;
            case R.id.nav_logout: {
                if (getIntent().hasExtra("google_account_info")) {
                    AppConstants.googleApiClient.connect();
                    AppConstants.googleApiClient.registerConnectionCallbacks(this);
                } else {
                    signOut();
                }
            }
            break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (AppConstants.googleApiClient != null && AppConstants.googleApiClient.isConnected()){
            Auth.GoogleSignInApi.signOut(AppConstants.googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            AppConstants.googleApiClient.disconnect();
                            signOut();
                        }
                    });

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google API Client Connection Suspended");
    }

}
