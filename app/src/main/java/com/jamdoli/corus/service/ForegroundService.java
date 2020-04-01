package com.jamdoli.corus.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.jamdoli.corus.R;
import com.jamdoli.corus.activities.HomeActivity;
import com.jamdoli.corus.gps.GpsHandler;
import com.jamdoli.corus.nearbydevice.NearByDeviceManager;
import com.jamdoli.corus.nearbydevice.datastore.CoronaTracerDatabase;
import com.jamdoli.corus.nearbydevice.datastore.NearByDeviceDao;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ForegroundService extends NonStopIntentService {

	public static final String CHANNEL_ID = "ForegroundServiceChannel";
	public static final String TAG = "ForegroundService";

	private static GpsHandler gpsHandler = null;
	private static NearByDeviceManager nearByDeviceManager = null;
	private static NearByDeviceDao nearByDeviceDao = null;
	private static BluetoothManager bluetoothManager = null;

	private static ForegroundService foregroundService = null;

	public static final long NOTIFY_INTERVAL = 50 * 1000; //in millis
	private Handler mHandler = new Handler();
	private Timer mTimer = null;

	public ForegroundService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		foregroundService = this;
		initiatePeriodicScanning();
//        initializeBluetoothScanner();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String input = intent.getStringExtra("inputExtra");
		createNotificationChannel();
		Intent notificationIntent = new Intent(this, HomeActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this,
			0, notificationIntent, 0);
		Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
			.setContentTitle("Foreground Service")
			.setSmallIcon(R.drawable.logo)
			.setContentText(input)
			.setContentIntent(pendingIntent)
			.build();

		startForeground(1, notification);

		return START_NOT_STICKY;
	}

	private void initiatePeriodicScanning() {

		initializeBluetoothObjects();

		if (mTimer != null) {
			mTimer.cancel();
		} else {
			// recreate new
			mTimer = new Timer();
		}
		// schedule task
		mTimer.scheduleAtFixedRate(new StartScannerTimerTask(), 0, NOTIFY_INTERVAL);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {

	}

	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel serviceChannel = new NotificationChannel(
				CHANNEL_ID,
				"Foreground Service Channel",
				NotificationManager.IMPORTANCE_DEFAULT
			);
			NotificationManager manager = getSystemService(NotificationManager.class);
			manager.createNotificationChannel(serviceChannel);
		}
	}

	private void initializeBluetoothScanner() {
		if (nearByDeviceManager != null) {
			nearByDeviceManager.stopScan();
		}

		initializeBluetoothObjects();

		Toast.makeText(getApplicationContext(), getDateTime(),
			Toast.LENGTH_SHORT).show();
		try {
			nearByDeviceManager.startScan();
		} catch (Exception e) {
			Log.e(TAG, "Scan interrupted due to exception", e);
		}
	}

	private String getDateTime() {
		// get date time in custom format
		SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
		return sdf.format(new Date());
	}

	class StartScannerTimerTask extends TimerTask {

		@Override
		public void run() {
			// run on another thread
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					foregroundService.initializeBluetoothScanner();
				}
			});
		}
	}

	private void initializeBluetoothObjects() {

		bluetoothManager = (BluetoothManager) foregroundService
			.getSystemService(Context.BLUETOOTH_SERVICE);
		gpsHandler = new GpsHandler(new FusedLocationProviderClient(foregroundService));
		nearByDeviceDao = CoronaTracerDatabase.getNearByDeviceDao(foregroundService);
		nearByDeviceManager = new NearByDeviceManager(gpsHandler, nearByDeviceDao,
			bluetoothManager, this);
	}
}