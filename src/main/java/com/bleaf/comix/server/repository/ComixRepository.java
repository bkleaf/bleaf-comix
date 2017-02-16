package com.bleaf.comix.server.repository;

import com.bleaf.comix.server.configuration.ComixPathConfig;
import com.bleaf.comix.server.repository.filter.ComixFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Created by drg75 on 2017-02-12.
 */
@Slf4j
@Data
@Repository
@ConfigurationProperties(prefix = "bleafcomix.repository")
public class ComixRepository {
    @Autowired
    ComixPathConfig comixPathConfig;

    private String defaultRoot;

    /**
     * root 하위 1depth의 디렉토리 또는 file list를 돌려준다.
     *
     * @param root
     * @return
     */
    public Map<String, List<String>> getPath(String root) {
        log.debug("request Root Path = {}", root);
        List<String> fileList = Lists.newLinkedList();
        List<String> dirList = Lists.newLinkedList();

        Path direcotryPath = Paths.get(defaultRoot, root);
        if (Files.isDirectory(direcotryPath)) {
            try (DirectoryStream<Path> stream =
                         Files.newDirectoryStream(direcotryPath,
                                 new ComixFilter(comixPathConfig.getExcludeFile1(),
                                         comixPathConfig.getExcludeFile2(),
                                         comixPathConfig.getIncludeFile()))) {
                for (Path path : stream) {
                    if(Files.isDirectory(path)) {
                        dirList.add(path.toString());
                    } else {
                        fileList.add(path.toString());
                    }
                }
            } catch (IOException e) {
                log.error("io exception");
            }
        } else {
            fileList.add(direcotryPath.toString());
        }

        Map listBox = Maps.newHashMap();
        listBox.put("file", fileList);
        listBox.put("dir", dirList);

        return listBox;
    }
}

