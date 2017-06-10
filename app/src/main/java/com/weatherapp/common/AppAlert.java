package com.weatherapp.common;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.weatherapp.R;

/**
 * Created by NirajKumar on 6/10/17.
 */

public class AppAlert extends BottomSheetDialogFragment {

    private AppCompatTextView txtAlertTitle, txtAlertMessage;
    private AppCompatButton btnAlertOk;

    private String title = "", message = "";

    public AppAlert(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("title");
        message = getArguments().getString("message");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);;
        dialog.setContentView(R.layout.layout_alert_with_one_button);

        txtAlertTitle = (AppCompatTextView) dialog.findViewById(R.id.txt_alert_title);
        txtAlertTitle.setText(title);
        txtAlertMessage = (AppCompatTextView) dialog.findViewById(R.id.txt_alert_message);
        txtAlertMessage.setText(message);

        btnAlertOk = (AppCompatButton) dialog.findViewById(R.id.btn_alert_ok);
        btnAlertOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return dialog;
    }

}
