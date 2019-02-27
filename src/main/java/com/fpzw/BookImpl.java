package com.fpzw;

import com.base.Book;
import com.base.Chapter;
import com.utils.OkHttp;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class BookImpl implements Book {
    private String mUrl;
    private String mTitle;
    private String mAuthor;
    private List<Chapter> mChapterList;

    /**
     * 构造函数
     */
    public BookImpl(String url, String title, String author) {
        mUrl = url;
        mTitle = title;
        mAuthor = author;
    }

    /**
     * 构造函数
     */
    public BookImpl(String title, String author, List<Chapter> chapterList) {
        mTitle = title;
        mAuthor = author;
        mChapterList = chapterList;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getAuthor() {
        return mAuthor;
    }

    @Override
    public List<Chapter> getChapters() {
        if (mChapterList == null) {
            List<Chapter> chapterList = new LinkedList<Chapter>();

            InputStream input = OkHttp.get(mUrl, null);
            if (input != null) {
                String webPage = Domain.readWebPage(input);
                if (webPage != null) {
                    collectChapters(webPage, chapterList);
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

            mChapterList = chapterList;
        }

        return mChapterList;
    }

    /**
     * 解析HTML，遍历页面中的章节
     */
    private void collectChapters(String webPage, List<Chapter> chapterList) {
        Document document = Parser.parse(webPage, mUrl);
        Element body = document.body();

        Element chapterElement = body.getElementsByClass("chapter").first();
        for (Element chapterInfoElement : chapterElement.getElementsByTag("a")) {
            String url = chapterInfoElement.absUrl("href");
            String title = chapterInfoElement.text();

            chapterList.add(new ChapterImpl(url, title));
        }

        /**
         * 注意：网站上列出的章节是逆序的，反转
         */
        Collections.reverse(chapterList);
    }
}
