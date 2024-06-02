package com.sky.controller.user;


import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Tag(name = "店铺相关接口")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 查询店铺营业状态
     */
    @GetMapping("/status")
    @Operation(summary = "查询店铺营业状态")
    public Result<Integer> getStatus(){
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        log.info("店铺的营业状态为：{}",shopStatus==1?"营业":"打烊");
        return Result.success(shopStatus);

    }


}






