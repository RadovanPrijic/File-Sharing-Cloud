package cli.command;

import app.AppConfig;
import app.ChordState;
import servent.message.MessageType;
import servent.message.QuitMessage;
import servent.message.util.MessageUtil;
import virtual_filesys.VirtualFileRepository;

public class QuitCommand implements CLICommand{

    @Override
    public String commandName() {
        return "quit";
    }

    @Override
    public void execute(String args) {
        if (args != null) {
            AppConfig.timestampedErrorPrint("The QUIT command must be without any arguments.");
            return;
        }

        if (AppConfig.chordState.getPredecessor() == null) {
            VirtualFileRepository.emptyTheWorkingDirectory();
            ChordState.quit(AppConfig.myServentInfo.getIpAddress(), AppConfig.myServentInfo.getListenerPort());
            System.exit(0);
        } else {
            String nextNodeIp = AppConfig.chordState.getNextNodeIp();
            int nextNodePort = AppConfig.chordState.getNextNodePort();
            QuitMessage message = new QuitMessage(
                    MessageType.QUIT,
                    AppConfig.myServentInfo.getIpAddress(),
                    nextNodeIp,
                    AppConfig.myServentInfo.getListenerPort(),
                    nextNodePort,
                    true,
                    VirtualFileRepository.virtualFilesMap);
            MessageUtil.sendMessage(message);
        }
    }
}
