package com.jamdoli.corus.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DeviceRequest {

    private String bluetoothSignature;
    private String name;
    private int txPower;
    private int rssi;
    private long timestamp;
    private double lat;
    private double lng;
}
