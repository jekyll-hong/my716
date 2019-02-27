package com.fpzw;

import com.base.Chapter;
import com.utils.OkHttp;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.InputStream;

class ChapterImpl implements Chapter {
    private String mUrl;
    private String mTitle;

    /**
     * 构造函数
     */
    public ChapterImpl(String url, String title) {
        mUrl = url;
        mTitle = title;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getContent() {
        StringBuffer contentBuffer = new StringBuffer();

        InputStream input = OkHttp.get(mUrl, null);
        if (input != null) {
            String webPage = Domain.readWebPage(input);
            if (webPage != null) {
                collectContent(webPage, contentBuffer);
            }
            else {
                /**
                 * read content error
                 */
            }
        }
        else {
            /**
             * access fail
             */
        }

        return contentBuffer.toString();
    }

    /**
     * 解析HTML，获取章节内容
     */
    private void collectContent(String webPage, StringBuffer contentBuffer) {
        Document document = Parser.parse(webPage, mUrl);
        Element body = document.body();

        Element contentElement = body.getElementById("nr");
        for (Element paragraphElement : contentElement.children()) {
            String innerHtml = paragraphElement.html();
            for (String section : innerHtml.split("<br>")) {
                contentBuffer.append(section.replaceAll("&nbsp;", ""));
            }
        }
    }
}
