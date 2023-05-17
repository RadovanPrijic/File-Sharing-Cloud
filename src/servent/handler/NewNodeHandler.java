package servent.handler;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.*;
import servent.message.util.MessageUtil;
import virtual_filesys.VirtualFile;
import virtual_filesys.VirtualFileRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class NewNodeHandler implements MessageHandler {

	private Message clientMessage;
	
	public NewNodeHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.NEW_NODE) {
			String newNodeIp = clientMessage.getSenderIp();
			int newNodePort = clientMessage.getSenderPort();
			ServentInfo newNodeInfo = new ServentInfo(newNodeIp, newNodePort);
			
			//check if the new node collides with another existing node.
			if (AppConfig.chordState.isCollision(newNodeInfo.getChordId())) {
				Message sry = new SorryMessage(AppConfig.myServentInfo.getIpAddress(), clientMessage.getSenderIp(), AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort());				MessageUtil.sendMessage(sry);
				MessageUtil.sendMessage(sry);
				return;
			}
			
			//check if he is my predecessor
			boolean isMyPred = AppConfig.chordState.isKeyMine(newNodeInfo.getChordId());
			if (isMyPred) { //if yes, prepare and send welcome message
				ServentInfo hisPred = AppConfig.chordState.getPredecessor();
				if (hisPred == null) {
					hisPred = AppConfig.myServentInfo;
				}
				
				AppConfig.chordState.setPredecessor(newNodeInfo);
				Map<String, VirtualFile> myVirtualFiles = VirtualFileRepository.getVirtualFilesMap();
				Map<String, VirtualFile> hisVirtualFiles = new HashMap<>();
				
				int myId = AppConfig.myServentInfo.getChordId();
				int hisPredId = hisPred.getChordId();
				int newNodeId = newNodeInfo.getChordId();
				
				for (Entry<String, VirtualFile> valueEntry : myVirtualFiles.entrySet()) {

					String filePath = valueEntry.getKey();
					String fileName = filePath;

					int index = filePath.indexOf("/");
					if (index >= 0) {
						fileName = filePath.substring(0, index);
					}
					int fileHash = ChordState.fileHash(fileName);

					if (hisPredId == myId) { //i am first and he is second
						if (myId < newNodeId) {
							if (fileHash <= newNodeId && fileHash > myId) {
								hisVirtualFiles.put(valueEntry.getKey(), valueEntry.getValue());
							}
						} else {
							if (fileHash <= newNodeId || fileHash > myId) {
								hisVirtualFiles.put(valueEntry.getKey(), valueEntry.getValue());
							}
						}
					} else if (hisPredId < myId) { //my old predecesor was before me
						if (fileHash <= newNodeId) {
							hisVirtualFiles.put(valueEntry.getKey(), valueEntry.getValue());
						}
					} else { //my old predecesor was after me
						if (hisPredId > newNodeId) { //new node overflow
							if (fileHash <= newNodeId || fileHash > hisPredId) {
								hisVirtualFiles.put(valueEntry.getKey(), valueEntry.getValue());
							}
						} else { //no new node overflow
							if (fileHash <= newNodeId && fileHash > hisPredId) {
								hisVirtualFiles.put(valueEntry.getKey(), valueEntry.getValue());
							}
						}
					}
				}
				for (String key : hisVirtualFiles.keySet()) { //remove his values from my map
					myVirtualFiles.remove(key);
				}
				VirtualFileRepository.setVirtualFilesMap(myVirtualFiles);

				WelcomeMessage message = new WelcomeMessage(
						AppConfig.myServentInfo.getIpAddress(),
						newNodeIp,
						AppConfig.myServentInfo.getListenerPort(),
						newNodePort,
						hisVirtualFiles);
				MessageUtil.sendMessage(message);
			} else { //if he is not my predecessor, let someone else take care of it
				ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(newNodeInfo.getChordId());
				NewNodeMessage message = new NewNodeMessage(
						newNodeIp,
						nextNode.getIpAddress(),
						newNodePort,
						nextNode.getListenerPort());
				MessageUtil.sendMessage(message);
			}
		} else
			AppConfig.timestampedErrorPrint("NewNode message handler got something that is not NEW_NODE.");
	}
}
