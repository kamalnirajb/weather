package com.weatherapp.fragments.usermanagement;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.weatherapp.HomeActivity;
import com.weatherapp.R;
import com.weatherapp.common.AppConstants;
import com.weatherapp.common.AppSettings;
import com.weatherapp.http.AppWebservices;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements TextView.OnEditorActionListener, View.OnClickListener, AppWebservices.AppWebServiceCallBack{

    private static final String TAG = "RegisterFragment";

    // UI references.
    private AutoCompleteTextView editTxtName;
    private AutoCompleteTextView editTxtEmail;
    private EditText editTxtPassword, editTextConfirmPassword;
    private View scrollViewRegiserForm;
    private AppCompatButton btnRegister;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        // Set up the login form.
        scrollViewRegiserForm = view.findViewById(R.id.register_form);
        editTxtName = (AutoCompleteTextView) view.findViewById(R.id.name);
        editTxtEmail = (AutoCompleteTextView)  view.findViewById(R.id.email);
        editTxtPassword = (EditText)  view.findViewById(R.id.password);
        editTextConfirmPassword = (EditText) view.findViewById(R.id.confirm_password);
        editTextConfirmPassword.setOnEditorActionListener(this);

        btnRegister = (AppCompatButton)  view.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
        return view;
    }
    @Override
    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            register();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register: {
                register();
            }
            break;
        }
    }

    private void register() {
        try {
            // Reset errors.
            editTxtName.setError(null);
            editTxtEmail.setError(null);
            editTxtPassword.setError(null);

            // Store values at the time of the login attempt.
            String name = editTxtName.getText().toString();
            String email = editTxtEmail.getText().toString();
            String password = editTxtPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            boolean cancel = false;
            View focusView = null;

            if (TextUtils.isEmpty(email)) {
                editTxtEmail.setError(getString(R.string.error_email_required));
                focusView = editTxtEmail;
                cancel = true;
            } else if (TextUtils.isEmpty(password)) {
                editTxtPassword.setError(getString(R.string.error_password_required));
                focusView = editTxtPassword;
                cancel = true;
            } else if (!password.equals(confirmPassword)){
                editTextConfirmPassword.setError(getString(R.string.error_password_confirmpassword_same));
                focusView = editTxtPassword;
                cancel = true;
            }

            if (cancel) {
                focusView.requestFocus();
            } else  {

                String params = "{\n" +
                        "                        \"name\" : "+ name +",\n" +
                        "                        \"email\" : "+ email +",\n" +
                        "                        \"password\" : "+ password +"\n" +
                        "                }";


                new AppWebservices(getActivity(), getContext(), this, "register", AppConstants.APP_API_URL).execute(params);
            }
        }catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onSuccess(JSONObject response, String key) {
        Log.i(TAG, "Response:" + response);
        try {
            if ("register".equalsIgnoreCase(key) && response.has("status") && response.getBoolean("status")) {
                Bundle acct = new Bundle();
                acct.putString("name", response.getString("name"));
                acct.putString("email", response.getString("email"));
                Intent homeIntent = new Intent(getContext(), HomeActivity.class);
                homeIntent.putExtra("app_account_info", acct);
                startActivity(homeIntent);
            } else {
                AppSettings.showAlert(getActivity().getSupportFragmentManager(), getContext(), getString(R.string.service_unavailable));
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "Error: " + message);
        AppSettings.showAlert(getActivity().getSupportFragmentManager(),getContext(), message);
    }
}
