package com.bleaf.comix.server.configuration;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by bleaf on 2017. 2. 16..
 */

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "bleafcomix.config")
public class ComixPathConfig {
    private String defaultRoot;

    private List<String> imageType;

    @NestedConfigurationProperty
    private Map<String, List<String>> compressType;

    private List<String> excludeFile1;
    private List<String> excludeFile2;
    private List<String> includeFile;

    @PostConstruct
    public void init() {
        includeFile = Lists.newArrayList(imageType);

        String key;
        for(Iterator<String> iterator=compressType.keySet().iterator(); iterator.hasNext();) {
            key = iterator.next();
            includeFile.addAll(compressType.get(key));
        }

        log.info("create include file list = {}", includeFile);
    }
}
