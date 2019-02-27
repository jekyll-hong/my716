package com.kanshushenqi;

import com.base.Chapter;
import com.utils.OkHttp;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ChapterImpl implements Chapter {
    private static final String QUERY_CONTENT = Domain.URL + "/book/%s/%s.html";

    private String mBookId;
    private JSONObject mChapterObj;

    /**
     * 构造函数
     */
    public ChapterImpl(String bookId, JSONObject chapterObj) {
        mBookId = bookId;
        mChapterObj = chapterObj;
    }

    @Override
    public String getTitle() {
        return mChapterObj.getString("name");
    }

    @Override
    public String getContent() {
        StringBuffer buffer = new StringBuffer();

        InputStream input = OkHttp.get(String.format(QUERY_CONTENT, mBookId, getId()), null);
        if (input != null) {
            try {
                /**
                 * 文本头有BOM
                 */
                String strJson = readString(input);
                JSONObject rootObj = new JSONObject(strJson);

                JSONObject dataObj = rootObj.getJSONObject("data");
                String content = dataObj.getString("content");

                buffer.append(filter(content));
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

        return buffer.toString();
    }

    /**
     * 获取id
     */
    private String getId() {
        return String.valueOf(mChapterObj.getInt("id"));
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

    /**
     * 过滤内容
     */
    private static String filter(String src) {
        StringBuffer strBuffer = new StringBuffer();

        String[] result = src.replaceAll("<br/>", "\r\n")
                .replaceAll("<br>", "\r\n")
                .replaceAll("<br />", "\r\n")
                .replaceAll("</br>", "\r\n")
                .replaceAll("<p>", "\r\n")
                .replaceAll("&nbsp;", "")
                .replaceAll("<.+?>", "")
                .split("\r\n");

        for (int i = 0; i < result.length; i++) {
            String paragraph = result[i].trim();

            if (!paragraph.isEmpty()) {
                paragraph = paragraph.replace(" ", "")
                        .replace("\u3000\u3000", "")
                        .replace("\t", "")
                        .replace("\\s*|\t|\r|\n|\r\n", "");

                if (!paragraph.isEmpty()) {
                    strBuffer.append(paragraph);
                }
            }
        }

        return strBuffer.toString();
    }
}
