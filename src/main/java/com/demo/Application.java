package com.demo;

import com.zhuishushenqi.Platform;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public final class Application {
	private Settings mSettings;

	/**
	 * 构造函数
	 */
	private Application(Settings settings) {
		mSettings = settings;
	}

	/**
	 * 执行
	 */
	private void execute() {
		Scanner scanner = new Scanner(System.in);

		FileWriter writer = null;
		try {
			System.out.print("请输入书名或作者：");

			String keyWords = scanner.nextLine();
			if (keyWords.isEmpty()) {
				throw new Exception("无效的输入");
			}

			List<Book> bookList = Platform.query(keyWords);
			if (bookList.isEmpty()) {
				throw new Exception("没有找到相关的书");
			}

			System.out.printf("找到了%2d本书：\n", bookList.size());

			for (int i = 0; i < bookList.size(); i++) {
				Book book = bookList.get(i);
				System.out.printf("%2d.《%s》 %s著\n",i + 1, book.getTitle(), book.getAuthor());
			}

			System.out.print("请选择：");

			int bookIndex = scanner.nextInt();
			if (bookIndex < 1 || bookIndex > bookList.size()) {
				throw new Exception("无效的序号");
			}

			Book selectBook = bookList.get(bookIndex - 1);

			String fileName = String.format("%s_%s.txt", selectBook.getTitle(), selectBook.getAuthor());
			writer = new FileWriter(mSettings.getOutputFile(fileName));

			List<Chapter> chapterList = selectBook.getChapters();
			if (chapterList.isEmpty()) {
				throw new Exception("获取章节失败");
			}

			for (int i = 0; i < chapterList.size(); i++) {
				Chapter chapter = chapterList.get(i);

				writer.write(chapter.getTitle());
				writer.write("\r\n");
				writer.write(chapter.getContent());
				writer.write("\r\n");

				System.out.printf("下载进度: %4d/%4d\n",i + 1, chapterList.size());
			}
		}
		catch (Exception e) {
			System.out.print(e.getMessage() + "\n");
		}
		finally {
			if (writer != null) {
				try {
					writer.close();
				}
				catch (IOException e) {
					/**
					 * ignore
					 */
				}
			}
		}

		scanner.close();
	}

	/**
	 * 主程序入口
	 */
	public static void main(String[] args) {
		Application app = new Application(Settings.create(args));
		app.execute();
	}
}
