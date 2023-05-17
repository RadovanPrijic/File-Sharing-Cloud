package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.MessageType;
import servent.message.RemoveMessage;
import servent.message.util.MessageUtil;
import virtual_filesys.VirtualFile;
import virtual_filesys.VirtualFileRepository;

public class RemoveCommand implements CLICommand{

    @Override
    public String commandName() {
        return "remove";
    }

    @Override
    public void execute(String args) {
        if (args == null || args.split(" ").length != 1) {
            AppConfig.timestampedErrorPrint("The REMOVE command must have exactly one argument.");
            return;
        }

        String fileName = args, forHash = args;
        int firstOccurrence = args.indexOf("/");
        int lastOccurrence = args.lastIndexOf("/");

        if (firstOccurrence >= 0) {
            forHash = args.substring(0, firstOccurrence);
            fileName = args.substring(lastOccurrence + 1, fileName.length());
        }

        int fileHash = ChordState.fileHash(forHash);
        AppConfig.timestampedStandardPrint("REMOVE command" + " | File path: " + args + " | File name: " + fileName + " | File hash: " + fileHash);

        if (AppConfig.chordState.isKeyMine(fileHash)) {
            VirtualFile fileToRemove = VirtualFileRepository.virtualFilesMap.get(args);
            boolean isFilePresent = false;

            if (fileToRemove == null)
                AppConfig.timestampedErrorPrint("File with the path " + args + " does not exist in this virtual file repository and could therefore not be removed.");
            else
                isFilePresent = true;

            if (isFilePresent)
                VirtualFileRepository.removeFromMap(args);
        } else {
            ServentInfo nextServentInfo = AppConfig.chordState.getNextNodeForKey(fileHash);
            String nextNodeIp = nextServentInfo.getIpAddress();
            int nextNodePort = nextServentInfo.getListenerPort();
            RemoveMessage message = new RemoveMessage(
                    MessageType.REMOVE,
                    AppConfig.myServentInfo.getIpAddress(),
                    nextNodeIp,
                    AppConfig.myServentInfo.getListenerPort(),
                    nextNodePort,
                    args,
                    fileHash);
            MessageUtil.sendMessage(message);
        }
    }
}
