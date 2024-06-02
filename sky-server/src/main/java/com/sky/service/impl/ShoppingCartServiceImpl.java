package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断当前加入到购物车中的商品是否已经存在了
        ShoppingCart shoppingCart=new ShoppingCart();
        //对象属性拷贝
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId()); //取得当前登录用户的id

        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);

        //如果存在了，只需要将商品数量加一
        if(list!=null && list.size()>0){
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateNumberById(cart);

        }else {
            //如果不存在，需要插入一条购物车数据
            //判断本次添加到购物车的是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();//从前端传过来的数据取出菜品id
            if(dishId!=null){
                //本次添加的是菜品
                Dish dish = dishMapper.getById(dishId);//根据菜品id查找菜品

                shoppingCart.setName(dish.getName());//菜品名称
                shoppingCart.setImage(dish.getImage());//图片
                shoppingCart.setAmount(dish.getPrice());//金额
            }else {
                //本次添加的是套餐
                Long setmealId = shoppingCartDTO.getSetmealId();//从前端传过来的数据取出套餐id
                Setmeal setmeal = setmealMapper.getById(setmealId);//根据套餐id查找套餐

                shoppingCart.setName(setmeal.getName());//套餐名称
                shoppingCart.setImage(setmeal.getImage());//图片
                shoppingCart.setAmount(setmeal.getPrice()); //金额
            }
            shoppingCart.setNumber(1);//份数
            shoppingCart.setCreateTime(LocalDateTime.now());//创建时间
            shoppingCartMapper.insert(shoppingCart);

        }

    }


    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        //获取到当前登录用户的id
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }


    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }


    /**
     * 删除购物车中的一个商品
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();

        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //查询当前用户的购物车数据
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //如果要删除的记录存在
        if(list!=null&&list.size()>0){
            shoppingCart = list.get(0);
            //获取该商品的数量
            Integer number = shoppingCart.getNumber();

            if(number==1) {
                //当前商品在购物车中的份数为1，直接删除当前记录
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }else {
                //当前商品在购物车中的份数不为1，修改份数即可
                shoppingCart.setNumber(shoppingCart.getNumber()-1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }

        }


    }


}
