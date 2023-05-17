package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.AddMessage;
import servent.message.MessageType;
import servent.message.util.MessageUtil;
import virtual_filesys.VirtualFile;
import virtual_filesys.VirtualFileRepository;

import java.io.File;

public class AddCommand implements CLICommand{

    @Override
    public String commandName() {
        return "add";
    }

    @Override
    public void execute(String args) {
        if (args == null || args.split(" ").length != 1) {
            AppConfig.timestampedErrorPrint("The ADD command must have exactly one argument.");
            return;
        }

        File file = new File(AppConfig.WORKING_DIRECTORY + "/" + args);
        if (!file.exists()) {
            AppConfig.timestampedErrorPrint("File with the path " + args + " does not exist in this working directory and could therefore not be added.");
            return;
        }

        VirtualFile virtualFile = VirtualFileRepository.createVirtualFile(file, args);

        String fileName = args, forHash = args;
        int firstOccurrence = args.indexOf("/");
        int lastOccurrence = args.lastIndexOf("/");

        if (firstOccurrence >= 0) {
            forHash = args.substring(0, firstOccurrence);
            fileName = args.substring(lastOccurrence + 1, fileName.length());
        }

        int fileHash = ChordState.fileHash(forHash);
        AppConfig.timestampedStandardPrint("ADD command" + " | File path: " + args + " | File name: " + fileName + " | File hash: " + fileHash);

        if (AppConfig.chordState.isKeyMine(fileHash)) {
            VirtualFileRepository.addFileToVirtualFileSystem(MessageType.ADD, virtualFile, args);
        } else {
            ServentInfo nextServentInfo = AppConfig.chordState.getNextNodeForKey(fileHash);
            String nextNodeIp = nextServentInfo.getIpAddress();
            int nextNodePort = nextServentInfo.getListenerPort();
            AddMessage message = new AddMessage(
                    MessageType.ADD,
                    AppConfig.myServentInfo.getIpAddress(),
                    nextNodeIp,
                    AppConfig.myServentInfo.getListenerPort(),
                    nextNodePort,
                    virtualFile,
                    fileHash);
            MessageUtil.sendMessage(message);
        }
    }
}
