package com.jamdoli.corus.nearbydevice;


import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import com.jamdoli.corus.R;
import com.jamdoli.corus.bluetooth.advertiser.BluetoothAdvertiserService;
import com.jamdoli.corus.bluetooth.receiver.BluetoothScanner;
import com.jamdoli.corus.bluetooth.receiver.DeviceScannerCallback;
import com.jamdoli.corus.gps.GpsHandler;
import com.jamdoli.corus.nearbydevice.datastore.NearByDeviceDao;
import com.jamdoli.corus.nearbydevice.datastore.NearByDeviceEntity;
import com.jamdoli.corus.utils.ApiClient;
import com.jamdoli.corus.utils.ApiInterface;
import com.jamdoli.corus.utils.AppSettingsUsingSharedPrefs;
import java.util.List;
import java.util.UUID;

public class NearByDeviceManager {

	private static final boolean DEFAULT_STREAM_IN_PROGRESS = false;
	private static final String STREAM_IN_PROGRESS_TAG = "STREAM_IN_PROGRESS_TAG";


	private static final long DEFAULT_BLUETOOTH_REPORT_DELAY_IN_MILLIS = 10000;
	private static final String BLUETOOTH_REPORT_DELAY_IN_MILLIS_TAG = "bluetoothReportDelayInMillis";

	private final GpsHandler gpsHandler;
	private final NearByDeviceDao nearByDeviceDao;
	private final BluetoothManager manager;
	private DeviceScannerCallback deviceScannerCallback;
	private final AppSettingsUsingSharedPrefs appSettingsUsingSharedPrefs;
	private final Context context;
	private final ApiInterface apiInterface;
	private BluetoothAdvertiserService bluetoothAdvertiserService;
	private final UUID bluetoothServiceUUID;

	public NearByDeviceManager(GpsHandler gpsHandler, NearByDeviceDao nearByDeviceDao,
		BluetoothManager manager, Context context) {
		this.gpsHandler = gpsHandler;
		this.nearByDeviceDao = nearByDeviceDao;
		this.manager = manager;
		this.context = context;
		this.apiInterface = ApiClient.getApiService();
		this.appSettingsUsingSharedPrefs = AppSettingsUsingSharedPrefs.getInstance();

		bluetoothServiceUUID = UUID.fromString(context.getString(R.string.bluetooth_service_uuid));
	}

	public void startScan() throws InterruptedException {

		String userBluetoothSignature = appSettingsUsingSharedPrefs.getBtAddress();

		long reportDelayInMillis = appSettingsUsingSharedPrefs.getLongValueForKey(
			BLUETOOTH_REPORT_DELAY_IN_MILLIS_TAG,
			DEFAULT_BLUETOOTH_REPORT_DELAY_IN_MILLIS);

		if (deviceScannerCallback != null) {
			BluetoothScanner.stopScan(deviceScannerCallback, manager.getAdapter());
		}

		bluetoothAdvertiserService = new BluetoothAdvertiserService(userBluetoothSignature,
			bluetoothServiceUUID);
		bluetoothAdvertiserService.startBluetoothAdvertisingService(manager, context);

		deviceScannerCallback = new DeviceScannerCallback(this, context, bluetoothServiceUUID);
		if (!BluetoothScanner.startScan(deviceScannerCallback, manager.getAdapter(),
			reportDelayInMillis, bluetoothServiceUUID)) {

			Thread.sleep(1000);
			startScan();
		}

		streamScannedDevicesData();
	}

	public void processScannedDevice(List<DeviceData> deviceData) {

		Log.d("nearby", "Device Data: " + deviceData);
		NearByDeviceEntity[] nearByDeviceEntities = new NearByDeviceEntity[deviceData.size()];

		Location location = gpsHandler.getLastLocation();

		for (int i = 0; i < deviceData.size(); i++) {
			nearByDeviceEntities[i] = buildNearByDeviceEntity(deviceData.get(i), location);
			Log.d("nearby", nearByDeviceEntities.toString());
		}

		nearByDeviceDao.insertAll(nearByDeviceEntities);
		streamScannedDevicesData();
	}

	public void stopScan() {
		BluetoothScanner.stopScan(deviceScannerCallback, manager.getAdapter());
	}

	public void streamScannedDevicesData() {

		synchronized (this) {
			boolean streamInProgress = appSettingsUsingSharedPrefs
				.getBooleanValueForKey(STREAM_IN_PROGRESS_TAG, DEFAULT_STREAM_IN_PROGRESS);

			if (streamInProgress) {
				return;
			}

			NearByDeviceStreamer nearByDeviceStreamer = NearByDeviceStreamer.builder()
				.apiClient(apiInterface)
				.nearByDeviceDao(nearByDeviceDao)
				.appSettingsUsingSharedPrefs(appSettingsUsingSharedPrefs)
				.build();

			nearByDeviceStreamer.execute(0);
			appSettingsUsingSharedPrefs.setBooleanValueForKey(STREAM_IN_PROGRESS_TAG, true);

		}
	}

	public void scannerStartFailed() {

		try {
			startScan();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private NearByDeviceEntity buildNearByDeviceEntity(DeviceData deviceData, Location location) {
		return NearByDeviceEntity.builder()
			.bluetoothSignature(deviceData.getBluetoothSignature())
			.name(deviceData.getName())
			.rssi(deviceData.getRssi())
			.txPower(deviceData.getTxPower())
			.timestamp(deviceData.getTimestamp())
			.lat(location.getLatitude())
			.lng(location.getLongitude())
			.build();
	}

}
