package com.leyou.web;

import com.leyou.service.PageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;

@Controller
public class PageController {

    @Resource
    private PageService pageService;

    /**
     * 跳转到商品详情页
     * @param model
     * @param id
     * @return
     */
    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id")Long id,Model model){
        // 加载所需的数据并将数据放入模型
        model.addAllAttributes(pageService.itemPage(id));
        // 页面静态化
        pageService.asyncExcute(id);
        return "item";      //templates/item.html
    }
}
