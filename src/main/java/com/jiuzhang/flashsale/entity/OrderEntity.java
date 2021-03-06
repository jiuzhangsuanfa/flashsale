package com.jiuzhang.flashsale.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiuzhang.flashsale.common.enums.OrderStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秒杀订单 Entity
 *
 * @author jiuzhang
 * @since 2021-01-15
 */
@Data
@TableName("`order`")
@EqualsAndHashCode(callSuper = false)
public class OrderEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;

    private OrderStatus orderStatus;

    private Long activityId;

    private Long userId;

    private BigDecimal orderAmount;

    private LocalDateTime createTime;

    private LocalDateTime payTime;

}
