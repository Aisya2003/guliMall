package com.example.mall.cart.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.cart.feign.ProductFeignClient;
import com.example.mall.cart.feign.WareFeignClient;
import com.example.mall.cart.interceptor.HandleUserInfoInterceptor;
import com.example.mall.cart.model.to.UserTo;
import com.example.mall.cart.model.vo.CartVo;
import com.example.mall.cart.service.CartService;
import com.example.mall.common.model.constant.RedisConstant;
import com.example.mall.common.model.exception.ThreadPoolException;
import com.example.mall.common.model.to.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ProductFeignClient productFeignClient;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final WareFeignClient wareFeignClient;

    @Override
    public void addToCart(Long skuId, Integer skuNum) {
        BoundHashOperations<String, Object, Object> hashOperations = getCart();
        CartVo.CartItem cartItem = new CartVo.CartItem();

        String redisCartItem = (String) hashOperations.get(skuId.toString());
        if (StringUtils.isNotBlank(redisCartItem)) {
            //购物车中存在商品
            CartVo.CartItem item = JSON.parseObject(redisCartItem, CartVo.CartItem.class);
            item.setCount(item.getCount() + skuNum);
            hashOperations.put(skuId.toString(), JSON.toJSONString(item));
            return;
        }
        //不存在则查询
        CompletableFuture<Void> cartItemFuture = CompletableFuture.runAsync(() -> {
            SkuInfoTo skuInfoTo = productFeignClient.getInfo(skuId);
            cartItem.setCheck(true);
            cartItem.setCount(skuNum);
            cartItem.setImage(skuInfoTo.getSkuDefaultImg());
            cartItem.setPrice(skuInfoTo.getPrice());
            cartItem.setTitle(skuInfoTo.getSkuTitle());
            cartItem.setSkuId(skuInfoTo.getSkuId());
        }, threadPoolExecutor);

        CompletableFuture<Void> attrsFuture = CompletableFuture.runAsync(() ->
                cartItem.setSkuAttr(productFeignClient.getSkuSaleAttrs(skuId)));

        try {
            CompletableFuture.allOf(cartItemFuture, attrsFuture).get();
        } catch (Exception e) {
            ThreadPoolException.error("执行添加购物车线程异常", e.getCause());
        }

        hashOperations.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public CartVo.CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> hashOperations = getCart();
        String itemJson = (String) hashOperations.get(skuId.toString());
        if (StringUtils.isBlank(itemJson)) {
            return null;
        }
        return JSON.parseObject(itemJson, CartVo.CartItem.class);
    }

    @Override
    public CartVo getCartInfo() {
        UserTo userTo = HandleUserInfoInterceptor.threadLocal.get();
        BoundHashOperations<String, Object, Object> hashOperations = getCart();
        CartVo cartVo = new CartVo();
        if (userTo.getUserId() == null) {
            //未登录
            cartVo.setItems(this.getCartItemList(hashOperations));
        } else {
            //登录
            //获取所有临时购物车的信息
            String tempCartKey = RedisConstant.Cart.USER_CART_PREFIX + userTo.getUserKey();
            hashOperations = stringRedisTemplate.boundHashOps(tempCartKey);
            List<CartVo.CartItem> cartItemList = this.getCartItemList(hashOperations);
            if (cartItemList != null && !cartItemList.isEmpty()) {
                for (CartVo.CartItem cartItem : cartItemList) {
                    //合并到购物车中
                    addToCart(cartItem.getSkuId(), cartItem.getCount());
                }
                //删除临时购物车
                stringRedisTemplate.delete(tempCartKey);
            }
            //查询用户购物车
            hashOperations = getCart();
            cartVo.setItems(getCartItemList(hashOperations));
        }
        return cartVo;
    }

    @Override
    public void changeItemStatus(Long skuId, Boolean hasChecked) {
        CartVo.CartItem cartItem = this.getCartItem(skuId);
        cartItem.setCheck(hasChecked);
        this.getCart().put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void changeItemCount(Long skuId, Integer itemCount) {
        CartVo.CartItem cartItem = this.getCartItem(skuId);
        cartItem.setCount(itemCount);
        this.getCart().put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void removeItem(Long skuId) {
        this.getCart().delete(skuId.toString());
    }

    @Override
    public List<CartVo.CartItem> getCartItemList() {
        UserTo userTo = HandleUserInfoInterceptor.threadLocal.get();
        if (userTo.getUserId() == null) {
            return null;
        }
        BoundHashOperations<String, Object, Object> hashOperations = getCart();
        List<CartVo.CartItem> cartItemList = getCartItemList(hashOperations);
        if (cartItemList == null || cartItemList.isEmpty()) {
            return null;
        }
        Set<Long> skuIds = cartItemList.stream()
                .map(CartVo.CartItem::getSkuId)
                .collect(Collectors.toSet());
        //更新价格
        List<UpdateCartItemPriceTo> price = productFeignClient.getPrice(skuIds);
        if (price == null || price.isEmpty()) {
            return null;
        }
        Map<Long, BigDecimal> priceMap = price
                .stream()
                .collect(Collectors.toMap(
                        UpdateCartItemPriceTo::getSkuId,
                        UpdateCartItemPriceTo::getPrice));
        //获取重量
        List<GetItemWeightTo> weightToList = productFeignClient.getWeight(skuIds);
        if (weightToList == null || weightToList.isEmpty()) {
            return null;
        }
        Map<Long, BigDecimal> weightMap = weightToList.stream()
                .collect(Collectors.toMap(GetItemWeightTo::getSkuId, GetItemWeightTo::getWeight));
        //获取库存
        List<SkuHasStockPrefetchTo> prefetchTos = cartItemList.stream()
                .map(cartItem -> {
                    SkuHasStockPrefetchTo prefetchTo = new SkuHasStockPrefetchTo();
                    prefetchTo.setSkuId(cartItem.getSkuId());
                    prefetchTo.setPrefetch(cartItem.getCount());
                    return prefetchTo;
                })
                .collect(Collectors.toList());
        List<SkuHasStockTo> skuHasStockTos = wareFeignClient.hasStockWithPrefetch(prefetchTos);
        Map<Long, Boolean> stockMap = null;
        if (skuHasStockTos != null && !skuHasStockTos.isEmpty()) {
            stockMap = skuHasStockTos.stream()
                    .collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
        }
        Map<Long, Boolean> finalStockMap = stockMap;
        return cartItemList.stream()
                .filter(CartVo.CartItem::getCheck)
                .peek(cartItem -> {
                    Long skuId = cartItem.getSkuId();
                    cartItem.setPrice(priceMap.get(skuId));
                    cartItem.setWeight(weightMap.get(skuId));
                    if (finalStockMap != null) {
                        cartItem.setHasStock(finalStockMap.get(skuId));
                    }
                })
                .collect(Collectors.toList());
    }

    private List<CartVo.CartItem> getCartItemList(BoundHashOperations<String, Object, Object> hashOperations) {
        List<Object> values = hashOperations.values();
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream()
                .map(o -> JSON.parseObject((String) o, CartVo.CartItem.class))
                .collect(Collectors.toList());
    }

    private BoundHashOperations<String, Object, Object> getCart() {
        UserTo userTo = HandleUserInfoInterceptor.threadLocal.get();
        Long userId = userTo.getUserId();
        String cartKey = RedisConstant.Cart.USER_CART_PREFIX;
        if (userId != null) {
            cartKey += userId;
        } else {
            cartKey += userTo.getUserKey();
        }
        return stringRedisTemplate.boundHashOps(cartKey);
    }
}
