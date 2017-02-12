package com.bleaf.comix.server.repository;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by drg75 on 2017-02-12.
 */
@Slf4j
@Data
@Repository
@ConfigurationProperties(prefix = "bleafcomix")
public class ComixRepository {

    private final static String WINDOW_DEFAULT_DRIVE="D:";

    public List<String> exclude = Lists.newArrayList();

    /**
     * root 하위 1depth의 디렉토리 또는 file list를 돌려준다.
     *
     * @param root
     * @return
     */
    public List<String> getPath(String root) {
        log.trace("request Root Path = {}", root);
        List<String> list = Lists.newLinkedList();

        Path direcotryPath = Paths.get(WINDOW_DEFAULT_DRIVE, root);
        if (Files.isDirectory(direcotryPath)) {
            try (DirectoryStream<Path> stream =
                         Files.newDirectoryStream(direcotryPath)) {
                for (Path path : stream) {
                    log.debug("path = {} : {}", Files.isRegularFile(path), path);
                    list.add(path.toString());
                }
            } catch (IOException e) {
                log.error("io exception");
            }
        } else {
            list.add(direcotryPath.toString());
        }

        for(String s : exclude) {
            log.info("text ex = {} ", s);

        }

        return list;
    }

    public boolean isSupport(String path) {
        return false;
    }
}