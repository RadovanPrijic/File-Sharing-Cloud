package servent.message;

import virtual_filesys.VirtualFile;

import java.io.Serial;

public class AddMessage extends BasicMessage{

    @Serial
    private static final long serialVersionUID = -6019525696014986648L;

    private VirtualFile virtualFile;
    private int fileHash;

    public AddMessage(MessageType type, String senderIp, String receiverIp, int senderPort, int receiverPort,
                      VirtualFile virtualFile, int fileHash) {
        super(type, senderIp, receiverIp, senderPort, receiverPort);
        this.virtualFile = virtualFile;
        this.fileHash = fileHash;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public int getFileHash() {
        return fileHash;
    }
}
