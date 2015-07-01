package com.mongo.base;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;

/**
 * BaseMongoRespository的实现..
 * User: shuiqing
 * Date: 2015/6/30
 * Time: 10:20
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class BaseMongoRepositoryImpl<M, ID extends Serializable> extends SimpleMongoRepository<M, ID>
        implements BaseMongoRepository<M,ID>{

    private Class<M> persistentClass;

    private MongoTemplate mongoTemplate;

    /**
     * Creates a ew {@link org.springframework.data.mongodb.repository.support.SimpleMongoRepository} for the given {@link org.springframework.data.mongodb.repository.query.MongoEntityInformation} and {@link org.springframework.data.mongodb.core.MongoTemplate}.
     *
     * @param metadata        must not be {@literal null}.
     * @param mongoOperations
     */
    public BaseMongoRepositoryImpl(MongoEntityInformation metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * @return the persistentClass
     */
    public Class<M> getPersistentClass() {
        return this.persistentClass;
    }

    public M findById(String id) {
        return getMongoTemplate().findOne(new Query(Criteria.where("id").is(id)), this.getPersistentClass());
    }
}
