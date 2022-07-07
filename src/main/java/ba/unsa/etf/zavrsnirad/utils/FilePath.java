package ba.unsa.etf.zavrsnirad.utils;

import java.util.Objects;

public enum FilePath {
    JRXML_SRC_FILE_NAME("tmp.jrxml"),
    JAPSER_SRC_FILE_NAME("tmp.jasper"),
    HMTL_DEST_FILE_NAME("tmp.html"),
    JRXML_DESC_FILE_NAME("destination_tmp.jrxml"),
    FINAL_FILE_NAME("final_file.jrxml");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getFilePath() {
        return path;
    }

    public static String getDirectory() {
        return Objects.requireNonNull(FilePath.class.getResourceAsStream("/tmp/")).toString();
    }

    public String getFullPath() {
        return  getDirectory() + "/" + path;
    }

}
