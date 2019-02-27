package com.base;

import java.util.List;

/**
 * 书
 */
public interface Book {
    /**
     * 获取名称
     */
    String getTitle();

    /**
     * 获取作者
     */
    String getAuthor();

    /**
     * 获取章节
     */
    List<Chapter> getChapters();
}
