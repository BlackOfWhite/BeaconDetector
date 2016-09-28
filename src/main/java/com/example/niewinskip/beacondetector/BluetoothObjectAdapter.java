package com.example.niewinskip.beacondetector;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by niewinskip on 2016-06-23.
 */
public class BluetoothObjectAdapter extends ArrayAdapter<BluetoothObject>{
        public BluetoothObjectAdapter(Context context, List<BluetoothObject> bObjects) {
            super(context, 0, bObjects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            BluetoothObject bo = getItem(position);
            final  BluetoothDevice bd = bo.getBluetoothDevice();
            int RSSI = bo.getRSSI();
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.bluetooth_object, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            TextView tvRSSI= (TextView) convertView.findViewById(R.id.tvRSSI);
            TextView tvUUIDs = (TextView) convertView.findViewById(R.id.tvUUIDS);
            TextView tvType = (TextView) convertView.findViewById(R.id.tvType);
            TextView tvState = (TextView) convertView.findViewById(R.id.tvState);
            final Switch switchConnect = (Switch) convertView.findViewById(R.id.switchConnect);
            // Populate the data into the template view using the data object
            String name = bd.getName();
            String address = bd.getAddress();
            ParcelUuid[] UUIDs = bd.getUuids();
            int type = bd.getType();
            int state = bd.getBondState();
            if (name == null || name.isEmpty()) {
                name = "Unknown";
            } else if (address == null || address.isEmpty()) {
                address = "Unknown";
            }
            tvName.setText("Name: " + name);
            tvAddress.setText("Address: " + address);
            tvRSSI.setText("RSSI: " + RSSI);
            tvUUIDs.setText("UUIDs: " + UUIDs);
            tvState.setText("State: " + state);
            tvType.setText("Type: " + type);
            // Return the completed view to render on screen
            switchConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (switchConnect.isEnabled()) {
                        connect(bd);
                    } else {

                    }
                }
            });
            return convertView;
        }

    private Boolean connect(BluetoothDevice bdDevice) {
        Boolean bool = false;
        try {
            Log.i("Log", "service method is called ");
            Class cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class[] par = {};
            Method method = cl.getMethod("createBond", par);
            Object[] args = {};
            bool = (Boolean) method.invoke(bdDevice);//, args);// this invoke creates the detected devices paired.
            //Log.i("Log", "This is: "+bool.booleanValue());
            //Log.i("Log", "devicesss: "+bdDevice.getName());
        } catch (Exception e) {
            Log.i("Log", "Inside catch of serviceFromDevice Method");
            e.printStackTrace();
        }
        return bool.booleanValue();
    };
}

