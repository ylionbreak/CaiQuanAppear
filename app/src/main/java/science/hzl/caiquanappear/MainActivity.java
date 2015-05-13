package science.hzl.caiquanappear;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

	BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
	BluetoothSocket transferSocket;
	Button connect;
	TextView textView;
	String result;
	//RelativeLayout layout = (RelativeLayout)findViewById(R.id.main_layout);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		connect=(Button)findViewById(R.id.connect);
		textView=(TextView)findViewById(R.id.textView);

		connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BluetoothDevice device = bluetoothAdapter.getRemoteDevice("18:DC:56:D3:26:D1");
				//BluetoothDevice device = bluetoothAdapter.getRemoteDevice("22:22:CC:06:08:C9");
				connectDevice(device);
				connect.setVisibility(View.GONE);

				if(transferSocket.isConnected()) {
					Thread acceptTread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								listenForMessages(transferSocket, result);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					acceptTread.start();
				}



			}
		});

	}

	private void sendMessage(BluetoothSocket socket,String message){
		OutputStream outputStream;
		try {
			outputStream = socket.getOutputStream();

			byte[] bytes = (message+" ").getBytes();
			bytes[bytes.length-1]=0;

			outputStream.write(bytes);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	void connectDevice(BluetoothDevice device){
		try{
			UUID uuid =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
			BluetoothSocket bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
			bluetoothSocket.connect();
			transferSocket=bluetoothSocket;
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	private void listenForMessages(BluetoothSocket socket,String _result){

		int bufferSize =1024;
		byte[] buffer = new byte[bufferSize];
		try {
			InputStream inputStream = socket.getInputStream();
			int bytesRead = -1;

			while (true){
				bytesRead = inputStream.read(buffer);
				if(bytesRead != -1){
					String result = "";
					while(bytesRead == bufferSize && buffer[bufferSize-1]!=0){
						result = result + new String(buffer,0,bytesRead-1);
						bytesRead = inputStream.read(buffer);
					}
				}

				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);

			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	private Handler handler =new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:{
					connect.setVisibility(View.VISIBLE);
					connect.setText("");
					Random random = new Random();
					int a = random.nextInt()%3;

					ScaleAnimation scaleAnimation =new ScaleAnimation(0.3f,1,0.3f,1,Animation.RELATIVE_TO_SELF,0.5f,
							Animation.RELATIVE_TO_SELF,0.5f);
					scaleAnimation.setDuration(200);
					if(a==0){
						connect.setBackgroundResource(R.drawable.shitou);
						connect.startAnimation(scaleAnimation);
					}else if(a==1){
						connect.setBackgroundResource(R.drawable.jiandao);
						connect.startAnimation(scaleAnimation);
					}else if(a==2){
						connect.setBackgroundResource(R.drawable.bu);
						connect.startAnimation(scaleAnimation);
					}

				}
					break;

			}
			super.handleMessage(msg);
		}

	};



}
