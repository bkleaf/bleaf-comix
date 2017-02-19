package com.bleaf;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BleafComixApplicationTests {

    @Test
    public void contextLoads() {

		try {

//			SeekableInMemoryByteChannel

			FileInputStream fis = new FileInputStream("D:\\My Download\\cert.zip");
//			ZipArchiveInputStream zis = new ZipArchiveInputStream(fis, "EUC-KR", false);
			ZipArchiveInputStream zis = new ZipArchiveInputStream(fis);

			String name;
			ZipArchiveEntry entry;

			while((entry = zis.getNextZipEntry()) != null) {
                name = entry.getName();
                log.debug("zip name = {}", name);

                if(!entry.getName().equals("cert.pem")) continue;

                File outFile = new File("D:\\My Download\\cert.pem");
                outFile.createNewFile();

                FileOutputStream out = new FileOutputStream(outFile);
                BufferedOutputStream bos = new BufferedOutputStream(out);

                byte[] b = new byte[1024];
                int length=0;
                while((length = zis.read(b)) > 0) {
                    bos.write(b, 0, length);
                }


                bos.close();
                out.close();
            }



            zis.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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