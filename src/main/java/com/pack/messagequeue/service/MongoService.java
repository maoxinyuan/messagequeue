package com.pacific.messagequeue.service;

import com.pacific.messagequeue.contant.MessageContantValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author maoxy
 * @date 2019/1/23 15:38
 */
@Service
public class MongoService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 创建对象
     * @param obj 对象
     * @param collectionName 表名
     */
    public  void save(Object obj,String collectionName) {
        mongoTemplate.save(obj,collectionName);
    }

    /**
     * 根据id查询对象
     */
    public <T> T find(String id,Class<T> clazz,String collectionName) {
        Query query=new Query(Criteria.where("id").is(id));
        return  mongoTemplate.findOne(query , clazz, collectionName);
    }

    /**
     * 根据条件查询聚合
     * @param clazz 返回类型
     * @param collectionName  表名
     * @return
     */
    public <T> List<T> findList(Class<T> clazz, Map<String,Integer> map,String countName,String collectionName){
        Query query = new Query();
        map.keySet().stream().forEach(key->{
            query.addCriteria(Criteria.where(key).is(map.get(key)));
        });
        query.addCriteria(Criteria.where(countName).lt(MessageContantValue.SCHEDULER_COUNT));
        return mongoTemplate.find(query, clazz, collectionName);
    }


    /**
     * 根据id更新对象
     * @param id id
     * @param paramName 字段名
     * @param paramValue 字段值
     * @param clazz 类
     * @param collectionName 表名
     */
    public <T> void update(String id,String paramName,Object paramValue,Class<T> clazz,String collectionName) {
        Query query=new Query(Criteria.where("id").is(id));
        Update update= new Update().set(paramName, paramValue);
        //更新查询返回结果集的第一条
        mongoTemplate.updateFirst(query, update, clazz, collectionName);
        //更新查询返回结果集
        //mongoTemplate.updateMulti(query,update, clazz);
    }

    /**
     * 根据id删除对象
     * @param id
     * @param collectionName 表名
     */
    public <T> void delete(String id,Class<T> clazz,String collectionName) {
        Query query=new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query,clazz, collectionName);
    }

}
