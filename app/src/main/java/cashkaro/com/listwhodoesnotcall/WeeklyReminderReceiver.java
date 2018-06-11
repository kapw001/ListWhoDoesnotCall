package cashkaro.com.listwhodoesnotcall;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by yasar on 11/8/17.
 */

public class WeeklyReminderReceiver extends BroadcastReceiver {

    private static final String TAG = "WeeklyReminderReceiver";

    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm Called", Toast.LENGTH_SHORT).show();

        Log.e(TAG, "onReceive:   Alarm Called ");

        addNotification(context);
    }


    private void addNotification(Context context) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Contact List")
                        .setContentText("This is a list of unwanted contact list").setAutoCancel(true);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        notificationIntent.putExtra("clearDatabase", true);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(999, builder.build());
    }
}