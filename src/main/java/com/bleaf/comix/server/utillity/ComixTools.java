package com.bleaf.comix.server.utillity;

import com.bleaf.comix.server.configuration.ComixPathConfig;
import com.bleaf.comix.server.configuration.PathType;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by drg75 on 2017-02-12.
 */
@Slf4j
@Component
public class ComixTools {

    @Autowired
    ComixPathConfig comixPathConfig;

    public String makeStringList(List<String> list) {
        StringBuffer strList = new StringBuffer();

        for (String path : list) {
            strList.append(path).append("\n");
        }

        return strList.toString();
    }

    public String getEncoding(String path) throws IOException {
        byte[] buf = new byte[4096];
        java.io.FileInputStream fis = new FileInputStream(path);

        UniversalDetector detector = new UniversalDetector(null);

        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        detector.reset();

        return encoding;
    }

    public PathType getPathType(Path path) {
        if(Files.isDirectory(path)) {
            return PathType.DIR;
        } else {
            String fileName = path.getFileName().toString();
            String ext = com.google.common.io.Files.getFileExtension(fileName).toLowerCase();

            if(comixPathConfig
                    .getCompressType().get("zip")
                    .contains(ext)) {
                return PathType.ZIP;
            } else if(comixPathConfig
                    .getCompressType().get("rar")
                    .contains(ext)) {
                return PathType.RAR;
            } else if(comixPathConfig.getImageType().contains(ext)) {
                int count = path.getNameCount();

                // index 0 부터 시작, 마지막 path 앞의 path를 꺼냄.
                String checkPath = path.getName((count - 2)).toString();
                String ext2 = com.google.common.io.Files.getFileExtension(checkPath);
                if(ext2.equals("")) {
                    return PathType.IMAGE;
                } else if(comixPathConfig
                        .getCompressType().get("zip")
                        .contains(ext2)) {
                    return PathType.FILEINZIP;
                } else if(comixPathConfig
                        .getCompressType().get("rar")
                        .contains(ext2)) {
                    return PathType.FILEINRAR;
                }
            }
        }

        return PathType.NONE;
    }
}
