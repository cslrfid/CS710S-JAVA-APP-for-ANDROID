package com.csl.cslibrary4a;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

public class ScanData {
	public BluetoothDevice device; public String name, address;
	public byte[] scanRecord;
	public int rssi;
	public ArrayList<byte[]> decoded_scanRecord;
	public int serviceUUID;
	public boolean hasServicePower;

	public ScanData(BluetoothDevice device, int rssi, byte[] scanRecord) {
		this.device = device;
		this.rssi = rssi;
		this.scanRecord = scanRecord;
		decoded_scanRecord = new ArrayList<byte[]>();
	}
	ScanData(String name, String address, int rssi, byte[] scanRecord) {
		this.device = device; this.name = name; this.address = address;
		this.rssi = rssi;
		this.scanRecord = scanRecord;
	}
}
