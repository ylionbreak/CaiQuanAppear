package science.hzl.caiquanappear;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by YLion on 2015/5/13.
 */
public class BluetoothManager {
	BluetoothSocket transferSocket;
	Boolean listening=false;
	String returnMessage="";
	Handler handler;

	void connectDevice(BluetoothDevice device,Handler _handler){
		try{
			UUID uuid =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
			BluetoothSocket bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
			bluetoothSocket.connect();
			transferSocket=bluetoothSocket;
			handler=_handler;
			Log.e("x", String.valueOf(bluetoothSocket.isConnected()));
		}catch (IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public UUID startServerSocket  (BluetoothAdapter bluetoothAdapter){
		UUID uuid =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		String name = "bluetoothServer";

		try{
			final BluetoothServerSocket btserver =
					bluetoothAdapter.listenUsingRfcommWithServiceRecord(name,uuid);

			Thread acceptTread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						BluetoothSocket serverSocket =btserver.accept();
						//开始监听
						transferSocket = serverSocket;
						listenForMessages(serverSocket);
					}catch (IOException e){
						e.printStackTrace();
					}
				}
			});
			acceptTread.start();
		}catch (IOException e){
			e.printStackTrace();
		}
		return uuid;
	}



	private void sendMessage(String message){
		OutputStream outputStream;
		try {
			outputStream = transferSocket.getOutputStream();
			byte[] bytes = (message).getBytes();
			outputStream.write(bytes);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public void listenForMessages(BluetoothSocket socket){
		listening = true;

		int bufferSize =1024;
		byte[] buffer = new byte[bufferSize];
		try {
			InputStream inputStream = socket.getInputStream();
			int bytesRead ;

			while (listening){
				bytesRead = inputStream.read(buffer);
				String result = "";
				if(bytesRead != -1){

					while(bytesRead == bufferSize && buffer[bufferSize-1] != 0){
						result = result + new String(buffer,0,bytesRead);
						bytesRead = inputStream.read(buffer);
					}
					result = result + new String(buffer,0,bytesRead);
				}
				listening=false;
				returnMessage=result;

				///改变界面
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}

		}catch (IOException e){
			e.printStackTrace();
		}
	}



	public BluetoothSocket getTransferSocket() {
		return transferSocket;
	}
}
