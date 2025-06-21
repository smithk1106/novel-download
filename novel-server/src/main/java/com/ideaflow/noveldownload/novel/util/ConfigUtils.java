package com.ideaflow.noveldownload.novel.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import com.ideaflow.noveldownload.novel.model.AppConfig;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 配置工具类
 * <p>
 * 提供加载系统属性、用户属性以及应用配置的功能。
 */
@UtilityClass
public class ConfigUtils {

    public final String SELECTION_1 = "base";
    public final String SELECTION_2 = "crawl";
    public final String SELECTION_3 = "retry";
    public final String SELECTION_4 = "proxy";

    /**
     * 加载系统属性
     */
    public Props sys() {
        return Props.getProp("application.properties", StandardCharsets.UTF_8);
    }

    /**
     * 加载用户属性
     */
    public Setting usr() {
        // 从虚拟机选项 -Dconfig.file 获取用户配置文件路径
        String configFilePath = System.getProperty("config.file");

        // 若未指定或指定路径不存在，则从默认位置获取
        if (!FileUtil.exist(configFilePath)) {
            // 用户配置文件默认路径
            String defaultPath = System.getProperty("user.dir") + File.separator + resolveConfigFileName();
            // 若默认路径也不存在，则抛出 FileNotFoundException
            return new Setting(defaultPath);
        }

        Path absolutePath = Paths.get(configFilePath).toAbsolutePath();

        return new Setting(absolutePath.toString());
    }


    // 修复 hutool 的 bug：空串不能触发默认值
    private String getStrOrDefault(Setting setting, String key, String group, String defaultValue) {
        String value = setting.getByGroup(key, group);
        return StrUtil.isEmpty(value) ? defaultValue : value;
    }

    public String resolveConfigFileName() {
        return EnvUtils.isDev() ? "config-dev.ini" : "config.ini";
    }

}