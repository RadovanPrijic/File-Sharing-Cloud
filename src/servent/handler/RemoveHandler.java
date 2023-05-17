package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.RemoveMessage;
import servent.message.util.MessageUtil;
import virtual_filesys.VirtualFile;
import virtual_filesys.VirtualFileRepository;

public class RemoveHandler implements MessageHandler{

    private Message clientMessage;

    public RemoveHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.REMOVE) {
            RemoveMessage removeMsg = (RemoveMessage)clientMessage;
            String filePath = removeMsg.getFilePath();
            int fileHash = removeMsg.getFileHash();

            if (AppConfig.chordState.isKeyMine(fileHash)) {
                VirtualFile fileToRemove = VirtualFileRepository.virtualFilesMap.get(filePath);
                boolean isFilePresent = false;

                if (fileToRemove == null)
                    AppConfig.timestampedErrorPrint("File with the path " + filePath + " does not exist in this virtual file repository and could therefore not be removed.");
                else
                    isFilePresent = true;

                if (isFilePresent)
                    VirtualFileRepository.removeFromMap(filePath);
            } else {
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(fileHash);
                RemoveMessage message = new RemoveMessage(
                        MessageType.REMOVE,
                        clientMessage.getSenderIp(),
                        nextNode.getIpAddress(),
                        clientMessage.getSenderPort(),
                        nextNode.getListenerPort(),
                        filePath,
                        fileHash);
                MessageUtil.sendMessage(message);
            }

        } else
            AppConfig.timestampedErrorPrint("Remove message handler got a message that is not REMOVE.");
    }
}
