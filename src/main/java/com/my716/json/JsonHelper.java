package com.my716.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.my716.common.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JsonHelper {
    public static BindMobile parseSendSmsResult(String json) {
        JsonObject root = new JsonParser().parse(json).getAsJsonObject();
        if (root.get("ok").getAsBoolean()) {
            return new BindMobile();
        }

        return null;
    }

    public static Account parseLoginResult(String json) {
        JsonObject root = new JsonParser().parse(json).getAsJsonObject();
        if (root.get("ok").getAsBoolean()) {
            String token = root.get("token").getAsString();

            return new Account(token);
        }

        return null;
    }

    public static List<Book> parseQueryBookResult(String json) {
        JsonObject root = new JsonParser().parse(json).getAsJsonObject();
        if (root.get("ok").getAsBoolean()) {
            List<Book> bookList = new LinkedList<Book>();

            JsonArray bookArray = root.get("books").getAsJsonArray();
            for (int i = 0; i < bookArray.size(); i++) {
                JsonObject book = bookArray.get(i).getAsJsonObject();

                String id = book.get("_id").getAsString();
                String title = book.get("title").getAsString();
                String author = book.get("author").getAsString();

                bookList.add(new Book(id, title, author));
            }

            return bookList;
        }

        return null;
    }

    public static List<Source> parseQuerySourceResult(String json) {
        List<Source> sourceList = new ArrayList<Source>();

        JsonArray sourceArray = new JsonParser().parse(json).getAsJsonArray();
        for (int i = 0; i < sourceArray.size(); i++) {
            JsonObject source = sourceArray.get(i).getAsJsonObject();

            String id = source.get("_id").getAsString();
            String name = source.get("name").getAsString();

            if (!name.equals("优质书源")) {
                sourceList.add(new Source(id, name));
            }
        }

        return sourceList;
    }

    public static List<Chapter> parseGetChaptersResult(String json) {
        JsonObject root = new JsonParser().parse(json).getAsJsonObject();

        List<Chapter> chapterList = new LinkedList<Chapter>();

        JsonArray chapterArray = root.get("chapters").getAsJsonArray();
        for (int i = 0; i < chapterArray.size(); i++) {
            JsonObject chapter = chapterArray.get(i).getAsJsonObject();

            String title = chapter.get("title").getAsString();
            String link = chapter.get("link").getAsString();

            chapterList.add(new Chapter(title, link));
        }

        return chapterList;
    }

    public static ChapterText parseGetChapterTextResult(String json) {
        JsonObject root = new JsonParser().parse(json).getAsJsonObject();
        if (root.get("ok").getAsBoolean()) {
            JsonObject chapter = root.get("chapter").getAsJsonObject();

            String body = chapter.get("body").getAsString();

            return new ChapterText(body);
        }

        return null;
    }
}
