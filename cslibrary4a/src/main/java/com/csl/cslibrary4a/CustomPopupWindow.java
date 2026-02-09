package com.csl.cslibrary4a;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class CustomPopupWindow {
    Context context;
    Handler handler = new Handler();
    Runnable blinkRunnable;
    View popupView;

    public CustomPopupWindow(Context context) {
        this.context = context;
    }

    public PopupWindow popupWindow;
    boolean wait = false, blink = false;
    public void setdata(boolean wait, boolean blink) {
        this.wait = wait;
        this.blink = blink;
    }
    public void popupStart(String message) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        TextView textViewDismiss = (TextView)popupView.findViewById(R.id.dismissMessage);
        appendToLog("CustomPopupWindow.popupStart: wait = " + wait + ", message = " + message);
        textViewDismiss.setText(message);
        Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
        if (wait) btnDismiss.setVisibility(GONE);
        else {
            btnDismiss.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }
        if (blink) startBlinking(popupView);
    }
    private boolean isBlinkOn = true;
    private void startBlinking(View popupView) {
        blinkRunnable = new Runnable() {
            @Override
            public void run() {
                if (popupWindow != null && popupWindow.isShowing()) {
                    // Toggle background color to simulate blinking
                    appendToLog("CustomPopupWindow.startBlinking.blinkRunnable.run: isBlinkOn is " + isBlinkOn);
                    if (isBlinkOn) {
                        popupView.setVisibility(INVISIBLE);
                        //popupView.setBackgroundColor(Color.RED);
                    } else {
                        popupView.setVisibility(VISIBLE);
                        //popupView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    isBlinkOn = !isBlinkOn;
                    handler.postDelayed(this, (isBlinkOn ? 900 : 100)); // blink every 0.5 second
                }
            }
        };
        handler.post(blinkRunnable);
    }
    public void appendToLog(String s) {
        Log.i ("Hello", s);
    }
}
