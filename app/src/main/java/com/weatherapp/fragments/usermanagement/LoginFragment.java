package com.weatherapp.fragments.usermanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.weatherapp.HomeActivity;
import com.weatherapp.R;
import com.weatherapp.common.AppConstants;
import com.weatherapp.common.AppSettings;
import com.weatherapp.http.AppLoader;
import com.weatherapp.http.AppWebservices;

import org.json.JSONObject;

/**
 * Login Fragment
 */
public class LoginFragment extends Fragment implements TextView.OnEditorActionListener,
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        AppWebservices.AppWebServiceCallBack {

    // Constants & variables
    private static final String TAG = "LoginFragment";

    // UI references.
    private AutoCompleteTextView editTxtEmail;
    private EditText editTxtPassword;
    private View scrollViewLoginForm;
    private AppCompatButton btnAppSignin;
    private SignInButton btnGoogleSignin;

    private AppLoader appLoader;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleSignIn();
        appLoader = new AppLoader(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        // Set up the login form.
        scrollViewLoginForm = view.findViewById(R.id.register_form);
        editTxtEmail = (AutoCompleteTextView)  view.findViewById(R.id.email);
        editTxtPassword = (EditText)  view.findViewById(R.id.password);
        editTxtPassword.setOnEditorActionListener(this);

        btnGoogleSignin = (SignInButton) view. findViewById(R.id.btn_google_signin);
        btnGoogleSignin.setOnClickListener(this);

        btnAppSignin = (AppCompatButton)  view.findViewById(R.id.btn_app_singin);
        btnAppSignin.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Create GoogleAPI Client
     */
    private void initGoogleSignIn() {
        try {
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestId()
                    .requestProfile()
                    .build();
            AppConstants.googleApiClient = new GoogleApiClient.Builder(getContext())
                    .enableAutoManage(getActivity(),this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                    .build();

        }catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(AppConstants.googleApiClient);
        if (opr.isDone()) { // Already signin
            GoogleSignInResult result = opr.get();
            googleSignInSuccess(result);
        }
    }

    /**
     * Login using gmail account
     */
    private void login() {
        try {
            // Reset errors.
            editTxtEmail.setError(null);
            editTxtPassword.setError(null);

            // Store values at the time of the login attempt.
            String email = editTxtEmail.getText().toString();
            String password = editTxtPassword.getText().toString();

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
            }

            if (cancel) {
                focusView.requestFocus();
            } else {
                new AppWebservices(getActivity(), getContext(), this, "login",AppConstants.APP_API_URL).execute("{\"email\" : "+ email +",\"password\" : "+ password + "}");
            }
        }catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.SIGN_IN_SUCCESS: {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                googleSignInSuccess(result);
            }
            break;
        }
    }

    private void googleSignInSuccess(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                Intent homeIntent = new Intent(getContext(), HomeActivity.class);
                homeIntent.putExtra("google_account_info", acct);
                startActivity(homeIntent);
            }
        } else {
            Log.i(TAG, "Result: " + result.getStatus().getStatusMessage());
            if (result.getStatus().getStatusMessage() == null) {
                AppSettings.showAlert(getActivity().getSupportFragmentManager(), getContext(), result.getStatus().getStatusMessage());
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            login();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google_signin: {
                if (AppConstants.googleApiClient != null) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(AppConstants.googleApiClient);
                    startActivityForResult(signInIntent, AppConstants.SIGN_IN_SUCCESS);
                }
            }  break;
            case R.id.btn_app_singin: {
                login();
            }
            break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, connectionResult.getErrorMessage());
        AppSettings.showAlert(getActivity().getSupportFragmentManager(),getContext(), connectionResult.getErrorMessage());
    }

    @Override
    public void onSuccess(JSONObject response, String key) {
        Log.i(TAG, "Response:" + response);
        try {
            if ("login".equalsIgnoreCase(key)) {
                if (response.has("status") && response.getBoolean("status")) {
                    Bundle acct = new Bundle();
                    acct.putString("name", response.getString("name"));
                    acct.putString("email", response.getString("email"));
                    Intent homeIntent = new Intent(getContext(), HomeActivity.class);
                    homeIntent.putExtra("app_account_info", acct);
                    startActivity(homeIntent);
                }else {

                }
            }else {
                AppSettings.showAlert(getActivity().getSupportFragmentManager(),getContext(), getString(R.string.service_unavailable));
            }
        }catch (Exception e){
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "Error: " + message);
        AppSettings.showAlert(getActivity().getSupportFragmentManager(),getContext(), message);
    }

}
