package com.locationguard;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

public class SmsHelper {
    private static final String TAG = "LocationGuard";

    public static void sendSms(String destination, String message, Context context) {
        try {
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(destination, null, message, null, null);
            Log.d(TAG, "SMS sent to " + destination);
        } catch (Exception e) {
            Log.e(TAG, "SMS failed: " + e.getMessage());
        }
    }
}
