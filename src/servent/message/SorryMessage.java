package servent.message;

public class SorryMessage extends BasicMessage {

	private static final long serialVersionUID = 8866336621366084210L;

	public SorryMessage(String senderIp, String receiverIp, int senderPort, int receiverPort) {
		super(MessageType.SORRY, senderIp, receiverIp, senderPort, receiverPort);
	}
}
