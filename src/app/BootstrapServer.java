package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BootstrapServer {

	private volatile boolean working = true;
	private List<ServentInfo> activeServents;
	
	private class CLIWorker implements Runnable {
		@Override
		public void run() {
			Scanner sc = new Scanner(System.in);
			
			String line;
			while(true) {
				line = sc.nextLine();
				
				if (line.equals("stop")) {
					working = false;
					break;
				}
			}
			
			sc.close();
		}
	}
	
	public BootstrapServer() {
		activeServents = new ArrayList<>();
	}
	
	public void doBootstrap(int bsPort) {
		Thread cliThread = new Thread(new CLIWorker());
		cliThread.start();
		
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(bsPort);
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e1) {
			AppConfig.timestampedErrorPrint("Problem while opening listener socket.");
			System.exit(0);
		}
		
		Random rand = new Random(System.currentTimeMillis());
		
		while (working) {
			try {
				Socket newServentSocket = listenerSocket.accept();
				
				 /* 
				 * Handling these messages is intentionally sequential, to avoid problems with
				 * concurrent initial starts.
				 * 
				 * In practice, we would have an always-active backbone of servents to avoid this problem.
				 */
				
				Scanner socketScanner = new Scanner(newServentSocket.getInputStream());
				String message = socketScanner.nextLine();
				
				/*
				 * New servent has hailed us. He is sending us his own listener port.
				 * He wants to get a listener port from a random active servent,
				 * or -1 if he is the first one.
				 */
				if (message.equals("Hail")) {
					String newServentIp = socketScanner.nextLine();
					int newServentPort = socketScanner.nextInt();

					AppConfig.timestampedStandardPrint("Got " + newServentIp + ":" + newServentPort);
					PrintWriter socketWriter = new PrintWriter(newServentSocket.getOutputStream());
					
					if (activeServents.size() == 0) {
						socketWriter.write(String.valueOf(-1) + "\n");
						activeServents.add(new ServentInfo(newServentPort, newServentIp)); //first one doesn't need to confirm
					} else {
						ServentInfo randServent = activeServents.get(rand.nextInt(activeServents.size()));
						String randServentIp = randServent.getIpAddress();
						int randServentPort = randServent.getListenerPort();
						socketWriter.write(randServentIp + "\n" + String.valueOf(randServentPort) + "\n");
					}
					
					socketWriter.flush();
					newServentSocket.close();
				} else if (message.equals("New")) {
					/**
					 * When a servent is confirmed not to be a collider, we add him to the list.
					 */
					String newServentIp = socketScanner.nextLine();
					int newServentPort = socketScanner.nextInt();

					AppConfig.timestampedStandardPrint("Adding " + newServentIp + ":" + newServentPort);
					
					activeServents.add(new ServentInfo(newServentPort, newServentIp));
					newServentSocket.close();
				} else if (message.equals("Adios")) {
					String serventToRemoveIp = socketScanner.nextLine();
					int serventToRemovePort = socketScanner.nextInt();

					int serventToRemove = -1;

					for (int i = 0; i < activeServents.size(); i++) {
						if (activeServents.get(i).getIpAddress().equals(serventToRemoveIp) &&
								activeServents.get(i).getListenerPort() == serventToRemovePort) {
							serventToRemove = i;
							break;
						}
					}

					if (serventToRemove != -1) {
						activeServents.remove(serventToRemove);
						AppConfig.timestampedStandardPrint("Servent " + serventToRemoveIp + " : " + serventToRemovePort + " has been removed.");
					} else
						AppConfig.timestampedErrorPrint("Removing the servent was unsuccessful.");
				}
				
			} catch (SocketTimeoutException e) {
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expects one command line argument - the port to listen on.
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			AppConfig.timestampedErrorPrint("Bootstrap started without port argument.");
		}
		
		int bsPort = 0;
		try {
			bsPort = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Bootstrap port not valid: " + args[0]);
			System.exit(0);
		}
		
		AppConfig.timestampedStandardPrint("Bootstrap server started on port: " + bsPort);
		
		BootstrapServer bs = new BootstrapServer();
		bs.doBootstrap(bsPort);
	}
}
