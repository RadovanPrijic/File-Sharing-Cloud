package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.MessageType;
import servent.message.PullAskMessage;
import servent.message.util.MessageUtil;

public class PullCommand implements CLICommand{

    @Override
    public String commandName() {
        return "pull";
    }

    @Override
    public void execute(String args) {
        if (args == null || args.split(" ").length != 1) {
            AppConfig.timestampedErrorPrint("The PULL command must have exactly one argument.");
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
        AppConfig.timestampedStandardPrint("PULL command" + " | File path: " + args + " | File name: " + fileName + " | File hash: " + fileHash);

        if (AppConfig.chordState.isKeyMine(fileHash)) {
            AppConfig.timestampedErrorPrint("Try adding the file with the path " + args + " instead of pulling it.");
        } else {
            ServentInfo nextServentInfo = AppConfig.chordState.getNextNodeForKey(fileHash);
            String nextNodeIp = nextServentInfo.getIpAddress();
            int nextNodePort = nextServentInfo.getListenerPort();
            PullAskMessage message = new PullAskMessage(
                    MessageType.PULL_ASK,
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
