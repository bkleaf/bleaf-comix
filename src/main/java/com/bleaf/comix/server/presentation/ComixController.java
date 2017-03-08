package com.bleaf.comix.server.presentation;

import com.bleaf.comix.server.configuration.ComixPathConfig;
import com.bleaf.comix.server.service.ComixService;
import com.bleaf.comix.server.utillity.ComixTools;
import com.google.common.net.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by drg75 on 2017-02-12.
 */
@Slf4j
@RestController
public class ComixController {
    @Autowired
    ComixService comixService;

    @Autowired
    ComixTools comixTools;

    @Autowired
    ComixPathConfig comixPathConfig;

    @RequestMapping(path = "/")
    public String root() {
        return comixPathConfig.getDefaultPath();
    }

    @RequestMapping({
            "/comix/**/*.jpg",
            "/comix/**/*.gif",
            "/comix/**/*.png",
            "/comix/**/*.tif",
            "/comix/**/*.bmp",
            "/comix/**/*.jpeg",
            "/comix/**/*.tiff"})
    public void image(final HttpServletRequest request,
                      final HttpServletResponse response) {
        String path = this.getMatchPath(request);
        log.debug("request image path = {}", path);

        InputStream in = comixService.getImage(path);

        if(in == null) return;

        MediaType mediaType = comixTools.getMediaType(
                com.google.common.io.Files.getFileExtension(path));

        log.info("media type = {}", mediaType);

        response.setContentType(mediaType.toString());
        try {
            IOUtils.copy(in, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @RequestMapping({"/comix", "/comix/**/*"})
    @ResponseBody
    public String comix(final HttpServletRequest request,
                        final HttpServletResponse response) {
        String path = this.getMatchPath(request);
        log.info("request path = {}", path);

        return comixService.getPath(path);
    }


    private String getMatchPath(final HttpServletRequest request) {
        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        log.trace("request path = {}", path);

        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        log.trace("match = {}", bestMatchPattern);

        AntPathMatcher apm = new AntPathMatcher();
        String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);

        log.trace("final path = {}", finalPath);

        return finalPath;
    }
}
