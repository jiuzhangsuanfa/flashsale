package com.jiuzhang.flashsale.mq;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.jiuzhang.flashsale.common.enums.OrderStatus;
import com.jiuzhang.flashsale.entity.OrderEntity;
import com.jiuzhang.flashsale.exception.RedisUserException;
import com.jiuzhang.flashsale.service.ActivityService;
import com.jiuzhang.flashsale.service.OrderService;
import com.jiuzhang.flashsale.service.impl.RedisServiceImpl;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RocketMQMessageListener(topic = "seckill_order", consumerGroup = "seckill_order_group")
public class OrderConsumer implements RocketMQListener<MessageExt> {
    @Resource
    private OrderService orderService;
    @Resource
    private ActivityService activityService;
    @Resource
    RedisServiceImpl redisService;

    @Override
    @Transactional
    public void onMessage(MessageExt messageExt) {
        // 1.解析创建订单请求消息
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("接收到创建订单请求：" + message);
        OrderEntity order = JSON.parseObject(message, OrderEntity.class);
        order.setCreateTime(LocalDateTime.now());
        // 2.扣减库存
        boolean lockStockResult = activityService.lockStock(order.getActivityId());
        if (lockStockResult) {
            // 订单状态
            order.setOrderStatus(OrderStatus.CREATED);
            // 尝试将用户加入到限购用户中
            try {
                redisService.addRestrictedUser(order.getActivityId(), order.getUserId());
            } catch (RedisUserException exception) {
                // 如果失败则关闭订单，不允许购买
                order.setOrderStatus(OrderStatus.CLOSED);
                log.warn("用户" + order.getUserId() + "限购失败，禁止购买");
            }
        } else {
            order.setOrderStatus(OrderStatus.NO_STOCK);
        }
        // 3.插入订单
        orderService.save(order);
    }
}
