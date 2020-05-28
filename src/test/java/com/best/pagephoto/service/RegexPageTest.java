package com.best.pagephoto.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegexPageTest {

    @Test
    void shouldReturnPage() {
        // given
        var regexPage = new RegexPage();
        var page = "https://stackoverflow.com/questions/42618872/regex-for-website-or-url-validation/42619368";

        // when
        var pageDomain = regexPage.getPageName(page);

        // then
        assertTrue(pageDomain.isPresent());
        assertEquals("stackoverflow", pageDomain.get());
    }

    @Test
    void shouldNotReturnPage() {
        // given
        var regexPage = new RegexPage();
        var page = "page";

        // when
        var pageDomain = regexPage.getPageName(page);

        // then
        assertFalse(pageDomain.isPresent());
    }
}