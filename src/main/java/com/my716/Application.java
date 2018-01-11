package com.my716;

import com.my716.common.*;
import com.my716.ebook.Txt;
import com.my716.http.HttpHelper;
import com.my716.json.JsonHelper;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Application {
	public static void main(String[] args) {
		/**
		 * 传入参数
		 */
		for (String arg : args) {
			if (arg.startsWith("--output=")) {
				String path = arg.substring(9);

				Settings.getInstance().setOutputDirectory(path);
			}
			else if (arg.startsWith("--proxy=")) {
				String proxy = arg.substring(8);

				Settings.getInstance().setProxy(proxy);
			}
			else {
				//TODO: add more here
			}
		}

		Application app = new Application();
		app.execute();
	}

	private void execute() {
		Scanner scanner = new Scanner(System.in);

		try {
			/*
			System.out.print("请输入手机号：");

			String mobile = scanner.nextLine();
			if (!isMobile(mobile)) {
				throw new IllegalArgumentException("不是有效的手机号");
			}

			BindMobile bindMobile = sendSms(mobile);
			if (bindMobile == null) {
				throw new IllegalStateException("获取验证码失败");
			}

			System.out.print("请输入验证码：");

			String verificationCode = scanner.nextLine();
			if (!isVerificationCode(verificationCode)) {
				throw new IllegalArgumentException("不是有效的验证码");
			}

			Account account = login(mobile, verificationCode);
			if (account == null) {
				throw new IllegalStateException("登录失败");
			}
			*/

			System.out.print("请输入关键字（书名或作者）：");

			String keyword = scanner.nextLine();

			List<Book> bookList = queryBook(keyword);
			if (bookList == null) {
				throw new IllegalStateException("查找失败");
			}

			if (bookList.isEmpty()) {
				System.out.print("没有找到相关的书\n");
			}
			else {
				System.out.print("找到了" + bookList.size() + "本书：\n");
				for (int i = 0; i < bookList.size(); i++) {
					System.out.print("  #" + i + ".《" +
							bookList.get(i).getTitle() + "》" +
							bookList.get(i).getAuthor() + "著\n");
				}

				System.out.print("请选择：");

				int bookIndex = scanner.nextInt();
				if (bookIndex < 0 || bookIndex >= bookList.size()) {
					throw new IllegalArgumentException("无效的序号");
				}

				String bookId = bookList.get(bookIndex).getId();

				List<Source> sourceList = querySource(bookId);
				if (sourceList == null) {
					throw new IllegalStateException("查找失败");
				}

				if (sourceList.isEmpty()) {
					System.out.print("没有免费的源\n");
				}
				else {
					System.out.print("有" + sourceList.size() + "个源：\n");
					for (int i = 0; i < sourceList.size(); i++) {
						System.out.print("  #" + i + ". " + sourceList.get(i).getName() + "\n");
					}

					System.out.print("请选择：");

					int sourceIndex = scanner.nextInt();
					if (sourceIndex < 0 || sourceIndex >= sourceList.size()) {
						throw new IllegalArgumentException("无效的序号");
					}

					String sourceId = sourceList.get(sourceIndex).getId();

					List<Chapter> chapterList = getChapters(sourceId);
					if (chapterList == null) {
						throw new IllegalStateException("获取章节失败");
					}

					String fileName = bookList.get(bookIndex).getTitle()
							+ "_" + sourceList.get(sourceIndex).getName();

					System.out.print(fileName + "下载开始\n");

					Txt eBook = new Txt(fileName);

					for (int i = 0; i < chapterList.size(); i++) {
						try {
							ChapterText chapterText = getChapterText(chapterList.get(i).getLink());
							if (chapterText == null) {
								throw new IllegalStateException("获取文本失败");
							}

							eBook.writeChapter(chapterList.get(i).getTitle(), chapterText.getBody());

							System.out.print("进度:" + (i + 1) + "/" + chapterList.size() + "\n");
						}
						catch (Exception e) {
							/**
							 * 10秒后重试
							 */
							try {
								Thread.sleep(10 * 1000);
							}
							catch (InterruptedException e1) {
								//ignore
							}

							i--;
						}
					}

					eBook.close();

					System.out.print(fileName + "下载完成\n");
				}
			}
		}
		catch (IOException e) {
			System.out.print(e.getMessage());
		}
		finally {
			scanner.close();
		}
	}

	private boolean isMobile(String mobile) {
		Matcher matcher = Pattern.compile("^1[34578][0-9]{9}$").matcher(mobile); //移动电话号码
		return matcher.matches();
	}

	/**
	 * 请求短信验证码
	 */
	private BindMobile sendSms(String mobile) throws IOException {
		String url = "http://api.zhuishushenqi.com/sms/sendSms";

		HashMap<String, String> form = new HashMap<String, String>();
		form.put("mobile", mobile);
		form.put("type", "login");

		BindMobile bindMobile = null;

		Request request = HttpHelper.createPostRequest(url, HttpHelper.createFormBody(form));

		Response response = HttpHelper.createClient().newCall(request).execute();
		if (response.isSuccessful()) {
			String json = response.body().string();

			bindMobile = JsonHelper.parseSendSmsResult(json);
		}

		response.close();

		return bindMobile;
	}

	private boolean isVerificationCode(String verificationCode) {
		Matcher matcher = Pattern.compile("^[0-9]{4}$").matcher(verificationCode); //4位数字
		return matcher.matches();
	}

	/**
	 * 登录
	 */
	private Account login(String mobile, String smsVerificationCode) throws IOException {
		String url = "http://api.zhuishushenqi.com/user/login";

		HashMap<String, String> form = new HashMap<String, String>();
		form.put("mobile", mobile);
		form.put("smsCode", smsVerificationCode);
		form.put("platform_code", "mobile");

		Account account = null;

		Request request = HttpHelper.createPostRequest(url, HttpHelper.createFormBody(form));

		Response response = HttpHelper.createClient().newCall(request).execute();
		if (response.isSuccessful()) {
			String json = response.body().string();

			account = JsonHelper.parseLoginResult(json);
		}

		response.close();

		return account;
	}

	/**
	 * 找书
	 */
	private List<Book> queryBook(String keyword) throws IOException {
		String url = "http://api.zhuishushenqi.com/book/fuzzy-search?query=" + keyword;

		List<Book> bookList = null;

		Request request = HttpHelper.createGetRequest(url);

		Response response = HttpHelper.createClient().newCall(request).execute();
		if (response.isSuccessful()) {
			String json = response.body().string();

			bookList = JsonHelper.parseQueryBookResult(json);
		}

		response.close();

		return bookList;
	}

	/**
	 * 列出源
	 */
	private List<Source> querySource(String bookId) throws IOException {
		String url = "http://api.zhuishushenqi.com/atoc?view=summary&book=" + bookId;

		List<Source> sourceList = null;

		Request request = HttpHelper.createGetRequest(url);

		Response response = HttpHelper.createClient().newCall(request).execute();
		if (response.isSuccessful()) {
			String json = response.body().string();

			sourceList = JsonHelper.parseQuerySourceResult(json);
		}

		response.close();

		return sourceList;
	}

	/**
	 * 获取章节
	 */
	private List<Chapter> getChapters(String sourceId) throws IOException {
		String url = "http://api.zhuishushenqi.com/atoc/" + sourceId + "?view=chapters";

		List<Chapter> chapterList = null;

		Request request = HttpHelper.createGetRequest(url);

		Response response = HttpHelper.createClient().newCall(request).execute();
		if (response.isSuccessful()) {
			String json = response.body().string();

			chapterList = JsonHelper.parseGetChaptersResult(json);
		}

		response.close();

		return chapterList;
	}

	/**
	 * 获取章节文本
	 */
	private ChapterText getChapterText(String chapterLink) throws IOException {
		String url = "http://chapterup.zhuishushenqi.com/chapter/" + URLEncoder.encode(chapterLink, "UTF-8");

		ChapterText chapterText = null;

		Request request = HttpHelper.createGetRequest(url);

		Response response = HttpHelper.createClient().newCall(request).execute();
		if (response.isSuccessful()) {
			String json = response.body().string();

			chapterText = JsonHelper.parseGetChapterTextResult(json);
		}

		response.close();

		return chapterText;
	}
}
