package com.zhuishushenqi;

import com.demo.Chapter;
import com.utils.OkHttp;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class ChapterImpl implements Chapter {
	private static final String QUERY_CONTENT = "http://chapterup.zhuishushenqi.com/chapter/%s";

	private JSONObject mChapterObj;

	/**
	 * 构造函数
	 */
	public ChapterImpl(JSONObject chapterObj) {
		mChapterObj = chapterObj;
	}

	@Override
	public String getTitle() {
		return mChapterObj.getString("title");
	}

	@Override
	public String getContent() {
		StringBuffer buffer = new StringBuffer();

		InputStream input = OkHttp.get(String.format(QUERY_CONTENT, getEncodedLink()), null);
		if (input != null) {
			try {
				JSONObject rootObj = new JSONObject(new JSONTokener(input));

				if (rootObj.getBoolean("ok")) {
					JSONObject chapterObj = rootObj.getJSONObject("chapter");
					buffer.append(chapterObj.getString("body"));
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

		return buffer.toString();
	}

	/**
	 * 获取编码后的链接
	 */
	public String getEncodedLink() {
		String encodedLink = null;

		try {
			encodedLink = URLEncoder.encode(mChapterObj.getString("link"), "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			/**
			 * ignore
			 */
		}

		return encodedLink;
	}
}
