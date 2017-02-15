package com.bleaf.comix.server.repository.filter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by drg75 on 2017-02-15.
 */
@Slf4j
@AllArgsConstructor
public class ComixFilter implements DirectoryStream.Filter<Path> {
    private List<String> excludeFile1;
    private List<String> excludeFile2;
    private List<String> includeFile;

    @Override
    public boolean accept(Path entry) throws IOException {
        log.debug("entry filename = {}", entry.getFileName());
        if (entry.getFileName().toString().startsWith(".")) {
            return false;
        }

        if (excludeFile1.contains(entry.getFileName().toString())) {
            log.debug("exclude file1 = {}", entry.toString());
            return false;
        }

        for (String exc : excludeFile2) {
            if (entry.getFileName().toString().indexOf(exc) != -1) {
                log.debug("exclude file2 = {}", entry.toString());
                return false;
            }
        }

        if (Files.isRegularFile(entry)) {
            String ext = com.google.common.io.Files.getFileExtension(entry.getFileName().toString());
            if (ext == null) {
                log.debug("ext is null = {}", entry.toString());
                return false;
            }

            if (includeFile.contains(ext)) {
                return true;
            }
        }

        return true;
    }
}
