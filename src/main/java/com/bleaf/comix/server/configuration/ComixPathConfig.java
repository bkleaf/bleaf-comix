package com.bleaf.comix.server.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Created by bleaf on 2017. 2. 16..
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "bleafcomix.config")
public class ComixPathConfig {
    @NestedConfigurationProperty
    private Map<String, List<String>> compressType;

    private List<String> excludeFile1;
    private List<String> excludeFile2;
    private List<String> includeFile;
}
