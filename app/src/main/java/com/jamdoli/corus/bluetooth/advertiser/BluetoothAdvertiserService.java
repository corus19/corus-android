package com.jamdoli.corus.bluetooth.advertiser;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import com.jamdoli.corus.utils.Constants;
import java.util.UUID;

public class BluetoothAdvertiserService {

	private static final String TAG = BluetoothAdvertiserService.class.getSimpleName();

	private BluetoothGattServer bluetoothGattServer;
	private BluetoothAdvertiserServiceCallback bluetoothAdvertiserServiceCallback;
	private final UUID userBluetoothSignature;

	public BluetoothAdvertiserService(String userBluetoothSignature) {
		this.userBluetoothSignature = UUID.fromString(userBluetoothSignature);
	}


	public void startBluetoothAdvertisingService(BluetoothManager bluetoothManager,
		Context context) {

		startAdvertising(bluetoothManager.getAdapter());

		bluetoothAdvertiserServiceCallback = new BluetoothAdvertiserServiceCallback();
		bluetoothGattServer = bluetoothManager.openGattServer(context,
			bluetoothAdvertiserServiceCallback);
		if (bluetoothGattServer == null) {
			Log.e(TAG, "Unable to create GATT server");

			throw new IllegalStateException("Unable to create GATT server");
		}

		bluetoothAdvertiserServiceCallback.setBluetoothGattServer(bluetoothGattServer);
		bluetoothGattServer.addService(createUserBluetoothService());

	}

	private void startAdvertising(BluetoothAdapter bluetoothAdapter) {

		BluetoothLeAdvertiser mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
		if (mBluetoothLeAdvertiser == null) {
			Log.w(TAG, "Failed to create advertiser");
			return;
		}


		AdvertiseSettings settings = new AdvertiseSettings.Builder()
			.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
			.setConnectable(true)
			.setTimeout(0)
			.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW)
			.build();

		ParcelUuid parcelUuid = new ParcelUuid(Constants.BLUETOOTH_SERVICE_UUID);
		AdvertiseData data = new AdvertiseData.Builder()
			.setIncludeTxPowerLevel(true)
			.setIncludeDeviceName(false)
			.addServiceUuid(parcelUuid)
			.build();

		mBluetoothLeAdvertiser
			.startAdvertising(settings, data, mAdvertiseCallback);
	}


	private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
		@Override
		public void onStartSuccess(AdvertiseSettings settingsInEffect) {
			Log.i(TAG, "LE Advertise Started.");
		}

		@Override
		public void onStartFailure(int errorCode) {
			Log.w(TAG, "LE Advertise Failed: " + errorCode);
		}
	};

	private BluetoothGattService createUserBluetoothService() {
		BluetoothGattService service = new BluetoothGattService(Constants.BLUETOOTH_SERVICE_UUID,
			BluetoothGattService.SERVICE_TYPE_PRIMARY);

		// Counter characteristic (read-only, supports notifications)
		BluetoothGattCharacteristic userCharacteristic = new BluetoothGattCharacteristic(
			userBluetoothSignature,
			BluetoothGattCharacteristic.PROPERTY_READ
				| BluetoothGattCharacteristic.PROPERTY_NOTIFY,
			BluetoothGattCharacteristic.PERMISSION_READ);

		service.addCharacteristic(userCharacteristic);

		return service;
	}
}
