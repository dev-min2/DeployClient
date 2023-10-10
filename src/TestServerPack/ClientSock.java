package TestServerPack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

// SyncSock
public class ClientSock {
	private Socket socket;
	private InputStream inStream;
    private OutputStream outStream;
	
	ClientSock(String connectIP, int port, boolean local) {
		socket = new Socket();
		
		InetAddress rowIp;
		SocketAddress sockAddress;
		try {
			
			if(!local) {
				InetAddress[] ips = InetAddress.getAllByName(connectIP);
				rowIp = ips != null && ips.length > 0 ? ips[0] : null;
				sockAddress = new InetSocketAddress(rowIp, port);
			}
			else
				sockAddress = new InetSocketAddress(InetAddress.getLocalHost(),port);
			
			socket.connect(sockAddress);

			inStream = socket.getInputStream();
			outStream = socket.getOutputStream();
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	private byte[] convertStringIpToBytes(String ip) {
		if(ip == null || ip.isEmpty())
			return null;
		
		String[] strs = ip.split("\\.");
		byte[] ret = new byte[strs.length];
		int cnt = 0;
		for(String s : strs) {
			ret[cnt++] = (byte)Integer.parseInt(s);
		}
		
		return ret;
	}
	
	public boolean sendData(String data) {
		try {
			outStream.write(data.getBytes());
			outStream.flush();
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	// 덜 수신되는 경우는 없다고 가정 및 에코로 데이터 주고받음. 
	public String recvData() {
		byte[] readBuffer = new byte[100];
		int len = 0;
		try {
			len = inStream.read(readBuffer);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return new String(readBuffer,0,len);
	}
	
	public void close() throws IOException {
		socket.close();
	}
}
