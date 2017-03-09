package com.bleaf.comix.server.utillity;

import com.bleaf.comix.server.configuration.ComixPathConfig;
import com.bleaf.comix.server.configuration.PathType;
import com.google.common.base.Splitter;
import com.google.common.net.MediaType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by drg75 on 2017-02-12.
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "comix.config.tools")
public class ComixTools {

    @Autowired
    ComixPathConfig comixPathConfig;

    @Setter
    String encoding;
    @Setter
    String decoding;

    Charset encodingCharset;
    Charset decodingCharset;

    boolean isCharConvert = false;

    @PostConstruct
    public void init() {
        if(!(encoding.equals(decoding))) {
            isCharConvert = true;

            encodingCharset = Charset.forName(encoding);
            decodingCharset = Charset.forName(decoding);
        }
    }

    public String makeStringList(List<String> list) {
        StringBuffer strList = new StringBuffer();

        for (String path : list) {
            strList.append(path).append("\n");
        }

        return strList.toString();
    }

    public String getCharsetStr(String path) {
        byte[] buf = new byte[4096];

        UniversalDetector detector = new UniversalDetector(null);
        try (FileInputStream fis = new FileInputStream(path)) {
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
        } catch(IOException e) {
            e.printStackTrace();
        }

        String encoding = detector.getDetectedCharset();
        detector.reset();

        return encoding;
    }

    public PathType getPathType(Path path) {
        if(Files.isDirectory(path)) {
            return PathType.DIR;
        } else {
            String fileName = path.getFileName().toString().toLowerCase();

            if(this.isZip(fileName)) {
                return PathType.ZIP;
            } else if(this.isRar((fileName))) {
                return PathType.RAR;
            } else {
                String ext = com.google.common.io.Files.getFileExtension(fileName);
                if(comixPathConfig.getImageType().contains(ext)) {
                    int count = path.getNameCount();
                    // index 0 부터 시작, 마지막 path 앞의 path를 꺼냄.
                    String checkPath = path.getName((count - 2)).toString();
                    String ext2 = com.google.common.io.Files.getFileExtension(checkPath);
                    if(ext2.equals("")) {
                        return PathType.IMAGE;
                    } else if(this.isZip(checkPath)) {
                        return PathType.FILEINZIP;
                    } else if(this.isRar(checkPath)) {
                        return PathType.FILEINRAR;
                    }
                }
            }
        }

        return PathType.NONE;
    }

    public Path getCompressPath(Path path) {
        log.debug("full path = {}", path);
        List<String> sp = Splitter
                .on(File.separator)
                .splitToList(path.toString());

        int idx=0;
        for(String s : sp) {
            if(this.isZip(s) || this.isRar(s)) {
                break;
            }

            idx++;
        }

        Path p = path.subpath(0, idx);
        log.debug("subPath path = {} : {}", path.getRoot(),  p);

        return Paths.get(path.getRoot().toString(), p.toString());
    }

    public String convertCharset(String str) {
        if(!isCharConvert) return str;

        log.debug("origin string = {}", str);

        CharBuffer sourceBuffer = CharBuffer.wrap(str.toCharArray());
        ByteBuffer resultByteBuffer = decodingCharset.encode(sourceBuffer);
        byte[] resultBytes =  resultByteBuffer.array();

        String convertStr = new String(resultBytes, decodingCharset);
        log.debug("convert str = {}", convertStr);

        // 원래의 UTF-8 로 디코딩.
        //CharBuffer charBuffer = eucKRCharset.decode(ByteBuffer.wrap(resultBytes));

        return convertStr;
    }

    public boolean isImage(String fileName) {
        String ext = com.google.common.io.Files.getFileExtension(fileName).toLowerCase();
        for(String img : this.comixPathConfig.getImageType()) {
            if(ext.equalsIgnoreCase(img)
                    && fileName.toLowerCase().endsWith(("." + img))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 확장자를 검사한 후 ".zip" 또는 ".cbz"인지를 재검사한다
     * @param fileName : "zetman.zip" or "zetman 01.jpg"
     * @return
     */
    public boolean isZip(String fileName) {
        String ext = com.google.common.io.Files.getFileExtension(fileName).toLowerCase();
        for(String zip : this.comixPathConfig.getZipType()) {
            if(ext.equalsIgnoreCase(zip)
                    && fileName.toLowerCase().endsWith(("." + zip))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 확장자를 검사한 후 ".rar" 또는 ".cbr" 인지를 재검사한다
     * @param fileName : "zetman.rar" or "zetman 01.jpg"
     * @return
     */
    public boolean isRar(String fileName) {
        String ext = com.google.common.io.Files.getFileExtension(fileName).toLowerCase();
        for(String rar : this.comixPathConfig.getRarType()) {
            if(ext.equalsIgnoreCase(rar)
                    && fileName.toLowerCase().endsWith(("." + rar))) {
                return true;
            }
        }

        return false;
    }

    public MediaType getMediaType(String fileName) {
        String ext =
                com.google.common.io.Files.getFileExtension(fileName)
                        .toLowerCase();
        switch(ext) {
            case "jpg" :
                return MediaType.JPEG;
            case "jpeg" :
                return MediaType.JPEG;
            case "gif" :
                return MediaType.GIF;
            case "png" :
                return MediaType.PNG;
            case "tiff" :
                return MediaType.TIFF;
            case "tif" :
                return MediaType.TIFF;
            case "bmp" :
                return MediaType.BMP;
            default :
                return MediaType.JPEG;
        }
    }
}
