package com.jamdoli.corus.bluetooth.receiver;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.util.Log;
import com.jamdoli.corus.nearbydevice.DeviceData;
import com.jamdoli.corus.nearbydevice.NearByDeviceManager;
import com.jamdoli.corus.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BluetoothGattReceiverCallback extends BluetoothGattCallback {

	private static final String TAG = "BluetoothGattReceiver";

	private final NearByDeviceManager nearByDeviceManager;
	private BluetoothGatt bluetoothGatt;
	private final ScanResult scanResult;

	public BluetoothGattReceiverCallback(
		NearByDeviceManager nearByDeviceManager,
		ScanResult scanResult) {
		this.nearByDeviceManager = nearByDeviceManager;
		this.scanResult = scanResult;
	}

	@Override
	public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
		if (newState == BluetoothProfile.STATE_CONNECTED) {
			Log.i(TAG, "Connected to GATT client. Attempting to start service discovery");
			gatt.discoverServices();
		} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
			Log.i(TAG, "Disconnected from GATT client");
		}
	}

	@Override
	public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		if (status == BluetoothGatt.GATT_SUCCESS) {

			BluetoothGattService service = gatt.getService(Constants.BLUETOOTH_SERVICE_UUID);
			if (service != null) {
				List<BluetoothGattCharacteristic> characteristicList = service
					.getCharacteristics();
				if (characteristicList != null) {
					List<DeviceData> deviceDataList = new ArrayList<>(characteristicList.size());
					for (BluetoothGattCharacteristic characteristic : characteristicList) {
						deviceDataList.add(getDeviceData(scanResult,
							characteristic.getUuid().toString()));
					}

					nearByDeviceManager.processScannedDevice(deviceDataList);
				}
			} else {
				Log.w(TAG, "Gatt service cannot be received: " + status);
			}
		}

		if (bluetoothGatt != null) {
			bluetoothGatt.close();
		}
	}

	public static DeviceData getDeviceData(ScanResult scanResult, String uuid) {

		DeviceData.DeviceDataBuilder deviceDataBuilder = DeviceData.builder()
			.bluetoothSignature(scanResult.getDevice().getAddress())
			.name(scanResult.getDevice().getName())
			.rssi(scanResult.getRssi())
			.timestamp(TimeUnit.MILLISECONDS
				.convert(scanResult.getTimestampNanos(), TimeUnit.NANOSECONDS));

		if (Build.VERSION.SDK_INT >= 26) {
			deviceDataBuilder.txPower(scanResult.getTxPower());
		} else {
			deviceDataBuilder.txPower(scanResult.getScanRecord().getTxPowerLevel());
		}

		return deviceDataBuilder.build();
	}

	public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
		this.bluetoothGatt = bluetoothGatt;
	}
}
