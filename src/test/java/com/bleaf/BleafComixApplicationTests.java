package com.bleaf;

import com.bleaf.comix.server.utillity.ComixTools;
import com.github.junrar.Archive;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BleafComixApplicationTests {

	@Test
	public void contextLoads() {
		String rarFile = "/Users/bleaf/Downloads/GREEN WORLDZ.rar";
		File f = new File(rarFile);

		Archive a = null;
		try {
			a = new Archive(new FileVolumeManager(f));

		} catch (Exception e) {
			e.printStackTrace();
		}

		if( a != null) {
			a.getMainHeader().print();
			FileHeader fh = a.nextFileHeader();

			while (fh != null) {
				try {
					log.debug("file header = {} : {}", getEncoding(fh.getFileNameString()), fh.getFileNameString() );
				} catch (IOException e) {
					e.printStackTrace();
				}
				fh = a.nextFileHeader();
			}
		}
	}

	public static String getEncoding(String path) throws IOException {
		UniversalDetector detector = new UniversalDetector(null);

		int nread = path.getBytes().length;
		detector.handleData(path.getBytes(), 0, nread);

		detector.dataEnd();

		String encoding = detector.getDetectedCharset();
		if (encoding != null) {
			log.debug("Detected encoding = " + encoding);
		} else {
			log.debug("No encoding detected.");
		}

		detector.reset();

		return encoding;
	}

}
