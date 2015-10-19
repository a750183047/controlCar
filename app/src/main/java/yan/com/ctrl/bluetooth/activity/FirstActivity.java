package yan.com.ctrl.bluetooth.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import yan.com.ctrl.R;
import yan.com.ctrl.bluetooth.bluetooth.BluetoothComm;
import yan.com.ctrl.bluetooth.bluetooth.ScanDeviceActivity;

/**
 * 开始第一屏提示信息
 * Created by yan on 2015/10/16.
 */
public class FirstActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

        AlertDialog.Builder dialog = new AlertDialog.Builder(FirstActivity.this);
        dialog.setTitle("警    告");
        dialog.setMessage("请先连接上设备后再开启app");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(FirstActivity.this, ScanDeviceActivity.class);
                Intent intent1 = new Intent(FirstActivity.this, BluetoothComm.class);
                startActivity(intent1);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();


    }



    @Override
    public void onClick(View v) {

    }
}
