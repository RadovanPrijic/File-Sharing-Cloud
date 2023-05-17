package servent.message;

import java.io.Serial;

public class RemoveMessage extends BasicMessage{

    @Serial
    private static final long serialVersionUID = 1485593917120778749L;

    private String filePath;
    private int fileHash;

    public RemoveMessage(MessageType type, String senderIp, String receiverIp, int senderPort, int receiverPort,
                         String filePath, int fileHash) {
        super(type, senderIp, receiverIp, senderPort, receiverPort);
        this.filePath = filePath;
        this.fileHash = fileHash;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getFileHash() {
        return fileHash;
    }
}
