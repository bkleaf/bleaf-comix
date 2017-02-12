package com.bleaf.comix.server.utillity;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by drg75 on 2017-02-12.
 */
public class FileTools {
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
            System.out.println("Detected encoding = " + encoding);
        } else {
            System.out.println("No encoding detected.");
        }

        detector.reset();

        return encoding;
    }
}
