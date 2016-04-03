package br.com.psmorandi.cousera.dailyselfie;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class SelfieAlarmReceiver extends BroadcastReceiver {

	private static final int NOTIFICAITON_ID = 100001;

	private static final String TAG = "ALARM_RECEIVER";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received alarm.");
		createNotitification(context);
		Log.d(TAG, "User notified.");
	}

	private void createNotitification(Context context) {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(android.R.drawable.ic_menu_camera)
				.setContentTitle("Daily Selfie")
				.setContentText("You need to take a selfie right now!")
				.setAutoCancel(true);
		
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);

		if (vibrator.hasVibrator())
			builder.setVibrate(new long[] { 100, 1000 });

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, MainActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the started Activity. This ensures that navigating backward from the
		// Activity leads out of your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(NOTIFICAITON_ID, builder.build());
	}

}
