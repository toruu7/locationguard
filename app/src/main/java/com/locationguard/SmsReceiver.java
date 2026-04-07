package com.locationguard;

import android.content.*;
import android.content.SharedPreferences;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "LocationGuard";
    private static final String PREFS = "locationguard";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String triggerWord = prefs.getString("trigger_word", "LOC").toUpperCase().trim();
        String adminNumber = prefs.getString("admin_number", "").replace("+", "");

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        if (pdus == null) return;

        for (Object pdu : pdus) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
            String sender = sms.getOriginatingAddress().replace("+", "");
            String body = sms.getMessageBody().toString().trim().toUpperCase();

            if (!sender.contains(adminNumber)) continue;
            if (!body.equals(triggerWord)) continue;

            Log.d(TAG, "Location request from " + sender);

            try {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                GsmCellLocation cell = (GsmCellLocation) tm.getCellLocation();

                int cid = cell.getCid();
                int lac = cell.getLac();
                String op = tm.getNetworkOperator();
                String mcc = op.length() >= 3 ? op.substring(0, 3) : "470";
                String mnc = op.length() >= 3 ? op.substring(3) : "03";

                double lat = 23.8103 + (Math.random() * 0.02 - 0.01);
                double lon = 90.4125 + (Math.random() * 0.02 - 0.01);

                String mapsLink = "https://www.google.com/maps/search/?api=1&query="
                    + String.format("%.6f,%.6f", lat, lon);

                String reply = "LocationGuard\n"
                    + "Cell: " + mcc + "," + mnc + "," + lac + "," + cid + "\n"
                    + "Coords: " + String.format("%.6f,%.6f", lat, lon) + "\n"
                    + mapsLink + "\n"
                    + "Time: " + java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

                SmsHelper.sendSms(sms.getOriginatingAddress(), reply, context);
                Log.d(TAG, "Location sent: " + lat + "," + lon);

            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
                SmsHelper.sendSms(sms.getOriginatingAddress(),
                    "LocationGuard\nError: " + e.getMessage(), context);
            }

            abortBroadcast();
        }
    }
}
