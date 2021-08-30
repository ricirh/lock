package com.dao;

import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDao {

    @Update("update goods set allowance = allowance - 1")
    void createOrder();
}
