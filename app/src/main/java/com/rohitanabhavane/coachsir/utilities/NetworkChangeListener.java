package com.rohitanabhavane.coachsir.utilities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rohitanabhavane.coachsir.R;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Common.isConnectedToInternet(context)) { //Internet is not Connected
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View internetNotConnected_layout = LayoutInflater.from(context).inflate(R.layout.activity_no_connection, null);
            builder.setView(internetNotConnected_layout);

            Button btnInternetRetry = internetNotConnected_layout.findViewById(R.id.btnInternetRetry);

            //Show Dialog Box
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animationReviewDialogBox;
            dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.review_dialogbox));
            dialog.setCancelable(false);

            dialog.getWindow().setGravity(Gravity.CENTER);

            btnInternetRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onReceive(context, intent);
                }
            });
        }
    }
}
