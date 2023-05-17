package servent.message;

import virtual_filesys.VirtualFile;

import java.io.Serial;

public class PullTellMessage extends BasicMessage{

    @Serial
    private static final long serialVersionUID = -1405560229503408419L;

    private VirtualFile virtualFile;

    public PullTellMessage(MessageType type, String senderIp, String receiverIp, int senderPort, int receiverPort, VirtualFile virtualFile) {
        super(type, senderIp, receiverIp, senderPort, receiverPort);
        this.virtualFile = virtualFile;
    }

    public VirtualFile getVirtualFile(){
        return virtualFile;
    }
}
