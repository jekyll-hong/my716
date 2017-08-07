package com.my716;

class BookInfo {
	public String id;
	public String name;
	public String author;
	
	@Override
	public String toString() {
		return "《" + name + "》" + author + "著";
	}
}
