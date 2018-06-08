package net.dxs.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import net.dxs.client.handle.HandleMsg;

public class SocketClient {

	public static void main(String[] args) {
		Socket socket = null;
		try {
			socket = new Socket("192.168.5.12", 6002);
			HandleMsg responseHandle = new HandleMsg(socket);
			Thread thread = new Thread(responseHandle);
			thread.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
