package com.sese.translator.web.rest.parsing;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.sese.translator.domain.Language;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class UploadedFile {

    private static final Logger log = LoggerFactory.getLogger(UploadedFile.class);

    private final MultipartFile file;
    private final String fileName;
    private final String languageCode;
    private final String path;
    private CharsetMatch detect;

    public UploadedFile(MultipartFile file, String path) {
        this.file = file;
        fileName = file.getOriginalFilename();
        this.path = path;
        languageCode = detectLanguageCode();
        log.info("Detected language code: {}. {}", languageCode, this);
    }

    public MultipartFile getFile() {
        return file;
    }

    public String getPath() {
        return path;
    }

    public byte[] getBytes() throws IOException {
        return file.getBytes();
    }

    public String getFilename() {
        return fileName;
    }

    public Reader getReader() throws IOException {
        if (detect == null) {
            detect = new CharsetDetector().setText(getBytes()).detect();
        }
        return detect.getReader();
    }

    public String getLanguageCode() {
        return languageCode;
    }

    private String detectLanguageCode() {
        if (!path.isEmpty()) {
            Path filePath = Paths.get(path);
            for (Path pathPart : filePath) {
                String pathString = pathPart.toString();
                if (isIOSFile()) {
                    if (pathString.endsWith(".lproj")) {
                        return pathString.substring(0, pathString.indexOf(".lproj"));
                    }
                } else if (isAndroidFile()) {
                    if (pathString.startsWith("values-")) {
                        return pathString.substring(pathString.indexOf("-") + 1);
                    } else if (pathString.equals("values")) {
                        return Language.DEFAULT_LANGUAGE_CODE_ENGLISH;
                    }
                }
            }
            return filePath.getFileName().toString();
        } else {
            return FilenameUtils.getBaseName(fileName);
        }
    }

    public boolean isIOSFile() {
        return fileName.endsWith(".strings");
    }

    public boolean isAndroidFile() {
        return fileName.endsWith(".xml");
    }

    public boolean isWebFile() {
        return fileName.endsWith(".json");
    }

    public boolean isEnglishLanguageCode() {
        return Language.DEFAULT_LANGUAGE_CODE_ENGLISH.equalsIgnoreCase(languageCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UploadedFile)) return false;

        UploadedFile that = (UploadedFile) o;

        if (!file.equals(that.file)) return false;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        int result = file.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FileAndPath{" +
            "fileName='" + fileName + '\'' +
            ", languageCode='" + languageCode + '\'' +
            ", path='" + path + '\'' +
            '}';
    }
}
