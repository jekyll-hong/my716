package com.kanshushenqi;

import com.base.Book;
import com.base.Chapter;
import com.utils.OkHttp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

class BookImpl implements Book {
    private static final String QUERY_CHAPTER = Domain.URL + "/book/%s/";

    private JSONObject mBookObj;

    /**
     * 构造函数
     */
    public BookImpl(JSONObject bookObj) {
        mBookObj = bookObj;
    }

    @Override
    public String getTitle() {
        return mBookObj.getString("Name");
    }

    @Override
    public String getAuthor() {
        return mBookObj.getString("Author");
    }

    @Override
    public List<Chapter> getChapters() {
        List<Chapter> chapterList = new LinkedList<Chapter>();

        InputStream input = OkHttp.get(String.format(QUERY_CHAPTER, getId()), null);
        if (input != null) {
            try {
                /**
                 * 1. 文本头有BOM
                 * 2. json格式不正确，手动修正
                 */
                String strJson = readString(input).replaceAll("},]", "}]");
                JSONObject rootObj = new JSONObject(strJson);

                JSONObject dataObj = rootObj.getJSONObject("data");

                JSONArray chapterArray = dataObj.getJSONArray("list");
                for (int i = 0; i < chapterArray.length(); i++) {
                    JSONObject chapterObj = chapterArray.getJSONObject(i);

                    JSONArray subChapterArray = chapterObj.getJSONArray("list");
                    for (int j = 0; j < subChapterArray.length(); j++) {
                        JSONObject subChapterObj = subChapterArray.getJSONObject(j);
                        chapterList.add(new ChapterImpl(getId(), subChapterObj));
                    }
                }
            }
            catch (IOException e) {
                /**
                 * 网络异常
                 */
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

        return chapterList;
    }

    /**
     * 获取id
     */
    private String getId() {
        return String.valueOf(mBookObj.getInt("Id"));
    }

    /**
     * 读字符串
     */
    private String readString(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buf = new byte[1024];
        while (true) {
            int bytesRead = input.read(buf);
            if (bytesRead < 0) {
                break;
            }

            output.write(buf, 0, bytesRead);
        }

        /**
         * BOM
         */
        byte[] data = output.toByteArray();
        if ((data[0] == (byte)0xEF) && (data[1] == (byte)0xBB) && (data[2] == (byte)0xBF)) {
            /**
             * 0xEFBBBF
             */
            return new String(data, 3, data.length - 3, "UTF-8");
        }
        else if ((data[0] == (byte)0xFE) && (data[1] == (byte)0xFF)) {
            /**
             * 0xFEFF
             */
            return new String(data, 2, data.length - 2, "UTF-16BE");
        }
        else if ((data[0] == (byte)0xFF) && (data[1] == (byte)0xFE)) {
            /**
             * 0xFFFE
             */
            return new String(data, 2, data.length - 2, "UTF-16LE");
        }
        else {
            /**
             * default
             */
            return new String(data);
        }
    }
}
