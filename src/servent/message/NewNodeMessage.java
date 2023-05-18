package servent.message;

import java.io.Serial;

public class NewNodeMessage extends BasicMessage {

	@Serial
	private static final long serialVersionUID = 3899837286642127636L;

	public NewNodeMessage(String senderIp, String receiverIp, int senderPort, int receiverPort) {
		super(MessageType.NEW_NODE, senderIp, receiverIp, senderPort, receiverPort);
	}
}
