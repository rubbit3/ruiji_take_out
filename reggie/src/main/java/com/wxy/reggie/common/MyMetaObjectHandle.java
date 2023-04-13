package com.wxy.reggie.common;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.wxy.reggie.config.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 元数据对象处理器
 */
@Slf4j
@Component
public class MyMetaObjectHandle implements MetaObjectHandler {
    /**
     * 插入操作自动填充
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("Insert");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());

    }

    /**
     * 更新操作自动填充
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("Update");
        log.info(metaObject.toString());

        long id = Thread.currentThread().getId();
        log.info("线程id{}", id);

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());

    }
}
