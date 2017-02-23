package com.bleaf.comix.server.repository;

import com.bleaf.comix.server.configuration.ComixPathConfig;
import com.bleaf.comix.server.configuration.PathType;
import com.bleaf.comix.server.repository.filter.ComixFilter;
import com.bleaf.comix.server.utillity.ComixTools;
import com.github.junrar.Archive;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

    @Autowired
    ComixTools comixTools;

    String encoding;
    String decoding;

    public List<List<String>> getList(Path requestPath, PathType pathType) {
        log.debug("request Root Path = {}", requestPath);

        List<List<String>> list = null;
        if(pathType == PathType.DIR) {
            return getDirecotryList(requestPath);
        } else if(pathType == PathType.ZIP) {
            list = Lists.newArrayList();
            list.add(this.getZipList(requestPath));

            return list;
        } else if(pathType == PathType.RAR) {
            list = Lists.newArrayList();
            list.add(this.getRarList(requestPath));

            return list;
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
                    dirList.add(path.toString());
                } else {
                    fileList.add(path.toString());
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
                ZipArchiveInputStream zis  = new ZipArchiveInputStream(fis)) {

            String entryName;
            ZipArchiveEntry entry;


            while((entry = zis.getNextZipEntry()) != null) {
                entryName = entry.getName();
                log.debug("zip file entry name = {}", entryName);

                list.add(entryName);




//                    FileOutputStream out = new FileOutputStream(outFile);
//                    BufferedOutputStream bos = new BufferedOutputStream(out);
//
//                    byte[] b = new byte[1024];
//                    int length = 0;
//                    while ((length = zis.read(b)) > 0) {
//                        bos.write(b, 0, length);
//                    }
//
//
//                    bos.close();
//                    out.close();
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
		if( archive != null) {
		    list = Lists.newLinkedList();
//			archive.getMainHeader().print();

            FileHeader fh;
            String entryName;
			while ((fh = archive.nextFileHeader()) != null) {
				try {
					log.debug("file header = {} : {}",
                            this.comixTools.getCharsetStr(fh.getFileNameString()),
                            fh.getFileNameString() );

					entryName = this.comixTools.convertCharset(fh.getFileNameString());
					log.debug("fileheader encoding = {}", entryName);

                    list.add(entryName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return list;
    }
}

