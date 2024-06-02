package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface DishFlavorMapper {


    /**
     * 批量插入口味数据
     * @param flavors
     */
     void insertBatch(List<DishFlavor> flavors);



    /**
     * 根据菜品id批量删除关联的口味表数据
     * @param ids
     */
    void deleteByDishIds(@Param("ids") List<Long> ids);


    /**
     * 根据菜品id查询口味
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id=#{id}")
    List<DishFlavor> getByDishId(@Param("id") Long id);

    /**
     * 根据菜品id删除关联的口味表数据
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id=#{id}")
    void deleteByDishId(@Param("id") Long id);
}
