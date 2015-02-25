package com.sq.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * 抽象DAO层基类 提供一些简便方法.
 * 泛型： M 表示实体类型；ID表示主键类型.
 * User: shuiqing
 * Date: 2015/2/25
 * Time: 13:01
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
@NoRepositoryBean
public interface BaseRepository<M, ID extends Serializable> extends JpaRepository<M, ID> {

}
