package app;

import servent.message.NewNodeMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ServentInitializer implements Runnable {

	private ServentInfo getSomeServentInfo() {
		String bsIp = AppConfig.BOOTSTRAP_IP;
		int bsPort = AppConfig.BOOTSTRAP_PORT;

		String serventIp = null;
		int serventPort = -2;
		
		try {
			Socket bsSocket = new Socket(bsIp, bsPort);
			
			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			bsWriter.write("Hail\n" + AppConfig.myServentInfo.getIpAddress() + "\n" + AppConfig.myServentInfo.getListenerPort() + "\n");
			bsWriter.flush();
			
			Scanner bsScanner = new Scanner(bsSocket.getInputStream());

			serventIp = bsScanner.nextLine();

			if (serventIp.equals("-1"))
				return new ServentInfo(-1, null);

			serventPort = bsScanner.nextInt();
			
			bsSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new ServentInfo(serventIp, serventPort);
	}
	
	@Override
	public void run() {
		ServentInfo someServentInfo = getSomeServentInfo();
		
		if (someServentInfo.getListenerPort() == -2) {
			AppConfig.timestampedErrorPrint("Error in contacting bootstrap. Exiting...");
			System.exit(0);
		}
		if (someServentInfo.getListenerPort() == -1) { //bootstrap gave us -1 -> we are first
			AppConfig.timestampedStandardPrint("First node in Chord system.");
		} else { //bootstrap gave us something else - let that node tell our successor that we are here
			NewNodeMessage nnm = new NewNodeMessage(
					AppConfig.myServentInfo.getIpAddress(),
					someServentInfo.getIpAddress(),
					AppConfig.myServentInfo.getListenerPort(),
					someServentInfo.getListenerPort());
			 MessageUtil.sendMessage(nnm);
		}
	}

}
