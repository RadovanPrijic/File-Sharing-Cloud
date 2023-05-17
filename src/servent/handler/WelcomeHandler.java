package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdateMessage;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

public class WelcomeHandler implements MessageHandler {

	private Message clientMessage;
	
	public WelcomeHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.WELCOME) {
			WelcomeMessage welcomeMsg = (WelcomeMessage)clientMessage;
			AppConfig.chordState.init(welcomeMsg);
			UpdateMessage message = new UpdateMessage(
					AppConfig.myServentInfo.getIpAddress(),
					AppConfig.chordState.getNextNodeIp(),
					AppConfig.myServentInfo.getListenerPort(),
					AppConfig.chordState.getNextNodePort(),
					"");
			MessageUtil.sendMessage(message);
			
		} else
			AppConfig.timestampedErrorPrint("Welcome message handler got a message that is not WELCOME.");
	}
}
