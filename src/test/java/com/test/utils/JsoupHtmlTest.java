package com.test.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

/**
 * Jsoup简单名了的获取html网页数据，分析
 * Created by admin on 2017/1/20.
 */
public class JsoupHtmlTest {
    @Test
    public void htmlTest() throws IOException {
        Document document = Jsoup.connect("http://www.baidu.com/s").data("wd", "a").get();
        System.out.println(document.html());
        Elements elements = document.getElementsByTag("a");
        for (Element e : elements) {
            System.out.println(e.attr("href"));
        }
    }
}
