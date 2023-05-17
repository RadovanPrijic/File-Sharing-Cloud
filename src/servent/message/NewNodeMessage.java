package servent.message;

public class NewNodeMessage extends BasicMessage {

	private static final long serialVersionUID = 3899837286642127636L;

	public NewNodeMessage(String senderIp, String receiverIp, int senderPort, int receiverPort) {
		super(MessageType.NEW_NODE, senderIp, receiverIp, senderPort, receiverPort);
	}
}
