package com.lrh.es.controller;

import com.lrh.es.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ContentController {

    @Autowired
    public ContentService contentService;

    /**
     * 解析html内容放到es中
     * @param keyword
     * @return
     */
    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable String keyword){
        return contentService.parseContent(keyword);
    }

    /**
     * 查询分页数据
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> searchPage(@PathVariable String keyword,
                                               @PathVariable int pageNo,
                                               @PathVariable int pageSize){
        return contentService.searchPage(keyword,pageNo,pageSize);
    }
}
