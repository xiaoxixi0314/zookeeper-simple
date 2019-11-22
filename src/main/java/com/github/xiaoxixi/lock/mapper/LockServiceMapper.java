package com.github.xiaoxixi.lock.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface LockServiceMapper {

    boolean insert(Integer id);

    boolean delete(Integer id);

}
