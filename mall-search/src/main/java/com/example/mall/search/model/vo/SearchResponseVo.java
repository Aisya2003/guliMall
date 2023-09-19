package com.example.mall.search.model.vo;

import com.example.mall.common.model.to.es.SkuUploadESTo;
import com.example.mall.search.model.dto.ESUploadDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResponseVo {
    private List<ESUploadDto> products;
    private List<BrandVo> brands;
    private List<AttrVo> attrs;
    private List<CatalogVo> catalogs;

    private Integer pageNum;
    private Long total;
    private Integer totalPages;
    private List<Integer> pageBar;

    private List<NavVo> navs = new ArrayList<>();
    private List<Long> selectedAttrIds = new ArrayList<>();
    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String linkUrl;
    }
    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }
    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }
}
