package com.bleaf;

import com.bleaf.comix.server.utillity.ComixTools;
import com.github.junrar.Archive;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BleafComixApplicationTests {

	@Test
	public void contextLoads() {
		Path p = Paths.get("D:/My Download/GREEN WORLDZ.rar/1.jpg");

		log.debug("Path = {}", p.toString());

		int count = p.getNameCount();

		String p1 = p.getName(count - 2).toString();

		log.debug("path count = {} : {}", count, p1);


//		String rarFile = "D:/My Download/Yamato.Nadeshiko.DVDRip.x264.AC3-YYeTs_KOR.rar";
//		String rarFile = "D:/My Download/GREEN WORLDZ.rar";
//		File f = new File(rarFile);
//
//		Archive a = null;
//		try {
//			a = new Archive(new FileVolumeManager(f));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		if( a != null) {
//			a.getMainHeader().print();
//			FileHeader fh = a.nextFileHeader();
//
//			while (fh != null) {
//				try {
//					log.debug("file header = {} : {}", getEncoding(fh.getFileNameString()), fh.getFileNameString() );
//
//					String fileName = fh.getFileNameString();
//					String fileName2 = new String(fileName.getBytes(), Charset.forName("euc-kr"));
//
//					log.debug("encoding = {}", fileName2);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				fh = a.nextFileHeader();
//			}
//		}
	}

	public static String getEncoding(String path) throws IOException {
		UniversalDetector detector = new UniversalDetector(null);

		int nread = path.getBytes().length;
		detector.handleData(path.getBytes(), 0, nread);

		detector.dataEnd();

		String encoding = detector.getDetectedCharset();
		if (encoding != null) {
//			log.debug("Detected encoding = " + encoding);
		} else {
//			log.debug("No encoding detected.");
		}

		detector.reset();

		return encoding;
	}

}