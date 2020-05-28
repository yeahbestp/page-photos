package com.best.pagephoto.service;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegexPage {

    private static final String PAGE_PATTERN = "^(https?|ftp)\\:\\/{2}(w{3}[.])?(?<pageAddress>\\w*)\\.*.*";

     Optional<String> getPageName(String page){
        var pattern = Pattern.compile(PAGE_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(page);
        return matcher.find() ? Optional.of(matcher.group("pageAddress")) : Optional.empty();
    }
}
