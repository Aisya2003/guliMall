package com.example.mall.search.web;

import com.example.mall.search.model.vo.SearchRequestParamVo;
import com.example.mall.search.model.vo.SearchResponseVo;
import com.example.mall.search.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    private final SearchService.Web searchServiceWeb;

    public SearchController(SearchService.Web searchServiceWeb) {
        this.searchServiceWeb = searchServiceWeb;
    }


    @GetMapping("/search.html")
    public String searchHome(SearchRequestParamVo requestParamVo, Model model, HttpServletRequest request) {
        requestParamVo.set_url(request.getQueryString());
        List<String> attrs = requestParamVo.getAttrs();
        if (attrs != null) {
            List<String> newAttrs = new ArrayList<>();
            attrs.forEach(attr -> {
                if (attr.contains("  ")) {
                    newAttrs.add(attr.replaceAll(" {2}", "+ "));
                } else {
                    newAttrs.add(attr);
                }
            });
            attrs = null;
            requestParamVo.setAttrs(newAttrs);
        }
        model.addAttribute("result", searchServiceWeb.search(requestParamVo));
        return "search";
    }
}
