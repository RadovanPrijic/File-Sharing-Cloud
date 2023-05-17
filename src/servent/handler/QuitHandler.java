package servent.handler;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.QuitMessage;
import servent.message.util.MessageUtil;
import virtual_filesys.VirtualFile;
import virtual_filesys.VirtualFileRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class QuitHandler implements MessageHandler{

    private Message clientMessage;

    public QuitHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.QUIT) {
            QuitMessage quitMsg = (QuitMessage)clientMessage;

            if (quitMsg.getSenderIp().equals(AppConfig.myServentInfo.getIpAddress()) && quitMsg.getSenderPort() == AppConfig.myServentInfo.getListenerPort()) {
                VirtualFileRepository.emptyTheWorkingDirectory();
                ChordState.quit(quitMsg.getSenderIp(), quitMsg.getSenderPort());
                System.exit(0);
            } else {
                if (quitMsg.isSuccessor()) {
                    for (Map.Entry<String, VirtualFile> entryMap : quitMsg.getValues().entrySet()) {
                        String filePath = entryMap.getKey();
                        VirtualFile virtualFile = entryMap.getValue();
                        VirtualFileRepository.addFileToVirtualFileSystem(MessageType.ADD, virtualFile, filePath);
                    }
                }
                String nextNodeIp = AppConfig.chordState.getNextNodeIp();
                int nextNodePort = AppConfig.chordState.getNextNodePort();
                clearServentInfo(quitMsg.getSenderPort());
                QuitMessage message = new QuitMessage(
                        MessageType.QUIT,
                        quitMsg.getSenderIp(),
                        nextNodeIp,
                        quitMsg.getSenderPort(),
                        nextNodePort,
                        false,
                        VirtualFileRepository.virtualFilesMap); //TODO Mozda moze biti i null ovaj values map
                MessageUtil.sendMessage(message);
            }
        } else
            AppConfig.timestampedErrorPrint("Quit message handler got a message that is not QUIT.");
    }

    private void clearServentInfo(int port) {
        List<ServentInfo> allNodes = AppConfig.chordState.getAllNodeInfo();

        int index = 0;
        for (ServentInfo servent : allNodes) {
            if (servent.getListenerPort() == port)
                break;
            else
                index++;
        }
        allNodes.remove(index);

        Arrays.fill(AppConfig.chordState.getSuccessorTable(), null);

        if (allNodes.size() == 0) {
            AppConfig.chordState.setPredecessor(null);
            return;
        }

        QuitMessage msg = (QuitMessage)clientMessage;

        if (msg.isSuccessor()) {
            AppConfig.chordState.setPredecessor(allNodes.get(allNodes.size() - 1));
        }

        AppConfig.chordState.updateSuccessorTable();
    }
}
