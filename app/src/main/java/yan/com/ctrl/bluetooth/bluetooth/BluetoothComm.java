package yan.com.ctrl.bluetooth.bluetooth;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import yan.com.ctrl.R;
import yan.com.ctrl.bluetooth.Data.Data;
import yan.com.ctrl.bluetooth.rocker.Rocker;

public class BluetoothComm extends FragmentActivity {
	// Debugging
	private static final String TAG = "BluetoothComm";
	private static final boolean D = true;
	// 请求开启蓝牙的requestCode
	static final int REQUEST_ENABLE_BT = 1;
	// 请求连接蓝牙的requestCode
	static final int REQUEST_CONNECT_DEVICE = 2;
	// bluetoothCommService 传来的消息状态
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	private static boolean isLineOk = true;
	private boolean isBtLineOk = false; //用来判断蓝牙是否连接上了

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	// 蓝牙设备
	private BluetoothDevice device = null;

	private EditText txEdit;
	private EditText rxEdit;
	private EditText inputEdit;

	private Switch switchBt;
	//连接的设备
	private TextView connectDevices;
	// 发送按键
	private Button sendButton;
	// 清空接收记录按键
	private Button clearRxButton;
	// 清空发送记录按键
	private Button clearTxButton;
	// 断开连接按键
	private Button disconnectButton;
	private Button clearAll;
	// 本地蓝牙适配器
	private BluetoothAdapter bluetooth;
	// 创建一个蓝牙串口服务对象
	private BluetoothCommService mCommService = null;

	private StringBuffer mOutStringBuffer = new StringBuffer("");

	private String mConnectedDeviceName = null;
	

	private Button button;
	private Button btn_send;
	private TextView receive_message;

	private Rocker rocker;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.e("BluetoothComm", "-ON CREATE-");
		//setContentView(R.layout.main);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 获取控件
		connectDevices = (TextView) findViewById(R.id.connected_device);
		button = (Button) findViewById(R.id.btn_sou);
		btn_send = (Button) findViewById(R.id.btn_send);
		switchBt = (Switch) findViewById(R.id.switch_bt);
		receive_message = (TextView) findViewById(R.id.receive_message);
		// 获得本地蓝牙设备
		bluetooth = BluetoothAdapter.getDefaultAdapter();
		showWarning();
		//获取摇杆对象
		rocker  = (Rocker) findViewById(R.id.rudder);

		Bitmap rocker_bg = BitmapFactory.decodeResource(getResources(), R.drawable.rocker_bg1);
		Bitmap rocker_ctrl = BitmapFactory.decodeResource(getResources(),R.drawable.rocker_ctrl);
		rocker.setRockerBackground(rocker_bg);
		rocker.setRockerCtrl(rocker_ctrl);

		rocker.setRudderListener(new Rocker.RudderListener() {
			@Override
			public void onSteeringWheelChanged(int action, int angle) {
				if (action == Rocker.ACTION_RUDDER) {
					//status.setText("角度：" + angle);
					if (isBtLineOk) {
						String angles = String.valueOf(angle);
						sendMessage(angles + "\r\n");
					}

				}
			}
		});


		switchBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isBtLineOk = true;
				} else {
					isBtLineOk = false;
				}
			}
		});

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("Blue", "点击了搜索");
				Intent serverIntent = new Intent(BluetoothComm.this, ScanDeviceActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			}
		});
		btn_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage("测试连接成功\r\n");
				//Toast.makeText(BluetoothComm.this,"send ok",Toast.LENGTH_SHORT).show();
			}
		});

		if (bluetooth == null) {// 设备没有蓝牙设备
			Toast.makeText(this, "没有找到蓝牙适配器", Toast.LENGTH_LONG).show();
			finish();
			return;
		}



      //  resources = getResources();
      //  InitWidth();
      //  InitTextView();
      //  InitViewPager();


	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.e("BluetoothComm", "-ON START-");
		if (!bluetooth.isEnabled()) {
			// 请求打开蓝牙设备
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			if (mCommService == null) {
				mCommService = new BluetoothCommService(this, mHandler);
			}
		}
	}

	@Override
	protected synchronized void onResume() {
		super.onResume();
		//setContentView(R.layout.main);

		//在这个方法中调用画图对象的设置方法











		Log.e("BluetoothComm","-ON RESUME-");
		if (mCommService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mCommService.getState() == BluetoothCommService.STATE_NONE) {
				// Start the Bluetooth services开启线程监听
				mCommService.start();
			}
		}
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (bluetooth != null) {
		//	bluetooth.disable();
		}
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mCommService != null)
			mCommService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	/**
	 * onActivityResult方法，当启动startActivityForResult返回之后调用，根据用户的操作来执行相应的操作
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				if (D)
					Log.d(TAG, "打开蓝牙设备");
				Toast.makeText(this, "成功打开蓝牙设备", Toast.LENGTH_SHORT).show();
			} else {
				if (D)
					Log.d(TAG, "不允许打开蓝牙设备");
				Toast.makeText(this, "不能打开蓝牙程序即将关闭", Toast.LENGTH_SHORT)
						.show();
				finish();// 用户不打开设备，程序结束
			}
			break;
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {// 用户选择链接的设备
				// Get the device MAC address
				String address = data.getExtras().getString(
						ScanDeviceActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BluetoothDevice object
				device = bluetooth.getRemoteDevice(address);
				// 尝试连接设备
				mCommService.connect(device);
			}
			break;
		}
		return;
	}



	private void ensureDiscoverable() {
		if (bluetooth.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			// 最长可见时间300s
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	// 创建菜单选项
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	// 菜单项被点击
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			// Launch the ScanDeviceActivity to see devices and do scan
			Intent serverIntent = new Intent(this, ScanDeviceActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.discoverable:
			ensureDiscoverable();
			return true;
		case R.id.about:
			//Intent intent = new Intent(BluetoothComm.this, AboutActivity.class);
			//startActivity(intent);
			return true;
		case R.id.exit:
			finish();
			return true;
		}
		return false;
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	public void sendMessage(String message) {
		//没有连接设备，不能发送
		if (mCommService.getState() != BluetoothCommService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.nodevice, Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mCommService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
		}
	}

	// The Handler that gets information back from the BluetoothChatService

	/**
	 * 处理接收信息的线程的信息handleMessage
	 * **/
	private final Handler mHandler = new Handler(new Handler.Callback(){
		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothCommService.STATE_CONNECTED:
					connectDevices.setText(R.string.title_connected_to);
					connectDevices.append(mConnectedDeviceName);
					// mConversationArrayAdapter.clear();
					break;
				case BluetoothCommService.STATE_CONNECTING:
					connectDevices.setText(R.string.title_connecting);
					break;
				case BluetoothCommService.STATE_LISTEN:
					break;
				case BluetoothCommService.STATE_NONE:
					connectDevices.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				// String writeMessage = new String(writeBuf);
				// mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				// mConversationArrayAdapter.add(mConnectedDeviceName+":  " +
				// readMessage);
				//rxEdit.append(readMessage);
				receive_message.setText(readMessage);
				if("123400".equals(readMessage)){
					Toast.makeText(BluetoothComm.this,"成功接收到了数据",Toast.LENGTH_SHORT).show();
				}

				System.out.println("接收的数据为"+readMessage+"   ");

				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				switchBt.setChecked(true);
				Toast.makeText(getApplicationContext(),
						"连接到" + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();

				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
			return false;

		}
	});

	public boolean getIsLineOk(){
		return isLineOk;
	}
	public void setIsLineOk(boolean isLineOk){
		  this.isLineOk = isLineOk;
	}


	private void showWarning(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(BluetoothComm.this);
		dialog.setTitle("警    告");
		dialog.setMessage("请先连接上设备后再开启app");
		dialog.setCancelable(false);
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(BluetoothComm.this, ScanDeviceActivity.class);
				startActivity(intent);

			}
		});
		dialog.show();
	}

}