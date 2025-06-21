package com.ideaflow.noveldownload.test;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;

import java.util.HashMap;
import java.util.Map;

public class Test {
    private static final TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));

    public static void main(String[] args) {
        // 符合 epub 标准的模板
        Template template = engine.getTemplate(StrUtil.format("chapter_{}.flt", "epub"));
        Map<String, String> map = new HashMap<>();
        map.put("title", "111");
        map.put("content", "wwww");

        String render = template.render(map);
        System.out.println(render);

    }
}
