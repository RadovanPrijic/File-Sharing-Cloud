package servent.message;

import java.io.Serial;

public class UpdateMessage extends BasicMessage {

	@Serial
	private static final long serialVersionUID = 3586102505319194978L;

	public UpdateMessage(String senderIp, String receiverIp, int senderPort, int receiverPort, String text) {
		super(MessageType.UPDATE, senderIp, receiverIp, senderPort, receiverPort, text);
	}
}
