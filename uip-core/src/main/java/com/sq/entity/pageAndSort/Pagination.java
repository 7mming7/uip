package com.sq.entity.pageAndSort;

import org.springframework.data.domain.Pageable;

import java.io.Serializable;

/**
 * 抽象的分页对象.
 * User: shuiqing
 * Date: 2015/2/25
 * Time: 20:55
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class Pagination implements Serializable {

    private static final long serialVersionUID = 3309155422497701579L;

    // ==========================================
    // fields...
    private Sort sort;
}

