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

//    String str = new String("헬로월드!");
//
//    Charset eucKRCharset = Charset.forName("EUC-KR");
//    CharBuffer sourceBuffer = CharBuffer.wrap(str.toCharArray());
//    ByteBuffer resultByteBuffer = eucKRCharset.encode(sourceBuffer);
//    byte[] resultBytes =  resultByteBuffer.array();
//// EUC-KR 의 String 을 생성할 때, 두번째 인자값으로 인코딩 정보를 넣어준다.
//System.out.println(new String(resultBytes, eucKRCharset));
//// 만약 인코딩 정보를 넣지 않는다면 에러 스트링이(�, 0xfffd) 이 출력될 것이다.
//System.out.println(new String(resultBytes));
//
//    // 원래의 UTF-8 로 디코딩.
//    CharBuffer charBuffer = eucKRCharset.decode(ByteBuffer.wrap(resultBytes));
//System.out.println(charBuffer.toString());

    /**
     * 확장자를 검사한 후 ".zip" 또는 ".cbz"인지를 재검사한다
     * @param fileName : "zetman.zip" or "zetman 01.jpg"
     * @return
     */
    public boolean isZip(String fileName) {
        String ext = com.google.common.io.Files.getFileExtension(fileName).toLowerCase();
        for(String zip : this.comixPathConfig.getZipType()) {
            if(ext.equals(zip)) {
                return fileName.endsWith("." + zip);
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
            if(ext.equals(rar)) {
                return fileName.endsWith("." + rar);
            }
        }

        return false;
    }
}
