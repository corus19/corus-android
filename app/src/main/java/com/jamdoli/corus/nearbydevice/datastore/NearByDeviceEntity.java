package com.jamdoli.corus.nearbydevice.datastore;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity(tableName = "near_by_device_entity",
        indices = {@Index(value = "timestamp")})
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NearByDeviceEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "bluetooth_signature")
    private String bluetoothSignature;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "tx_power")
    private int txPower;

    @ColumnInfo(name = "rssi")
    private int rssi;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    @ColumnInfo(name = "lat")
    private double lat;

    @ColumnInfo(name = "lng")
    private double lng;
}
