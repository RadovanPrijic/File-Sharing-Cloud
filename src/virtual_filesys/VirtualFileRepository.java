package virtual_filesys;

import app.AppConfig;
//import org.apache.commons.io.FileUtils;
import servent.message.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VirtualFileRepository {

    public static Map<String, VirtualFile> virtualFilesMap = new ConcurrentHashMap<>();

    public static boolean addFileToVirtualFileSystem(MessageType msgType, VirtualFile file, String filePath) {
        if (msgType == MessageType.ADD) {
            if (virtualFilesMap.get(filePath) != null) {
                AppConfig.timestampedErrorPrint("File with the path " + filePath + " already exists in this virtual file repository and could therefore not be added.");
                return false;
            }
            saveFile(file);
            printVirtualFilesMap();
            AppConfig.timestampedStandardPrint("File with the path " + filePath + " has been added to this virtual file repository.");
        } else if (msgType == MessageType.PULL_ASK) {
            saveFile(file);
            printVirtualFilesMap();
            AppConfig.timestampedStandardPrint("File with the path " + filePath + " has been pulled to this virtual file repository.");
        }
        return true;
    }

    public static void printVirtualFilesMap(){
        AppConfig.timestampedStandardPrint("<----- Virtual file system of this node ----->");
        for (Map.Entry<String, VirtualFile> entry : virtualFilesMap.entrySet())
            AppConfig.timestampedStandardPrint("Virtual file with the path: " + entry.getKey());
        AppConfig.timestampedStandardPrint("<-------------------------------------------->");
    }

    static void saveFile(VirtualFile originalFile) {
        if (originalFile.isDirectory()) {
            virtualFilesMap.put(originalFile.getFilePath(), originalFile);

            for (VirtualFile file : originalFile.getFiles()) {
                if (file.isDirectory())
                    saveFile(file);
                else
                    virtualFilesMap.put(file.getFilePath(), file);
            }
        } else
            virtualFilesMap.put(originalFile.getFilePath(), originalFile);
    }

    public static VirtualFile pullFileFromVirtualFileSystem(String filePath) {
        VirtualFile fileToPull = virtualFilesMap.get(filePath);
        if (fileToPull == null) {
            AppConfig.timestampedErrorPrint("File with the path " + filePath + " does not exist in this virtual file repository and could therefore not be pulled.");
            return null;
        } else
            return fileToPull;
    }

    public static void removeFromMap(String filePath) {
        Set<String> pathHashSet = new HashSet<>();

        if (virtualFilesMap.get(filePath).isDirectory()) {
            for (Map.Entry<String, VirtualFile> entry : virtualFilesMap.entrySet()) {
                if (entry.getKey().startsWith(filePath)) {
                    pathHashSet.add(entry.getKey());
                }
            }
            for (String path : pathHashSet) {
                virtualFilesMap.remove(path);
            }
        } else
            virtualFilesMap.remove(filePath);

        printVirtualFilesMap();
        AppConfig.timestampedStandardPrint("File with the path " + filePath + " has been removed from this virtual file repository.");
    }

    public static void removeFile(String filePath) {
//        try {
//            FileUtils.forceDelete(new File(filePath));
//        } catch (IOException e) {
//            AppConfig.timestampedErrorPrint(e.getMessage());
//        }
    }

    public static void emptyTheWorkingDirectory() {
        File workDirectory = new File(AppConfig.WORKING_DIRECTORY);
        AppConfig.timestampedStandardPrint("Emptying the working directory and stopping ...");

        for (File f : workDirectory.listFiles())
            VirtualFileRepository.removeFile(AppConfig.WORKING_DIRECTORY + "/" + f.getName());
    }

    public static VirtualFile createVirtualFile(File file, String filePath) {
        VirtualFile virtualFile = null;

        if (file.isDirectory()) {
            List<VirtualFile> dirFiles = new ArrayList<>();

            for (File f : file.listFiles()) {
                if (f.isDirectory()) {
                    virtualFile = createVirtualFile(f, filePath + "/" + f.getName());
                    dirFiles.add(virtualFile);
                }
                else {
                    try {
                        virtualFile = new VirtualFile(filePath + "/" + f.getName(), false, readFileToBytes(f));
                        dirFiles.add(virtualFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            virtualFile = new VirtualFile(filePath, true, null);
            virtualFile.setFiles(dirFiles);
        }
        else {
            try {
                virtualFile = new VirtualFile(filePath, false, readFileToBytes(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return virtualFile;
    }

    public static Map<String, VirtualFile> getVirtualFilesMap() {
        return virtualFilesMap;
    }

    public static void setVirtualFilesMap(Map<String, VirtualFile> virtualFilesMap) {
        VirtualFileRepository.virtualFilesMap = virtualFilesMap;
    }

    private static byte[] readFileToBytes(File file) throws IOException {
        byte[] bytes = new byte[(int) file.length()];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(bytes);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return bytes;
    }
}
