package com.mongo.base;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * 抽象Mongodb DAO层基类 提供一些简便方法.
 * 泛型： M 表示实体类型；ID表示主键类型.
 * User: shuiqing
 * Date: 2015/6/30
 * Time: 9:55
 * Email: shuiqing301@gmail.com
 * _
 * |_)._ _
 * | o| (_
 */
@NoRepositoryBean
public interface BaseMongoRepository<M, ID extends Serializable> extends MongoRepository<M, ID> {

    public M findById(String id);
}

