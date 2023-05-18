package servent.message;

import java.io.Serial;

public class SorryMessage extends BasicMessage {

	@Serial
	private static final long serialVersionUID = 8866336621366084210L;

	public SorryMessage(String senderIp, String receiverIp, int senderPort, int receiverPort) {
		super(MessageType.SORRY, senderIp, receiverIp, senderPort, receiverPort);
	}
}
