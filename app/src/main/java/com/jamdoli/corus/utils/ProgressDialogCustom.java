package com.jamdoli.corus.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;
import com.jamdoli.corus.R;


public class ProgressDialogCustom {
    private Context context;
    private ProgressDialog progressDialog;
    private TextView uiTvProgressBatText;

    public ProgressDialogCustom(Context context) {
        this.context = context;
    }

    public void show() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if(!progressDialog.isShowing()) {
            progressDialog.show();
        }
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.custom_progressbar);

    }

    public void showWithMessage(String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.custom_progressbar);
        uiTvProgressBatText = progressDialog.findViewById(R.id.progressBarText);
        uiTvProgressBatText.setText(message);


    }

    public void dismiss() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
