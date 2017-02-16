package com.bleaf.comix.server.utillity;

import com.bleaf.comix.server.configuration.PathType;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by drg75 on 2017-02-12.
 */
@Slf4j
public class ComixTools {
    public static String makeStringList(List<String> list) {
        StringBuffer strList = new StringBuffer();

        for (String path : list) {
            strList.append(path).append("\n");
        }

        return strList.toString();
    }

    public static String getEncoding(String path) throws IOException {
        byte[] buf = new byte[4096];
        java.io.FileInputStream fis = new FileInputStream(path);

        UniversalDetector detector = new UniversalDetector(null);

        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            log.debug("Detected encoding = " + encoding);
        } else {
            log.debug("No encoding detected.");
        }

        detector.reset();

        return encoding;
    }

    public static PathType getPathType(Path path) {
        return PathType.DIR;
    }
}
