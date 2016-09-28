package com.example.niewinskip.beacondetector;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<BluetoothObject> mDeviceList;
    private List<BluetoothObject> tempList;
    private BluetoothAdapter mBluetoothAdapter;
    private FloatingActionButton fab;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG = "MyTag";
    private BluetoothObjectAdapter adapter;
    private Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mDeviceList = new ArrayList<>();
        tempList = new ArrayList<>();

        switch1 = (Switch) findViewById(R.id.switch1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempList = new ArrayList<BluetoothObject>();
                mBluetoothAdapter.startDiscovery();
                Toast.makeText(getBaseContext(), "Searching for new devices..", Toast.LENGTH_SHORT).show();
            }
        });
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch1.isEnabled()) {
                    setBluetoothOn();
                } else {
                    setBluetoothOff();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        mBluetoothAdapter.cancelDiscovery();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(TAG, "Start");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "Finished");
                adapter = new BluetoothObjectAdapter(getApplicationContext(), mDeviceList);
                listView.setAdapter(adapter);
//                for (BluetoothObject bo : tempList) {
//                    if (!mDeviceList.contains(bo)) {
//                        mDeviceList.add(bo);
//                        Log.d(TAG, "adddded");
//                    } else {
//                        Log.d(TAG, "notadded");
//                    }
//                }
                mDeviceList = tempList;
                Toast.makeText(getBaseContext(), "Finished", Toast.LENGTH_SHORT).show();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d(TAG, "Device found");

                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothObject bo = new BluetoothObject(device, intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
                tempList.add(bo);
                Log.d(TAG, device.getName() + "\n" + device.getAddress() + "\n" + bo.getRSSI() + "\n" + device.getBondState() + "\n" + device.getType() + "\n" + device.getUuids());
//                showToast("Found device " + device.getName());
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("ELO", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }

    private void setBluetoothOn() {
        mBluetoothAdapter.enable();
        Log.d(TAG, "Adapter enabled");
    }

    private void setBluetoothOff() {
        mBluetoothAdapter.disable();
        Log.d(TAG, "Adapter disable");
    }
}
