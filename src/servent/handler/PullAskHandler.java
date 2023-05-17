package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PullAskMessage;
import servent.message.PullTellMessage;
import servent.message.util.MessageUtil;
import virtual_filesys.VirtualFile;
import virtual_filesys.VirtualFileRepository;

public class PullAskHandler implements MessageHandler{

    private Message clientMessage;

    public PullAskHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.PULL_ASK) {
            PullAskMessage pullAskMsg = (PullAskMessage)clientMessage;
            String filePath = pullAskMsg.getFilePath();
            int fileHash = pullAskMsg.getFileHash();

            if (AppConfig.chordState.isKeyMine(fileHash)) {
                VirtualFile virtualFile = VirtualFileRepository.pullFileFromVirtualFileSystem(filePath);
                PullTellMessage pullTellMsg = new PullTellMessage(
                        MessageType.PULL_TELL,
                        AppConfig.myServentInfo.getIpAddress(),
                        clientMessage.getSenderIp(),
                        AppConfig.myServentInfo.getListenerPort(),
                        clientMessage.getSenderPort(),
                        virtualFile);
                MessageUtil.sendMessage(pullTellMsg);
            } else {
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(fileHash);
                PullAskMessage message = new PullAskMessage(
                        MessageType.PULL_ASK,
                        clientMessage.getSenderIp(),
                        nextNode.getIpAddress(),
                        clientMessage.getSenderPort(),
                        nextNode.getListenerPort(),
                        filePath,
                        fileHash);
                MessageUtil.sendMessage(message);
            }
        } else
            AppConfig.timestampedErrorPrint("PullAsk message handler got a message that is not PULL_ASK.");
    }
}
