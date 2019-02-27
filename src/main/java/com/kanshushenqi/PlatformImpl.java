package com.kanshushenqi;

import com.base.Book;
import com.base.Platform;
import com.utils.OkHttp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 看书神器
 */
public class PlatformImpl implements Platform {
    private static final String QUERY_BOOK = Domain.URL + "/Search.aspx?isSearchPage=1&key=%s&page=%d";

    private static final int BOOK_PER_PAGE = 10;

    /**
     * 构造函数
     */
    public PlatformImpl() {
        /**
         * nothing
         */
    }

    /**
     * 根据关键字找书
     */
    public List<Book> query(String keyWords) {
        List<Book> bookList = new ArrayList<Book>();

        int pageNumber = 1;
        while (queryOnePage(keyWords, pageNumber, bookList)) {
            pageNumber++;
        }

        return bookList;
    }

    /**
     * 指定结果页（一页有10个结果）
     */
    private static boolean queryOnePage(String keyWords, int pageNumber, List<Book> bookList) {
        boolean nextPage = false;

        InputStream input = OkHttp.get(String.format(QUERY_BOOK, keyWords, pageNumber), null);
        if (input != null) {
            try {
                JSONObject rootObj = new JSONObject(new JSONTokener(input));

                JSONArray bookArray = rootObj.getJSONArray("data");
                for (int i = 0; i < bookArray.length(); i++) {
                    JSONObject bookObj = bookArray.getJSONObject(i);
                    bookList.add(new BookImpl(bookObj));
                }

                nextPage = bookArray.length() == BOOK_PER_PAGE;
            }
            catch (JSONException e) {
                /**
                 * 解析异常
                 */
            }
            finally {
                try {
                    input.close();
                }
                catch (IOException e) {
                    /**
                     * ignore
                     */
                }
            }
        }

        return nextPage;
    }
}
