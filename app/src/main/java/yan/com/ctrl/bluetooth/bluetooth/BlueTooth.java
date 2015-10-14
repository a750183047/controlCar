package yan.com.ctrl.bluetooth.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Set;

import yan.com.ctrl.R;

/**
 * 蓝牙控制类
 * Created by yan on 2015/10/12.
 */
public class BlueTooth extends Activity {

    private BluetoothAdapter bluetoothAdapter;
    private Button bluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bluetooth_layout);

        bluetooth = (Button) findViewById(R.id.bluetooth_but);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

        if (pairedDevice.size() >0){
            for (BluetoothDevice device : pairedDevice){
                Log.e("bluetooth",device.getName()+":"+device.getAddress());
            }
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver,filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressBarVisibility(true);
                setTitle("正在扫描");
                Log.e("bluetooth","进来了");
                if (bluetoothAdapter.isDiscovering()){
                    bluetoothAdapter.cancelDiscovery();
                }
                bluetoothAdapter.startDiscovery();
            }
        });
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    Log.e("bluetooth",device.getName()+":"+device.getAddress()+"\n");
                }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    setProgressBarVisibility(false);
                    setTitle("已搜索完成");
                }
            }
        }
    };
}
