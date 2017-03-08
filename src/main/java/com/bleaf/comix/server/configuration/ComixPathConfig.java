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
@ConfigurationProperties(prefix = "comix.config.path")
public class ComixPathConfig {
    private String defaultPath;

    private String defaultRoot;

    private List<String> imageType;

    private List<String> zipType;
    private List<String> rarType;

    private List<String> excludeFile1;
    private List<String> excludeFile2;
    private List<String> includeFile;

    @PostConstruct
    public void init() {
        includeFile = Lists.newArrayList(imageType);
        includeFile.addAll(zipType);
        includeFile.addAll(rarType);

        log.info("create include file list = {}", includeFile);
    }
}
