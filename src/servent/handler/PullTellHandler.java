package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PullTellMessage;
import virtual_filesys.VirtualFile;
import virtual_filesys.VirtualFileRepository;

public class PullTellHandler implements MessageHandler{

    private Message clientMessage;

    public PullTellHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.PULL_TELL) {
            PullTellMessage pullTellMsg = (PullTellMessage)clientMessage;
            VirtualFile virtualFile = pullTellMsg.getVirtualFile();

            if (virtualFile != null)
                VirtualFileRepository.addFileToVirtualFileSystem(MessageType.PULL_ASK, virtualFile, virtualFile.getFilePath());
            else
                AppConfig.timestampedErrorPrint("You have tried to pull a file that does not exist in the virtual file system.");
        } else
            AppConfig.timestampedErrorPrint("PullTell message handler got a message that is not PULL_TELL.");
    }
}
