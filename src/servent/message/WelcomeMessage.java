package servent.message;

import virtual_filesys.VirtualFile;

import java.util.Map;

public class WelcomeMessage extends BasicMessage {

	private static final long serialVersionUID = -8981406250652693908L;

	private Map<String, VirtualFile> values;
	
	public WelcomeMessage(String senderIp, String receiverIp, int senderPort, int receiverPort, Map<String, VirtualFile> values) {
		super(MessageType.WELCOME, senderIp, receiverIp, senderPort, receiverPort);
		this.values = values;
	}
	
	public Map<String, VirtualFile> getValues() {
		return values;
	}
}
