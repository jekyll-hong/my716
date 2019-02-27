package com.zhuishushenqi;

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
 * 追书神器
 */
public class PlatformImpl implements Platform {
    private static final String QUERY_BOOK = "http://api.zhuishushenqi.com/book/fuzzy-search?query=%s";

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
}
