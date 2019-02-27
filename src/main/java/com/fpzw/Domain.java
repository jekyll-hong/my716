package com.fpzw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

class Domain {
    private static final String URL = "https://m.fpzw.com/";
    private static final String CHARSET_NAME = "GBK";

    /**
     * 获取网站的url
     */
    public static String getURL() {
        return URL;
    }

    /**
     * 读取网页的内容（注意：HTML页面的字符集为GBK）
     */
    public static String readWebPage(InputStream input) {
        String content = null;

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buf = new byte[4096];
            while (true) {
                int ret = input.read(buf);
                if (ret < 0) {
                    break;
                }

                output.write(buf, 0, ret);
            }

            content = output.toString(CHARSET_NAME);
        }
        catch (UnsupportedEncodingException e) {
            /**
             * 不支持的编码
             */
        }
        catch (IOException e) {
            /**
             * 网络异常
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

        return content;
    }
}
