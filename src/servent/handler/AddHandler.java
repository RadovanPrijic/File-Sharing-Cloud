package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.AddMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;
import virtual_filesys.VirtualFile;
import virtual_filesys.VirtualFileRepository;

public class AddHandler implements MessageHandler{

    private Message clientMessage;

    public AddHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.ADD) {
            AddMessage addMsg = (AddMessage)clientMessage;
            VirtualFile virtualFile = addMsg.getVirtualFile();
            int fileHash = addMsg.getFileHash();

            if (AppConfig.chordState.isKeyMine(fileHash))
                VirtualFileRepository.addFileToVirtualFileSystem(MessageType.ADD, virtualFile, virtualFile.getFilePath());
            else {
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(fileHash);
                AddMessage message = new AddMessage(
                        MessageType.ADD,
                        clientMessage.getSenderIp(),
                        nextNode.getIpAddress(),
                        clientMessage.getSenderPort(),
                        nextNode.getListenerPort(),
                        virtualFile,
                        fileHash);
                MessageUtil.sendMessage(message);
            }
        } else
            AppConfig.timestampedErrorPrint("Add message handler got a message that is not ADD.");
    }
}
