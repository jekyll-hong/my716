package com.base;

import java.util.List;

/**
 * 平台
 */
public interface Platform {
    /**
     * 根据关键字找书
     */
    List<Book> query(String keyWords);
}
