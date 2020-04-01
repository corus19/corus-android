package com.jamdoli.corus.bluetooth.advertiser;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

public class BluetoothAdvertiserServiceCallback extends BluetoothGattServerCallback {

	private static final String TAG = "BluetoothAdverCallback";

	private BluetoothGattServer bluetoothGattServer;

	@Override
	public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
		if (newState == BluetoothProfile.STATE_CONNECTED) {
			Log.i(TAG, "BluetoothDevice CONNECTED: " + device);
		} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
			Log.i(TAG, "BluetoothDevice DISCONNECTED: " + device);
			// Remove device from any active subscriptions
		}
	}

	@Override
	public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
		BluetoothGattCharacteristic characteristic) {

		bluetoothGattServer
			.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, new byte[1]);
	}

	@Override
	public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
		BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded,
		int offset, byte[] value) {
	}

	@Override
	public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset,
		BluetoothGattDescriptor descriptor) {
	}

	@Override
	public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
		BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded,
		int offset, byte[] value) {
	}

	public void setBluetoothGattServer(BluetoothGattServer bluetoothGattServer) {
		this.bluetoothGattServer = bluetoothGattServer;
	}
}
