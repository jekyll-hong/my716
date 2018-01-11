package com.my716.common;

public class Book {
	private String mId;
	private String mTitle;
	private String mAuthor;

	public Book(String id, String title, String author) {
		mId = id;
		mTitle = title;
		mAuthor = author;
	}

	public String getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getAuthor() {
		return mAuthor;
	}
}
