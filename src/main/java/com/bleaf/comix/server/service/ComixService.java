package com.bleaf.comix.server.service;

import com.bleaf.comix.server.configuration.ComixPathConfig;
import com.bleaf.comix.server.configuration.PathType;
import com.bleaf.comix.server.repository.ComixRepository;
import com.bleaf.comix.server.utillity.ComixTools;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
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
        Path requestPath = Paths.get(
                this.comixPathConfig.getDefaultRoot(), path);
        log.debug("server request path = {}", requestPath);

        PathType pathType = this.comixTools.getPathType(requestPath);
        log.debug("path type = {}", pathType);

        if(pathType == PathType.ZIP
            || pathType == PathType.RAR
            || pathType == PathType.DIR) {

            List<List<String>> listBox =
                    comixRepository.getList(requestPath, pathType);

            StringBuffer sb = new StringBuffer();
            for(int i=0; i < listBox.size(); i++) {
                if(i > 0) {
                    sb.append("\n");
                }

                sb.append(Joiner.on("\n").skipNulls().join(listBox.get(i)));
            }

            log.debug("request list = {}", sb.toString());

            return sb.toString();
        } else {
            //TODO 오류 메시지
            return "INVALID PATH";
        }
    }

    public InputStream getImage(String path) {
        Path requestPath = Paths.get(
                this.comixPathConfig.getDefaultRoot(), path);
        log.debug("server request path = {}", requestPath);

        PathType pathType = this.comixTools.getPathType(requestPath);
        log.debug("path type = {}", pathType);

        if(pathType == PathType.IMAGE
                || pathType == PathType.FILEINZIP
                || pathType == PathType.FILEINRAR) {

            try {
                InputStream in = comixRepository
                        .getImage(requestPath, pathType);
                if( in != null) {
                    return in;
                } else {
                    log.error("file not found = {}", requestPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.error("request type error !! = {}", requestPath);
        }

        return null;
    }
}
