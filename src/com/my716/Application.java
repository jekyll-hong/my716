package com.my716;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class Application {
	private static final String BOOK_DOWNLOAD_PATH = "/home/hongyu/Downloads";
	
	public static void main(String[] args) {
		String bookName = "*";
		String bookAuthor = "*";
		
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			
			if (arg.startsWith("-name=")) {
				bookName = arg.substring(6);
				if (bookName.isEmpty()) {
					bookName = "*";
				}
			}
			else if (arg.startsWith("-author=")) {
				bookAuthor = arg.substring(8);
				if (bookAuthor.isEmpty()) {
					bookAuthor = "*";
				}
			}
		}
		
		if (bookName.equals("*") && bookAuthor.equals("*")) {
			System.err.print("没有指定书名或作者！\r\n");
			return;
		}
		
		List<BookInfo> books = query(bookName, bookAuthor);
		if (books.isEmpty()) {
			System.err.print("没有找到相关的书！\r\n");
			return;
		}
		
		System.out.print("找到了" + books.size() + "本相关的书：\r\n");
		for (int i = 0; i < books.size(); i++) {
			BookInfo book = books.get(i);
			System.out.print("#" + i + "，" + book.toString() + "\r\n");
		}
		
		System.out.print("请输入序号：");
		
		int bookIndex = getUserSelect();
		if (bookIndex < 0 || bookIndex >= books.size()) {
			System.err.print("无效的序号！\r\n");
			return;
		}
		
		BookInfo book = books.get(bookIndex);
		
		List<SourceInfo> sources = Api.listSources(book.id);
		if (!inCollection(sources)) {
			System.err.print("还没有被书源收入！\r\n");
			return;
		}
		
		List<ChapterInfo> chapters = Api.getContents(book.id);
		if (chapters.isEmpty()) {
			System.err.print("获取目录失败！\r\n");
			return;
		}
		
		File ebook = new File(BOOK_DOWNLOAD_PATH + "/" + book.name + ".txt");
		if (ebook.exists()) {
			ebook.delete();
		}
		
		try {
			FileWriter writer = new FileWriter(ebook);
			
			for (int i = 0; i < chapters.size(); i++) {
				ChapterInfo chapter = chapters.get(i);
				writer.write(chapter.title);
				writer.write("\r\n");
				
				System.out.print("开始下载" + chapter.title + "\r\n");
				
				String text = Api.getText(chapter.url);
				writer.write(text);
				writer.write("\r\n");
				
				System.out.print(chapter.title + "下载完毕\r\n");
			}
			
			writer.close();
		}
		catch (IOException e) {
			//Ignore
		}
		
		System.out.print("《" + book.name + "》下载完毕！\r\n");
	}
	
	private static int getUserSelect() {
		Scanner scanner = new Scanner(System.in);
		
		int index = -1;
		try {
			index = Integer.parseInt(scanner.nextLine());
		}
		catch (NumberFormatException e) {
			//Ignore
		}
		
		scanner.close();
		
		return index;
	}
	
	private static List<BookInfo> query(String bookName, String bookAuthor) {
		List<BookInfo> books = null;
		
		if (!bookAuthor.equals("*")) {
			books = Api.queryByAuthor(bookAuthor);
			
			if (!books.isEmpty() && !bookName.equals("*")) {
				books = filterByName(books, bookName);
			}
		}
		else {
			books = Api.queryByName(bookName);
		}
		
		return books;
	}
	
	private static List<BookInfo> filterByName(List<BookInfo> booksByTheSameAuthor, String bookName) {
		ArrayList<BookInfo> books = new ArrayList<BookInfo>();
		
		for (int i = 0; i < booksByTheSameAuthor.size(); i++) {
			BookInfo book = booksByTheSameAuthor.get(i);
			
			if (book.name.contains(bookName)) {
				books.add(book);
			}
		}
		
		return books;
	}
	
	private static boolean inCollection(List<SourceInfo> sources) {
		for (int i = 0; i < sources.size(); i++) {
			SourceInfo source = sources.get(i);
			
			if (source.name.equals("book.my716.com")) {
				return true;
			}
		}
		
		return false;
	}
}
