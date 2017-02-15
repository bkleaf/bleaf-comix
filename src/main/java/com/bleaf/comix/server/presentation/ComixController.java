package com.bleaf.comix.server.presentation;

import com.bleaf.comix.server.service.ComixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by drg75 on 2017-02-12.
 */
@Slf4j
@RestController
@RequestMapping("/bleafcomix/")
public class ComixController {
    @Autowired
    ComixService comixService;

    @GetMapping("/**")
    public String comix(final HttpServletRequest request) {
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
