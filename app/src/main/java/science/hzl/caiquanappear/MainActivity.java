package science.hzl.caiquanappear;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

	BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
	Button connect;
	TextView textView;
	String result;

	private Handler handler =new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:{
					connect.setVisibility(View.VISIBLE);
					connect.setText("");
					Random random = new Random();
					int a = random.nextInt()%3;

//					ScaleAnimation scaleAnimation =new ScaleAnimation(0.3f,1,0.3f,1,Animation.RELATIVE_TO_SELF,0.5f,
//							Animation.RELATIVE_TO_SELF,0.5f);
//					scaleAnimation.setDuration(200);

					if(a==0){
//						connect.setBackgroundResource(R.drawable.shitou);
//						connect.startAnimation(scaleAnimation);

						gestureshitouImageView.setVisibility(View.VISIBLE);
						ShitouAnimation.start();
						//gestureshitouImageView.setVisibility(View.GONE);
					}else if(a==1){
//						connect.setBackgroundResource(R.drawable.jiandao);
//						connect.startAnimation(scaleAnimation);

						gesturejiandaoImageView.setVisibility(View.VISIBLE);
						JiandaoAnimation.start();
						//gesturejiandaoImageView.setVisibility(View.GONE);
					}else if(a==2){
//						connect.setBackgroundResource(R.drawable.bu);
//						connect.startAnimation(scaleAnimation);

						gesturebuImageView.setVisibility(View.VISIBLE);
						BuAnimation.start();
						//gesturebuImageView.setVisibility(View.GONE);
					}

				}
				break;

			}
			super.handleMessage(msg);
		}

	};

	ImageView gesturebuImageView;
	ImageView gesturejiandaoImageView;
	ImageView gestureshitouImageView;
	AnimationDrawable BuAnimation;
	AnimationDrawable ShitouAnimation;
	AnimationDrawable JiandaoAnimation;

	BluetoothManager bluetoothManager=new BluetoothManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		connect=(Button)findViewById(R.id.connect);
		textView=(TextView)findViewById(R.id.textView);

		gesturebuImageView = (ImageView)findViewById(R.id.gesture_bu_animation);
		gesturebuImageView.setBackgroundResource(R.drawable.gesture_bu);
		gesturebuImageView.setVisibility(View.GONE);
		BuAnimation = (AnimationDrawable)gesturebuImageView.getBackground();

		gesturejiandaoImageView = (ImageView)findViewById(R.id.gesture_jiandao_animation);
		gesturejiandaoImageView.setBackgroundResource(R.drawable.gesture_bu);
		gesturejiandaoImageView.setVisibility(View.GONE);
		ShitouAnimation = (AnimationDrawable)gesturejiandaoImageView.getBackground();

		gestureshitouImageView = (ImageView)findViewById(R.id.gesture_shitou_animation);
		gestureshitouImageView.setBackgroundResource(R.drawable.gesture_bu);
		gestureshitouImageView.setVisibility(View.GONE);
		JiandaoAnimation = (AnimationDrawable)gestureshitouImageView.getBackground();

		connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BluetoothDevice device = bluetoothAdapter.getRemoteDevice("18:DC:56:D3:26:D1");
				//BluetoothDevice device = bluetoothAdapter.getRemoteDevice("22:22:CC:06:08:C9");
				bluetoothManager.connectDevice(device,handler);
				connect.setVisibility(View.GONE);

				if(bluetoothManager.transferSocket.isConnected()) {
					Thread acceptTread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								listenForMessages(bluetoothManager.transferSocket);
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


	private void listenForMessages(BluetoothSocket socket){

		int bufferSize =1024;
		byte[] buffer = new byte[bufferSize];
		try {
			InputStream inputStream = socket.getInputStream();
			int bytesRead;

			while (true){
				bytesRead = inputStream.read(buffer);
				String result = "";
				if(bytesRead != -1){

					while(bytesRead == bufferSize && buffer[bufferSize-1]!=0){
						result = result + new String(buffer,0,bytesRead-1);
						bytesRead = inputStream.read(buffer);
					}
					result = result + new String(buffer,0,bytesRead);
				}
				this.result=result;
			//	if(Integer.parseInt(result)==1||Integer.parseInt(result)==2||Integer.parseInt(result)==3){

					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);

			//	}

			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}







}
