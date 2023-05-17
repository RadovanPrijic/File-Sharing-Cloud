package servent.message;

import virtual_filesys.VirtualFile;

import java.io.Serial;
import java.util.Map;

public class QuitMessage extends BasicMessage{

    @Serial
    private static final long serialVersionUID = 9100952796587741113L;

    private boolean isSuccessor;
    private Map<String, VirtualFile> values;

    public QuitMessage(MessageType type, String senderIp, String receiverIp, int senderPort, int receiverPort,
                       boolean isSuccessor, Map<String, VirtualFile> values) {
        super(MessageType.QUIT, senderIp, receiverIp, senderPort, receiverPort);
        this.isSuccessor = isSuccessor;
        this.values = values;
    }

    public boolean isSuccessor() {
        return isSuccessor;
    }

    public Map<String, VirtualFile> getValues() {
        return values;
    }
}
