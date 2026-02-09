package com.csl.cslibrary4a;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

public class ScanData {
    public BluetoothDevice device; public String name, address;
    public int rssi;
    public byte[] scanRecord;
    public ArrayList<byte[]> decoded_scanRecord;
    public int serviceUUID;
    public boolean hasServicePower;

    public ScanData(BluetoothDevice device, String name, String address, int rssi, byte[] scanRecord, ArrayList<byte[]> decoded_scanRecord, int serviceUUID, boolean hasServicePower) {
        this.device = device;
        this.name = name;
        this.address = address;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
        this.decoded_scanRecord = decoded_scanRecord;
        this.serviceUUID = serviceUUID;
        this.hasServicePower = hasServicePower;
    }
}
