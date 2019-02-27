package com.fpzw;

import com.base.Book;
import com.base.Chapter;
import com.base.Platform;
import com.utils.OkHttp;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2K小说
 */
public class PlatformImpl implements Platform {
    private static final String SEARCH_ACTION = "modules/article/search.php";
    private static final String QUERY_PARAMETERS = "?searchtype=keywords&searchkey=%s&submit=";

    private static final Pattern REG_PAGE_INFO = Pattern.compile("\\d+/\\d+");

    /**
     * 构造函数
     */
    public PlatformImpl() {
        /**
         * nothing
         */
    }

    /**
     * 生成搜索url
     */
    private String makeQueryUrl(String keyWords) {
        return Domain.getURL() + SEARCH_ACTION + String.format(QUERY_PARAMETERS, keyWords);
    }

    @Override
    public List<Book> query(String keyWords) {
        List<Book> bookList = new ArrayList<Book>();

        try {
            String queryUrl = makeQueryUrl(URLEncoder.encode(keyWords, "gbk"));
            int totalPages = 1;

            InputStream input = OkHttp.get(queryUrl, null);
            if (input != null) {
                String webPage = Domain.readWebPage(input);
                if (webPage != null) {
                    totalPages = collectBooks(webPage, bookList);
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

            /**
             * 搜索结果超过一页
             */
            for (int i = 2; i < totalPages; i++) {
                String queryTargetPageUrl = queryUrl.concat(String.format("&page=%d", i));

                input = OkHttp.get(queryTargetPageUrl, null);
                if (input != null) {
                    String webPage = Domain.readWebPage(input);
                    if (webPage != null) {
                        collectBooks(webPage, bookList);
                    }
                    else {
                        /**
                         * read content error
                         */
                        break;
                    }
                }
                else {
                    /**
                     * access fail
                     */
                    break;
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            /**
             * ignore
             */
        }

        return bookList;
    }

    /**
     * 解析HTML，遍历页面中的书籍
     */
    private static int collectBooks(String webPage, List<Book> bookList) {
        int totalPages = 1;

        Document document = Parser.parse(webPage, "");
        Element head = document.head();
        Element body = document.body();

        Element titleElement = head.getElementsByTag("title").first();
        if (titleElement.text().contains("搜索结果")) {
            /**
             * 搜索结果
             */
            for (Element bookElement : body.getElementsByClass("hot_sale")) {
                Element bookInfoElement = bookElement.getElementsByTag("a").first();

                String url = Domain.getURL().concat(bookInfoElement.attr("href"));
                String title = bookInfoElement.getElementsByClass("title").first().text();

                String author = bookInfoElement.getElementsByClass("author").first().text();
                String[] results = author.split("：");
                if (results.length == 2) {
                    author = results[1];
                }

                bookList.add(new BookImpl(url, title, author));
            }

            Elements pageElements = body.getElementsByClass("page");
            if (pageElements.size() > 0) {
                Matcher matcher = REG_PAGE_INFO.matcher(pageElements.last().text());
                if (matcher.find()) {
                    String[] results = matcher.group().split("/");
                    totalPages = Integer.parseInt(results[1]);
                }
            }
        }
        else {
            /**
             * 直接匹配，跳转到了书籍页面
             */
            String url = "";
            String title = "";
            String author = "";
            List<Chapter> chapterList = new LinkedList<Chapter>();

            /**
             * 从头部获取书籍信息
             */
            for (Element metaElement : head.getElementsByTag("meta")) {
                Attributes attributes = metaElement.attributes();
                if (attributes.hasKey("property")) {
                    String property = attributes.get("property");
                    if (property.equals("og:novel:book_name")) {
                        title = attributes.get("content");
                    }
                    else if (property.equals("og:novel:author")) {
                        author = attributes.get("content");
                    }
                    else if (property.equals("og:url")) {
                        url = attributes.get("content");
                    }
                }
            }

            Element chapterElement = body.getElementsByClass("chapter").first();
            for (Element chapterInfoElement : chapterElement.getElementsByTag("a")) {
                String chapterUrl = url.concat(chapterInfoElement.attr("href"));
                String chapterTitle = chapterInfoElement.text();

                chapterList.add(new ChapterImpl(chapterUrl, chapterTitle));
            }

            /**
             * 注意：网站上列出的章节是逆序的，反转
             */
            Collections.reverse(chapterList);

            bookList.add(new BookImpl(title, author, chapterList));
        }

        return totalPages;
    }
}
