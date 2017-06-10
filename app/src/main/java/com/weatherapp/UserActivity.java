package com.weatherapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.weatherapp.fragments.usermanagement.RegisterFragment;

/**
 * A login screen that offers login via email/password.
 */
public class UserActivity extends FragmentActivity {

    // Constants & variables
    private static final String TAG = "UserActivity";
    private AppCompatButton btnChangeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        btnChangeView = (AppCompatButton) findViewById(R.id.btn_app_register);
    }

    public void changeView(View view) {
        if (view instanceof AppCompatButton) {
            String title = btnChangeView.getText().toString();
            if (getString(R.string.action_register).equals(title)) {
                btnChangeView.setText(getString(R.string.action_sign_in));
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new RegisterFragment());
                transaction.addToBackStack(getString(R.string.action_sign_in));
                transaction.commit();
            }else if (getString(R.string.action_sign_in).equals(title)) {
                btnChangeView.setText(getString(R.string.action_register));
                getSupportFragmentManager().popBackStack();
            }
        }
    }

}

