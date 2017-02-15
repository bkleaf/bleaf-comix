package com.bleaf.comix.server.service;

import com.bleaf.comix.server.configuration.PathType;
import com.bleaf.comix.server.repository.ComixRepository;
import com.bleaf.comix.server.utillity.ComixTools;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by drg75 on 2017-02-12.
 */
@Service
@Slf4j
public class ComixService {
    @Autowired
    ComixRepository comixRepository;

    public String getPath(String path) {
        if (isZip(path)) {
            log.info("path = {} is zip!!!", path);
            return null;
        }

        Map<String, List<String>> listBox = comixRepository.getPath(path);


        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(ComixTools.makeStringList(listBox.get("file")));
        stringBuffer.append(ComixTools.makeStringList(listBox.get("dir")));


        log.debug("list = {}", stringBuffer);

        return stringBuffer.toString();
    }


    private PathType getPathType(String path) {



        return PathType.DIR;
    }


    private boolean isZip(String path) {
        String ext = Files.getFileExtension(path);

        String fileName = Files.getNameWithoutExtension(path);

        log.info("zip path = {} : {} : {}", path, fileName, ext);
        if (ext.equalsIgnoreCase("zip") ||
                ext.equalsIgnoreCase("cbz")) {


            return true;
        }

        return false;
    }

}
