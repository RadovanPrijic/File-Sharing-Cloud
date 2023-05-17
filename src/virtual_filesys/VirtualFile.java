package virtual_filesys;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VirtualFile implements Serializable {

    @Serial
    private static final long serialVersionUID = -3583109019856669382L;
    private boolean isDirectory;
    private String filePath;
    private byte[] fileData;
    private List<VirtualFile> files;

    public VirtualFile(String filePath, boolean isDirectory, byte[] fileData){
        this.filePath = filePath;
        this.isDirectory = isDirectory;
        this.fileData = fileData;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public List<VirtualFile> getFiles() {
        return files;
    }

    public void setFiles(List<VirtualFile> files) {
        this.files = files;
    }
}
