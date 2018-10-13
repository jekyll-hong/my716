package com.zhuishushenqi;

import com.demo.Book;
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
 * 搜书平台
 */
public class Platform {
    private static final String QUERY_BOOK = "http://api.zhuishushenqi.com/book/fuzzy-search?query=%s";

    /**
     * 根据关键字找书
     */
    public static List<Book> query(String keyWords) {
        List<Book> bookList = new ArrayList<Book>();

        InputStream input = OkHttp.get(String.format(QUERY_BOOK, keyWords), null);
        if (input != null) {
            try {
                JSONObject rootObj = new JSONObject(new JSONTokener(input));

                if (rootObj.getBoolean("ok")) {
                    JSONArray bookArray = rootObj.getJSONArray("books");

                    for (int i = 0; i < bookArray.length(); i++) {
                        JSONObject bookObj = bookArray.getJSONObject(i);
                        bookList.add(new BookImpl(bookObj));
                    }
                }
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

        return bookList;
    }

    /**
     * 构造函数（私有属性，不允许创建实例）
     */
    private Platform() {
        /**
         * nothing
         */
    }
}
