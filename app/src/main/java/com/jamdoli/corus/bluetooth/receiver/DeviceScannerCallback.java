package com.jamdoli.corus.bluetooth.receiver;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import com.jamdoli.corus.nearbydevice.DeviceData;
import com.jamdoli.corus.nearbydevice.NearByDeviceManager;
import com.jamdoli.corus.utils.AppHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeviceScannerCallback extends ScanCallback {

	private static final String TAG = "DeviceScannerCallback";
	private final UUID bluetoothServiceUUID;
	private final ParcelUuid serviceParcelUUID;

	private final NearByDeviceManager nearByDeviceManager;
	private final Context context;

	public DeviceScannerCallback(NearByDeviceManager nearByDeviceManager,
		Context context, UUID bluetoothServiceUUID) {
		this.nearByDeviceManager = nearByDeviceManager;
		this.context = context;
		this.serviceParcelUUID = new ParcelUuid(bluetoothServiceUUID);
		this.bluetoothServiceUUID = bluetoothServiceUUID;
	}

	@Override
	public void onScanResult(int callbackType, ScanResult result) {

		Log.d("DeviceScannerCallback", " Scan Results: " + result);
		startGattService(result);
	}

	/**
	 * Callback when batch results are delivered.
	 *
	 * @param results List of scan results that are previously scanned.
	 */
	@Override
	public void onBatchScanResults(List<ScanResult> results) {

		Log.d("DeviceScannerCallback", " Scan Results: " + results);
		List<DeviceData> deviceDataList = new ArrayList<>();
		for (ScanResult result : results) {

			UUID userBluetoothSignature = getUserBluetoothSignatureFromService(
				result.getScanRecord());
			if (userBluetoothSignature == null) {
				deviceDataList.add(BluetoothGattReceiverCallback
					.getDeviceData(result, userBluetoothSignature.toString()));
			} else {
				startGattService(result);
			}
		}

		nearByDeviceManager.processScannedDevice(deviceDataList);
	}

	/**
	 * Callback when scan could not be started.
	 *
	 * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
	 */
	@Override
	public void onScanFailed(int errorCode) {

		nearByDeviceManager.scannerStartFailed();
	}

	private UUID getUserBluetoothSignatureFromService(ScanRecord scanRecord) {

		Map<ParcelUuid, byte[]> serviceDataMap = scanRecord.getServiceData();

		byte[] serviceData = serviceDataMap.get(serviceParcelUUID);

		if (serviceData == null || serviceData.length != 12) {
			return null;
		}
		return AppHelper.getUUIDFromBytes(serviceData);

	}

	private void startGattService(ScanResult scanResult) {

		BluetoothDevice bluetoothDevice = scanResult.getDevice();
		BluetoothGattReceiverCallback bluetoothGattReceiverCallback = new BluetoothGattReceiverCallback(
			nearByDeviceManager, scanResult, bluetoothServiceUUID);
		BluetoothGatt bluetoothGatt = bluetoothDevice
			.connectGatt(context, true, bluetoothGattReceiverCallback);

		if (bluetoothGatt == null) {
			Log.w(TAG, "Unable to create GATT client");
			return;
		}

		bluetoothGattReceiverCallback.setBluetoothGatt(bluetoothGatt);

		Log.w(TAG, "Connect status: " + bluetoothGatt.connect());

	}

}
