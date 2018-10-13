package com.zhuishushenqi;

import com.demo.Book;
import com.demo.Chapter;
import com.utils.OkHttp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

class BookImpl implements Book {
	private static final String QUERY_SOURCE = "http://api.zhuishushenqi.com/atoc?view=summary&book=%s";
	private static final String QUERY_CHAPTER = "http://api.zhuishushenqi.com/atoc/%s?view=chapters";

	private JSONObject mBookObj;
	private JSONObject mSourceObj;

	/**
	 * 构造函数
	 */
	public BookImpl(JSONObject bookObj) {
		mBookObj = bookObj;

		selectSource();
	}

	/**
	 * 选择源
	 */
	private void selectSource() {
		InputStream input = OkHttp.get(String.format(QUERY_SOURCE, getId()), null);
		if (input != null) {
			try {
				JSONArray sourceArray = new JSONArray(new JSONTokener(input));

				for (int i = 1; i < sourceArray.length(); i++) {
					JSONObject sourceObj = sourceArray.getJSONObject(i);

					String sourceName = sourceObj.getString("name");
					if (sourceName.equals("176小说")) {
						mSourceObj = sourceObj;
						break;
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
	}

	/**
	 * 获取id
	 */
	private String getId() {
		return mBookObj.getString("_id");
	}

	@Override
	public String getTitle() {
		return mBookObj.getString("title");
	}

	@Override
	public String getAuthor() {
		return mBookObj.getString("author");
	}

	@Override
	public List<Chapter> getChapters() {
		List<Chapter> chapterList = new LinkedList<Chapter>();

		InputStream input = OkHttp.get(String.format(QUERY_CHAPTER, getSourceId()), null);
		if (input != null) {
			try {
				JSONObject rootObj = new JSONObject(new JSONTokener(input));

				JSONArray chapterArray = rootObj.getJSONArray("chapters");
				for (int i = 0; i < chapterArray.length(); i++) {
					JSONObject chapterObj = chapterArray.getJSONObject(i);
					chapterList.add(new ChapterImpl(chapterObj));
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

		return chapterList;
	}

	/**
	 * 获取源id
	 */
	private String getSourceId() {
		return mSourceObj.getString("_id");
	}
}
