package com.jamdoli.corus.bluetooth.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.Collections;
import java.util.UUID;

public class BluetoothScanner {

	private static final String TAG = "BluetoothScanner";

	public static boolean startScan(final ScanCallback callback, BluetoothAdapter adapter,
		long reportDelayInMillis, UUID bluetoothServiceUUID) {

		if (adapter == null) {
			Log.e(TAG, "BluetoothAdapter is null");
			return false;
		}

		if (!adapter.isEnabled()) {
			adapter.enable();
		}

		if (!adapter.isDiscovering()) {
			adapter.startDiscovery();
		}

		BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
		if (scanner == null) {
			Log.e(TAG, "BluetoothLeScanner is null");
			return false;
		}
		ScanFilter scanFilter = new ScanFilter.Builder()
			.setServiceUuid(new ParcelUuid(bluetoothServiceUUID))
			.build();

		ScanSettings scanSettings = new ScanSettings.Builder()
			.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
			.setReportDelay(reportDelayInMillis)
			.build();

		scanner.startScan(Collections.singletonList(scanFilter), scanSettings, callback);
		return true;
	}

	public static void stopScan(ScanCallback scanCallback, BluetoothAdapter adapter) {

		if (scanCallback != null) {
			BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
			scanner.stopScan(scanCallback);
		}
	}

}
