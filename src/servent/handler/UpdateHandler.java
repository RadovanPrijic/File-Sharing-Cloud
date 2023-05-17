package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdateMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class UpdateHandler implements MessageHandler {

	private Message clientMessage;
	
	public UpdateHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.UPDATE) {
			if (clientMessage.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
				ServentInfo newNodeInfo = new ServentInfo(clientMessage.getSenderIp(), clientMessage.getSenderPort());
				List<ServentInfo> newNodes = new ArrayList<>();
				newNodes.add(newNodeInfo);
				
				AppConfig.chordState.addNodes(newNodes);
				String newMessageText = "";
				if (clientMessage.getMessageText().equals("")) {
					newMessageText = String.valueOf(AppConfig.myServentInfo.getListenerPort());
				} else {
					newMessageText = clientMessage.getMessageText() + "," + AppConfig.myServentInfo.getListenerPort();
				}
				Message message = new UpdateMessage(
						clientMessage.getSenderIp(),
						AppConfig.chordState.getNextNodeIp(),
						clientMessage.getSenderPort(),
						AppConfig.chordState.getNextNodePort(),
						newMessageText);
				MessageUtil.sendMessage(message);
			} else {
				String messageText = clientMessage.getMessageText();
				String[] ports = messageText.split(",");
				
				List<ServentInfo> allNodes = new ArrayList<>();
				for (String port : ports) {
					allNodes.add(new ServentInfo(clientMessage.getSenderIp(), Integer.parseInt(port)));
				}
				AppConfig.chordState.addNodes(allNodes);
			}
		} else
			AppConfig.timestampedErrorPrint("Update message handler got message that is not UPDATE.");
	}
}
