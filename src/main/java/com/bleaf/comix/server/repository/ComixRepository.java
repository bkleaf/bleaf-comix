package com.bleaf.comix.server.repository;

import com.bleaf.comix.server.configuration.ComixPathConfig;
import com.bleaf.comix.server.configuration.PathType;
import com.bleaf.comix.server.repository.filter.ComixFilter;
import com.bleaf.comix.server.utillity.ComixTools;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by drg75 on 2017-02-12.
 */
@Slf4j
@Data
@Repository
public class ComixRepository {
    @Autowired
    ComixPathConfig comixPathConfig;

    @Autowired
    ComixTools comixTools;

    public List<List<String>> getList(Path requestPath, PathType pathType) {
        log.debug("request Root Path = {}", requestPath);

        List<List<String>> list = null;
        if (pathType == PathType.DIR) {
            return getDirecotryList(requestPath);
        } else if (pathType == PathType.ZIP) {
            list = Lists.newArrayList();
            list.add(this.getZipList(requestPath));

            return list;
        } else if (pathType == PathType.RAR) {
            list = Lists.newArrayList();
            list.add(this.getRarList(requestPath));

            return list;
        }

        return null;
    }

    public InputStream getImage(Path requestPath, PathType pathType) throws Exception {
        if (pathType == PathType.IMAGE) {
            File file = requestPath.toFile();

            if (file.isFile() && file.exists()) {
                return new FileInputStream(file);
            }
        } else if (pathType == PathType.FILEINZIP) {
            Path zipPath = comixTools.getCompressPath(requestPath);
            return this.getImageInZip(zipPath, requestPath.getFileName().toString());

        } else if (pathType == PathType.FILEINRAR) {
            Path rarPath = comixTools.getCompressPath(requestPath);
            return this.getImageInRar(rarPath, requestPath.getFileName().toString());
        }

        return null;
    }

    public InputStream getImageInZip(Path zipPath, String fileName) {

        log.debug("get image in zip = {} : {}", fileName, zipPath);

        try (FileInputStream fis = new FileInputStream(zipPath.toFile());
             ZipArchiveInputStream zis = new ZipArchiveInputStream(fis);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            String entryName;
            ZipArchiveEntry entry;
            while ((entry = zis.getNextZipEntry()) != null) {
                entryName = entry.getName();
                log.debug("zip file entry name = {}", entryName);

                if (entryName.toLowerCase().equals(fileName)) {
                    byte[] b = new byte[1024];
                    int length = 0;
                    while ((length = zis.read(b)) > 0) {
                        baos.write(b, 0, length);
                    }

                    return new ByteArrayInputStream(baos.toByteArray());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public InputStream getImageInRar(Path rarPath, String fileName) {
        log.debug("get image in rar = {} : {}", fileName, rarPath);

        File f = rarPath.toFile();

        Archive archive = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            archive = new Archive(new FileVolumeManager(f));
            if (archive != null) {
                FileHeader fh;
                String entryName;
                while ((fh = archive.nextFileHeader()) != null) {
                    entryName = this.comixTools.convertCharset(fh.getFileNameString());

                    if (entryName.toLowerCase().equals(fileName)) {
                        log.debug("entry name = {} : {}", entryName, fileName);
                        archive.extractFile(fh, baos);

                        return new ByteArrayInputStream(baos.toByteArray());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<List<String>> getDirecotryList(Path requestPath) {
        log.debug("request path = ", requestPath);

        List<String> fileList = Lists.newLinkedList();
        List<String> dirList = Lists.newLinkedList();

        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(requestPath,
                             new ComixFilter(comixPathConfig.getExcludeFile1(),
                                     comixPathConfig.getExcludeFile2(),
                                     comixPathConfig.getIncludeFile()))) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    dirList.add(path.getFileName().toString());
                } else {
                    fileList.add(path.getFileName().toString());
                }
            }
        } catch (IOException e) {
            log.error("io exception");
        }

        return Lists.newArrayList(dirList, fileList);
    }

    public List<String> getZipList(Path requestPath) {
        List<String> list = Lists.newArrayList();
        try (FileInputStream fis = new FileInputStream(requestPath.toFile());
             ZipArchiveInputStream zis = new ZipArchiveInputStream(fis)) {

            String entryName;
            ZipArchiveEntry entry;

            while ((entry = zis.getNextZipEntry()) != null) {
                entryName = entry.getName();
                list.add(entryName);

                log.debug("file in zip entry name = {}", entryName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getRarList(Path requestPath) {
        File f = requestPath.toFile();

        Archive archive = null;
        try {
            archive = new Archive(new FileVolumeManager(f));
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> list = null;
        if (archive != null) {
            list = Lists.newLinkedList();
//			archive.getMainHeader().print();

            FileHeader fh;
            String entryName;
            while ((fh = archive.nextFileHeader()) != null) {
                entryName = this.comixTools.convertCharset(fh.getFileNameString());
                list.add(entryName);

                log.debug("file in rar entry name = {}", entryName);
            }
        }

        return list;
    }
}

