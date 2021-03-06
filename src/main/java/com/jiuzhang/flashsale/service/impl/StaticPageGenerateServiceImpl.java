package com.jiuzhang.flashsale.service.impl;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.jiuzhang.flashsale.entity.ActivityEntity;
import com.jiuzhang.flashsale.entity.CommodityEntity;
import com.jiuzhang.flashsale.exception.StaticPageGenerateException;
import com.jiuzhang.flashsale.service.StaticPageGenerateService;
import com.jiuzhang.flashsale.service.ActivityService;
import com.jiuzhang.flashsale.service.CommodityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StaticPageGenerateServiceImpl implements StaticPageGenerateService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private CommodityService commodityService;

    public void generateActivityStaticPage(long activityId) throws StaticPageGenerateException {

        try (PrintWriter writer = new PrintWriter(
                new File("src/main/resources/templates/" + "seckill_item_" + activityId + ".html"))) {
            ActivityEntity activity = activityService.getById(activityId);
            CommodityEntity commodity = commodityService.getById(activity.getCommodityId());
            // 获取页面数据
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("activity", activity);
            resultMap.put("commodity", commodity);
            // 创建thymeleaf上下文对象
            Context context = new Context();
            // 把数据放入上下文对象
            context.setVariables(resultMap);
            // 执行页面静态化方法
            templateEngine.process("seckill_item", context, writer);
        } catch (Exception exception) {
            String message = "活动页面静态化出错：" + activityId;
            log.error(message);
            throw new StaticPageGenerateException(message);
        }

    }

}
