package com.example.niewinskip.beacondetector;

import android.bluetooth.BluetoothDevice;

/**
 * Created by niewinskip on 2016-06-19.
 */
public class BluetoothObject {

    private BluetoothDevice bd;
    private String address;

    public BluetoothObject(BluetoothDevice bd, int RSSI) {
        this.bd = bd;
        this.RSSI = RSSI;
        this.address = bd.getAddress();
    }

//    private String name;
//
//    public int getState() {
//        return state;
//    }
//
//    public void setState(int state) {
//        this.state = state;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    public ParcelUuid[] getUuids() {
//        return uuids;
//    }
//
//    public void setUuids(ParcelUuid[] uuids) {
//        this.uuids = uuids;
//    }

    public int getRSSI() {
        return RSSI;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bd;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

//    private int state;
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    private String address;
//    private int type;
//    private ParcelUuid[] uuids;
    private int RSSI;

    @Override
    public boolean equals(Object o) {
        if ((o instanceof BluetoothObject) && ((BluetoothObject) o).getBluetoothDevice().getAddress().equals(this.getBluetoothDevice().getAddress())) {
            return true;
        } else if ((o instanceof BluetoothDevice) && ((BluetoothDevice) o).getAddress().equals(this.getBluetoothDevice().getAddress())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (this.address == null) {
            return 0;
        }
        return hash + address.hashCode() * hash;
    }
}
