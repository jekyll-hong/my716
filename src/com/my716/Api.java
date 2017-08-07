package com.my716;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

final class Api {
	private static OkHttpClient mClient = new OkHttpClient();
	
	/**
	 * 通过书名查询
	 * @param bookName
	 * @return
	 * @throws IOException
	 */
	public static List<BookInfo> queryByName(String bookName) {
		String url = "http://api.zhuishushenqi.com/book/fuzzy-search?query=" + bookName + "&version=2&onlyTitle=true";
		ArrayList<BookInfo> books = new ArrayList<BookInfo>();
		
		try {
			JsonObject rootObj = process(url).getAsJsonObject();
			
			boolean result = rootObj.get("ok").getAsBoolean();
			if (result == true) {
				JsonArray bookArray = rootObj.get("books").getAsJsonArray();
				for (int i = 0; i < bookArray.size(); i++) {
					JsonObject bookObj = bookArray.get(i).getAsJsonObject();
					
					BookInfo book = new BookInfo();
					book.id = bookObj.get("_id").getAsString();
					book.name = bookObj.get("title").getAsString();
					book.author = bookObj.get("author").getAsString();
					
					books.add(book);
				}
			}
		}
		catch (IOException e) {
			//Ignore
		}
		
		return books;
	}
	
	/**
	 * 通过作者查询
	 * @param bookAuthor
	 * @return
	 * @throws IOException
	 */
	public static List<BookInfo> queryByAuthor(String bookAuthor) {
		String url = "http://api.zhuishushenqi.com/book/accurate-search?author=" + bookAuthor;
		ArrayList<BookInfo> books = new ArrayList<BookInfo>();
		
		try {
			JsonObject rootObj = process(url).getAsJsonObject();
			
			boolean result = rootObj.get("ok").getAsBoolean();
			if (result == true) {
				JsonArray bookArray = rootObj.get("books").getAsJsonArray();
				for (int i = 0; i < bookArray.size(); i++) {
					JsonObject bookObj = bookArray.get(i).getAsJsonObject();
					
					BookInfo book = new BookInfo();
					book.id = bookObj.get("_id").getAsString();
					book.name = bookObj.get("title").getAsString();
					book.author = bookObj.get("author").getAsString();
					
					books.add(book);
				}
			}
		}
		catch (IOException e) {
			//Ignore
		}
		
		return books;
	}
	
	/**
	 * 获取目录
	 * @param bookId
	 * @return
	 */
	public static List<ChapterInfo> getContents(String bookId) {
		String url = "http://api.zhuishushenqi.com/mix-atoc/" + bookId;
		ArrayList<ChapterInfo> chapters = new ArrayList<ChapterInfo>();
		
		try {
			JsonObject rootObj = process(url).getAsJsonObject();
			
			boolean result = rootObj.get("ok").getAsBoolean();
			if (result == true) {
				JsonObject tocObj = rootObj.get("mixToc").getAsJsonObject();
				
				JsonArray chapterArray = tocObj.get("chapters").getAsJsonArray();
				for (int i = 0; i < chapterArray.size(); i++) {
					JsonObject chapterObj = chapterArray.get(i).getAsJsonObject();
					
					ChapterInfo chapter = new ChapterInfo();
					chapter.title = chapterObj.get("title").getAsString();
					chapter.url = chapterObj.get("link").getAsString();
					
					chapters.add(chapter);
				}
			}
		}
		catch (IOException e) {
			//Ignore
		}
		
		return chapters;
	}
	
	/**
	 * 获取章节内容
	 * @param chapterUrl
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String getText(String chapterUrl) throws UnsupportedEncodingException {
		String url = "http://chapterup.zhuishushenqi.com/chapter/" + URLEncoder.encode(chapterUrl, "UTF-8");
		String text = "";
		
		try {
			JsonObject rootObj = process(url).getAsJsonObject();
			
			boolean result = rootObj.get("ok").getAsBoolean();
			if (result == true) {
				JsonObject chapterObj = rootObj.get("chapter").getAsJsonObject();
				
				text = chapterObj.get("body").getAsString();
			}
		}
		catch (IOException e) {
			//Ignore
		}
		
		return text;
	}
	
	/**
	 * 列出收录本书的所有源
	 * @param bookId
	 * @return
	 */
	public static List<SourceInfo> listSources(String bookId) {
		String url = "http://api.zhuishushenqi.com/atoc?view=summary&book=" + bookId;
		ArrayList<SourceInfo> sources = new ArrayList<SourceInfo>();
		
		try {
			JsonArray sourceArray = process(url).getAsJsonArray();
			for (int i = 0; i < sourceArray.size(); i++) {
				JsonObject sourceObj = sourceArray.get(i).getAsJsonObject();
				
				SourceInfo source = new SourceInfo();
				source.name = sourceObj.get("host").getAsString();
				source.url = sourceObj.get("link").getAsString();
				
				sources.add(source);
			}
		}
		catch (IOException e) {
			//Ignore
		}
		
		return sources;
	}
	
	private static JsonElement process(String url) throws IOException {
		Request request = buildRequest(url);
		
		Response response = connect(request);
		if (response.code() != 200) {
			return null;
		}
		
		ResponseBody body = response.body();
		
		MediaType contentType = body.contentType();
		if (!contentType.type().equals("application") || !contentType.subtype().equals("json")) {
			return null;
		}
		
		return parseJson(body.string());
	}
	
	private static Request buildRequest(String url) {
		Request.Builder builder = new Request.Builder();
		builder.url(url);
		
		return builder.build();
	}
	
	private static Response connect(Request request) throws IOException {
		Call session = mClient.newCall(request);
		
		return session.execute();
	}
	
	private static JsonElement parseJson(String json) {
		JsonParser parser = new JsonParser();
		
		return parser.parse(json);
	}
}
