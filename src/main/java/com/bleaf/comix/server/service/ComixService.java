package com.bleaf.comix.server.service;

import com.bleaf.comix.server.configuration.ComixPathConfig;
import com.bleaf.comix.server.configuration.PathType;
import com.bleaf.comix.server.repository.ComixRepository;
import com.bleaf.comix.server.utillity.ComixTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Created by drg75 on 2017-02-12.
 */
@Service
@Slf4j
public class ComixService {
    @Autowired
    ComixPathConfig comixPathConfig;

    @Autowired
    ComixRepository comixRepository;

    @Autowired
    ComixTools comixTools;

    public String getPath(String path) {
//        if (isZip(path)) {
//            log.info("path = {} is zip!!!", path);
//            return null;
//        }

        Path requestPath = Paths.get(this.comixPathConfig.getDefaultRoot(), path);
        log.debug("server request path = {}", requestPath);

        PathType pathType = this.comixTools.getPathType(requestPath);
        log.debug("path type = {}", pathType);

        if(pathType == PathType.NONE) {
            //TODO 오류 메시지
            return "INVALID PATH";
        } else if(pathType == PathType.IMAGE) {
            //TODO image 처리
        } else if(pathType == PathType.ZIP
            || pathType == PathType.RAR
            || pathType == PathType.DIR) {

            List<List<String>> listBox =
                    comixRepository.getList(requestPath, pathType);
        }

//        Map<String, List<String>> listBox = comixRepository.getPath(path);
//
//
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append(comixTools.makeStringList(listBox.get("file")));
//        stringBuffer.append(comixTools.makeStringList(listBox.get("dir")));

        return null;
    }
}
