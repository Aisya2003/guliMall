package com.example.mall.product.web;

import com.example.mall.product.model.vo.Catelog2Vo;
import com.example.mall.product.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    private final CategoryService.Web categoryServiceWeb;

    public IndexController(CategoryService.Web categoryServiceWeb) {
        this.categoryServiceWeb = categoryServiceWeb;
    }

    @GetMapping({"/", "/index.html"})
    public String homePage(Model model) {
        model.addAttribute("categories", categoryServiceWeb.getRootCategories());
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCategoryJson() {
        return categoryServiceWeb.getAllCategoriesJson();
    }
}
