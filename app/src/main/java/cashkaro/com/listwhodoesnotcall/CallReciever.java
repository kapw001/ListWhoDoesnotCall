package cashkaro.com.listwhodoesnotcall;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by yasar on 2/8/17.
 */

public class CallReciever extends BroadcastReceiver {
    private static String previousState = null;
    private ContentResolver mResolver;
    private SharedPreferences sharedPreferences;
    public static String message;
    private static String number;
    public static boolean doFillMessage = false;
    public Context context;

    private DatabaseHandler databaseHandler;


    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;

        databaseHandler = new DatabaseHandler(context);
        mResolver = context.getApplicationContext()
                .getContentResolver();
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (state == null) {
            number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            CallReciever.previousState = TelephonyManager.EXTRA_STATE_RINGING;
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(context, "Please give a read call log permissions ", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    Cursor managedCursor = mResolver.query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " = ? ",
                            new String[]{number}, CallLog.Calls.DATE + " DESC");

                    if (managedCursor.getCount() > 0) {
                        managedCursor.moveToFirst();
                        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                        int _NAME = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                        String phNumber = managedCursor.getString(number);
                        String callerName = managedCursor.getString(_NAME);


                        databaseHandler.addContact(new Contact(callerName, phNumber));


                        Log.e(TAG, "onReceive: Test    " + phNumber + "    " + callerName + "    ");


                    }
                }
            }, 1000);

        } else {
            CallReciever.previousState = null;
        }


    }


}
