package com.ideaflow.noveldownload.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ideaflow.noveldownload.config.AppProperties;
import com.ideaflow.noveldownload.constans.CommonConst;
import com.ideaflow.noveldownload.entity.NovelEntity;
import com.ideaflow.noveldownload.mapper.NovelMapper;
import com.ideaflow.noveldownload.pojo.CommonResult;
import com.ideaflow.noveldownload.pojo.NovelWebSearch;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Objects;

@RestController
@RequestMapping("/novel")
public class NovelController {

    @jakarta.annotation.Resource
    private NovelMapper novelMapper;

    @Autowired
    private AppProperties appProperties;

    @PostMapping("/list")
    public CommonResult<IPage<NovelEntity>> list(@Valid @RequestBody NovelWebSearch novelWebSearch) {
        // 创建分页对象，传入当前页和每页显示的条数
        Page<NovelEntity> page = new Page<>(novelWebSearch.getPageNo(), novelWebSearch.getPageSize());
        
        // 创建条件构造器
        LambdaQueryWrapper<NovelEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        // 如果name不为空，添加name的模糊查询条件
        if (StringUtils.hasText(novelWebSearch.getName())) {
            queryWrapper.like(NovelEntity::getName, novelWebSearch.getName());
        }
        queryWrapper.orderByDesc(NovelEntity::getId);
        // 执行分页查询
        IPage<NovelEntity> pageResult = novelMapper.selectPage(page, queryWrapper);
        pageResult.getRecords().forEach(novelEntity -> {
            // 设置下载地址
            if (CommonConst.SAVE_TYPE_HTML.equalsIgnoreCase(novelEntity.getSaveType()) && StringUtils.hasText(novelEntity.getDownloadUrl())) {
                if (!novelEntity.getDownloadUrl().startsWith("http")) {
                    novelEntity.setDownloadUrl(appProperties.getContentBase() + novelEntity.getDownloadUrl());
                }
            }
            // 设置封面地址
            if (StringUtils.hasText(novelEntity.getCover())) {
                novelEntity.setCover(appProperties.getContentBase() + novelEntity.getCover());
            }
        });
        
        return CommonResult.success(pageResult);
    }

    @PostMapping("/delete/{id}")
    public CommonResult<String> delete(@PathVariable String id) {
        NovelEntity novelEntity = novelMapper.selectById(id);
        if (Objects.isNull(novelEntity)|| !StringUtils.hasText(novelEntity.getDownloadUrl())) {
            return CommonResult.error("小说不存在");
        }
        // 设置文件路径
        String filePath =  System.getProperty("user.dir") + File.separator+novelEntity.getDownloadUrl();
        File file = new File(filePath);

        if (!file.exists()) {
            int deleteById = novelMapper.deleteById(id);
            return CommonResult.success("删除成功");
        }
        boolean delete = file.delete();
        int deleteById = novelMapper.deleteById(id);
        if (!delete) {
            return CommonResult.success("删除失败");
        }
        return CommonResult.success("删除成功");
    }

}
