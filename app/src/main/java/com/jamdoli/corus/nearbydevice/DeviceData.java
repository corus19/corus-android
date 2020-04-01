package com.jamdoli.corus.nearbydevice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class DeviceData {

	private String bluetoothSignature;
	private String name;
	private int txPower;
	private int rssi;
	private long timestamp;
}
