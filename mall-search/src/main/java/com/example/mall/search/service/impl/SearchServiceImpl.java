package com.example.mall.search.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.common.model.constant.ProductConstant;
import com.example.mall.common.model.constant.SystemConstant;
import com.example.mall.common.model.exception.ESException;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.es.SkuUploadESTo;
import com.example.mall.search.feign.ProductFeignClient;
import com.example.mall.search.model.constant.ESConstant;
import com.example.mall.search.model.dto.ESUploadDto;
import com.example.mall.search.model.vo.SearchRequestParamVo;
import com.example.mall.search.model.vo.SearchResponseVo;
import com.example.mall.search.service.SearchService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService, SearchService.Web {
    private final RestHighLevelClient esClient;
    private final ProductFeignClient productFeignClient;

    public SearchServiceImpl(RestHighLevelClient esClient, ProductFeignClient productFeignClient) {
        this.esClient = esClient;
        this.productFeignClient = productFeignClient;
    }

    @Override
    public SearchResponseVo search(SearchRequestParamVo requestParamVo) {
        SearchRequest searchRequest = this.buildSearchSource(requestParamVo);
        SearchResponse searchResponse = null;
        try {
            searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            ESException.error("搜索时出错:" + e.getMessage());
        }


        return handleSearchResponse(searchResponse, requestParamVo);
    }

    /**
     * 处理hit内容
     * response(hits){
     * took,
     * time_out,
     * hits{
     * total,
     * max_score,
     * hits[
     * {
     * _id,
     * _score,
     * _source{
     * //实际检索的数据
     * }
     * highlight{
     * //高亮数据
     * }
     * }
     * ]
     * },
     * aggregations{
     * 聚合名称{
     * buckets[
     * {聚合数据，包含子聚合}
     * ]
     * }
     * }
     * }
     *
     * @param searchResponse
     * @return
     */
    private SearchResponseVo handleSearchResponse(SearchResponse searchResponse, SearchRequestParamVo requestParamVo) {
        SearchResponseVo responseVo = new SearchResponseVo();
        SearchHits hits = searchResponse.getHits();
        //设置分页参数
        //总hit数
        long totalHits = hits.getTotalHits().value;
        responseVo.setTotal(totalHits);
        //总页码
        responseVo.setTotalPages((int) Math.ceil((totalHits / (double) ESConstant.DEFAULT_PAGE_SIZE)));
        //当前页码
        responseVo.setPageNum(requestParamVo.getPageNum());

        //命中的数据
        SearchHit[] sourceHits = hits.getHits();
        if (sourceHits == null || sourceHits.length == 0) {
            return null;
        }
        //封装商品信息
        List<ESUploadDto> productEntityList = new ArrayList<>();
        for (SearchHit sourceHit : sourceHits) {
            String sourceJson = sourceHit.getSourceAsString();
            ESUploadDto esUploadDto = JSON.parseObject(sourceJson, ESUploadDto.class);
            if (requestParamVo.getKeyword() != null) {
                //高亮
                Optional<Text[]> texts = Optional.ofNullable(sourceHit.getHighlightFields())
                        .map(stringHighlightFieldMap -> stringHighlightFieldMap.get(ESConstant.SEARCH_KEYWORD_ATTR))
                        .map(HighlightField::getFragments)
                        .filter(arr -> arr.length > 0);
                texts.ifPresent(text -> esUploadDto.setSkuTitle(text[0].string()));
            }
            productEntityList.add(esUploadDto);
        }
        responseVo.setProducts(productEntityList);
        //封装聚合信息
        Aggregations aggregations = searchResponse.getAggregations();
        //封装分类聚合信息
        List<SearchResponseVo.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalogIdAggregations = aggregations.get(ESConstant.Aggregation.NAME_CATALOG_ID);
        for (Terms.Bucket bucket : catalogIdAggregations.getBuckets()) {
            SearchResponseVo.CatalogVo catalogVo = new SearchResponseVo.CatalogVo();
            //id
            catalogVo.setCatalogId(bucket.getKeyAsNumber().longValue());
            //name
            ParsedStringTerms catalogIdSubAggregations = bucket.getAggregations().get(ESConstant.Aggregation.NAME_CATALOG_NAME);
            //一个id对应一个name
            catalogVo.setCatalogName(catalogIdSubAggregations.getBuckets().get(0).getKeyAsString());

            //加入分类集合
            catalogVos.add(catalogVo);
        }
        //分类信息
        responseVo.setCatalogs(catalogVos);
        //封装品牌聚合信息
        List<SearchResponseVo.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brandIdAggregations = aggregations.get(ESConstant.Aggregation.NAME_BRAND_ID);
        for (Terms.Bucket bucket : brandIdAggregations.getBuckets()) {
            SearchResponseVo.BrandVo brandVo = new SearchResponseVo.BrandVo();
            //brandId
            brandVo.setBrandId(bucket.getKeyAsNumber().longValue());
            Aggregations brandIdSubAggregations = bucket.getAggregations();
            //brandName
            ParsedStringTerms brandNameTerms = brandIdSubAggregations.get(ESConstant.Aggregation.NAME_BRAND_NAME);
            brandVo.setBrandName(brandNameTerms.getBuckets().get(0).getKeyAsString());
            //brandImg
            ParsedStringTerms brandImgTerms = brandIdSubAggregations.get(ESConstant.Aggregation.NAME_BRAND_IMG);
            brandVo.setBrandImg(brandImgTerms.getBuckets().get(0).getKeyAsString());

            brandVos.add(brandVo);
        }
        responseVo.setBrands(brandVos);
        //封装属性聚合信息
        List<SearchResponseVo.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attrAggregations = aggregations.get(ESConstant.Aggregation.NAME_ATTR_PATH);
        Aggregations attrSubAggregations = attrAggregations.getAggregations();
        //attrId
        ParsedLongTerms idSubAggregation = attrSubAggregations.get(ESConstant.Aggregation.NAME_ATTR_ID);
        for (Terms.Bucket bucket : idSubAggregation.getBuckets()) {
            SearchResponseVo.AttrVo attrVo = new SearchResponseVo.AttrVo();
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            //attrName
            ParsedStringTerms attrNameSubAggregation = bucket.getAggregations().get(ESConstant.Aggregation.NAME_ATTR_NAME);
            attrVo.setAttrName(attrNameSubAggregation.getBuckets().get(0).getKeyAsString());
            //attrValue
            ParsedStringTerms attrValueSubAggregation = bucket.getAggregations().get(ESConstant.Aggregation.NAME_ATTR_VALUE);
            List<String> attrValueList = new ArrayList<>();
            for (Terms.Bucket attrValueSubAggregationBucket : attrValueSubAggregation.getBuckets()) {
                attrValueList.add(attrValueSubAggregationBucket.getKeyAsString());
            }
            attrVo.setAttrValue(attrValueList);
            attrVos.add(attrVo);
        }
        responseVo.setAttrs(attrVos);

        //页码导航
        List<Integer> pageBar = new ArrayList<>();
        for (int i = 1; i <= responseVo.getTotalPages(); i++) {
            pageBar.add(i);
        }
        responseVo.setPageBar(pageBar);


        //属性面包屑导航
        List<String> attrs = requestParamVo.getAttrs();
        if (attrs == null || attrs.isEmpty()) {
            return responseVo;
        }
        List<SearchResponseVo.NavVo> navVoList = attrs.stream()
                .map(attrValue -> {
                    String[] split = attrValue.split(ESConstant.PRICE_RANGE_SPLIT_SYMBOL);
                    SearchResponseVo.NavVo navVo = new SearchResponseVo.NavVo();
                    navVo.setNavValue(split[1]);
                    //远程调用获取属性名称
                    String attrName = productFeignClient.attrInfo(Long.valueOf(split[0]));
                    responseVo.getSelectedAttrIds().add(Long.valueOf(split[0]));
                    if (StringUtils.isNotBlank(attrName)) {
                        navVo.setNavName(attrName);
                    } else {
                        navVo.setNavName(split[0]);
                    }
                    String replace = handleRequestUrlParams(requestParamVo, attrValue, ESConstant.FILTER_ATTRS);
                    navVo.setLinkUrl(SystemConstant.MALL_SEARCH_HOME + "?" + replace);
                    return navVo;
                }).collect(Collectors.toList());
        //品牌面包屑导航
        List<Long> brandList = requestParamVo.getBrandId();
        if (brandList != null && !brandList.isEmpty()) {
            SearchResponseVo.NavVo navVo = new SearchResponseVo.NavVo();
            navVo.setNavName("品牌");
            //远程调用获取品牌名称
            List<String> brandNames = productFeignClient.brandNames(brandList);
            StringJoiner stringJoiner = new StringJoiner(";");
            String replace = null;
            for (String brandName : brandNames) {
                replace = handleRequestUrlParams(requestParamVo, brandName, ESConstant.FILTER_BRAND_ID);
                stringJoiner.add(brandName);
            }
            navVo.setNavValue(stringJoiner.toString());
            navVo.setLinkUrl(SystemConstant.MALL_SEARCH_HOME + "?" + replace);
            navVoList.add(navVo);
        }
        responseVo.setNavs(navVoList);
        return responseVo;
    }

    private String handleRequestUrlParams(SearchRequestParamVo requestParamVo, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            encode = encode.replace("+", "%20");
            encode = encode.replace("%2B", "+");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return requestParamVo.get_url().replaceAll("&?" + key + "=" + encode, "");
    }

    /**
     * 构建搜索source
     *
     * @param requestParamVo
     * @return
     */
    private SearchRequest buildSearchSource(SearchRequestParamVo requestParamVo) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建boolQuery，搜索条件
        /* 构建查询条件开始 */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        String conditionText = null;
        //must
        //商品标题
        conditionText = requestParamVo.getKeyword();
        if (StringUtils.isNotBlank(conditionText)) {
            boolQuery.must(QueryBuilders.matchQuery(ESConstant.SEARCH_KEYWORD_ATTR, conditionText));
        }
        //filter
        //分类id
        conditionText = requestParamVo.getCatalog3Id();
        if (StringUtils.isNotBlank(conditionText)) {
            boolQuery.filter(QueryBuilders.termQuery(ESConstant.FILTER_CATALOG_ID, conditionText));
        }
        //品牌id
        List<Long> brandId = requestParamVo.getBrandId();
        if (brandId != null && !brandId.isEmpty()) {
            boolQuery.filter(QueryBuilders.termsQuery(ESConstant.FILTER_BRAND_ID, brandId));
        }
        //属性
        List<String> attrs = requestParamVo.getAttrs();
        if (attrs != null && !attrs.isEmpty()) {
            for (String attr : attrs) {
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
                String[] split = attr.split(ESConstant.ATTR_ID_SPLIT_SYMBOL);
                if (split.length == 2) {//校验参数格式
                    String attrId = split[0];
                    String[] attrValues = split[1].split(ESConstant.ATTR_VALUE_SPLIT_SYMBOL);
                    nestedBoolQueryBuilder.must(QueryBuilders.termQuery(ESConstant.FILTER_ATTRS_ID, attrId));
                    nestedBoolQueryBuilder.must(QueryBuilders.termsQuery(ESConstant.FILTER_ATTRS_VALUE, attrValues));
                    //存在多个属性，因此需要构建多个nestedQueryBuilder
                    NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(ESConstant.FILTER_ATTRS, nestedBoolQueryBuilder, ScoreMode.None);
                    boolQuery.filter(nestedQueryBuilder);
                }
            }
        }
        //库存
        Integer condtionNum = requestParamVo.getHasStock();
        if (condtionNum != null) {
            boolQuery.filter(QueryBuilders.termQuery(ESConstant.FILTER_HAS_STOCK, condtionNum > 0));
        }
        //价格区间
        conditionText = requestParamVo.getSkuPrice();
        if (StringUtils.isNotBlank(conditionText)) {
            RangeQueryBuilder priceRangBuilder = QueryBuilders.rangeQuery(ESConstant.FILTER_PRICE);
            String[] split = conditionText.split(ESConstant.PRICE_RANGE_SPLIT_SYMBOL);
            if (split.length == 1) {//大于or小于
                if (conditionText.startsWith(ESConstant.PRICE_RANGE_SPLIT_SYMBOL)) {//小于
                    priceRangBuilder.lte(split[0]);
                } else if (conditionText.endsWith(ESConstant.PRICE_RANGE_SPLIT_SYMBOL)) {//大于
                    priceRangBuilder.gte(split[0]);
                }
            } else if (split.length == 2) {//大于and小于
                priceRangBuilder
                        .gte(split[0])
                        .lte(split[1]);
            }
            boolQuery.filter(priceRangBuilder);
        }
        searchSourceBuilder.query(boolQuery);
        /* 构建查询条件结束 */
        //排序
        conditionText = requestParamVo.getSort();
        if (StringUtils.isNotBlank(conditionText)) {
            String[] split = conditionText.split(ESConstant.SORT_SPLIT_SYMBOL);
            if (split.length == 2) {
                String sortType = split[1];
                if (sortType.equalsIgnoreCase(ESConstant.SORT_ASC) || sortType.equalsIgnoreCase(ESConstant.SORT_DESC)) {
                    searchSourceBuilder.sort(split[0], sortType.equalsIgnoreCase(ESConstant.SORT_ASC) ? SortOrder.ASC : SortOrder.DESC);
                }
            }
        }
        //分页
        condtionNum = requestParamVo.getPageNum();
        if (condtionNum != null && condtionNum > 0) {
            searchSourceBuilder.from((condtionNum - 1) * ESConstant.DEFAULT_PAGE_SIZE);
            searchSourceBuilder.size(ESConstant.DEFAULT_PAGE_SIZE);
        }
        //高亮
        //存在模糊查询需要高亮
        if (StringUtils.isNotBlank(requestParamVo.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field(ESConstant.SEARCH_KEYWORD_ATTR);
            highlightBuilder.preTags(ESConstant.HIGHLIGHT_PREFIX);
            highlightBuilder.postTags(ESConstant.HIGHLIGHT_SUFFIX);
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        //aggregation
        //品牌聚合
        TermsAggregationBuilder brandTermsAggregation = AggregationBuilders
                .terms(ESConstant.Aggregation.NAME_BRAND_ID)
                .field(ESConstant.Aggregation.FIELD_BRAND_ID)
                .size(ESConstant.Aggregation.DEFAULT_SIZE);
        //子聚合name
        brandTermsAggregation.subAggregation(AggregationBuilders
                .terms(ESConstant.Aggregation.NAME_BRAND_NAME)
                .field(ESConstant.Aggregation.FIELD_BRAND_NAME)
                .size(1));
        //子聚合img
        brandTermsAggregation.subAggregation(AggregationBuilders
                .terms(ESConstant.Aggregation.NAME_BRAND_IMG)
                .field(ESConstant.Aggregation.FIELD_BRAND_IMG)
                .size(1));
        //分类聚合
        //分类id聚合
        TermsAggregationBuilder catalogTermsAggregation = AggregationBuilders
                .terms(ESConstant.Aggregation.NAME_CATALOG_ID)
                .field(ESConstant.Aggregation.FIELD_CATALOG_ID)
                .size(ESConstant.Aggregation.DEFAULT_SIZE);
        //子聚合name
        catalogTermsAggregation.subAggregation(AggregationBuilders
                .terms(ESConstant.Aggregation.NAME_CATALOG_NAME)
                .field(ESConstant.Aggregation.FIELD_CATALOG_NAME)
                .size(1));
        //属性聚合
        NestedAggregationBuilder attrNestedAggregation = AggregationBuilders
                .nested(ESConstant.Aggregation.NAME_ATTR_PATH, ESConstant.Aggregation.FIELD_ATTR_PATH);
        //子聚合id
        TermsAggregationBuilder attrIdTermsAggregation = AggregationBuilders
                .terms(ESConstant.Aggregation.NAME_ATTR_ID)
                .field(ESConstant.Aggregation.FIELD_ATTR_ID);
        /*子聚合id的子聚合开始*/
        //子聚合id的子聚合name
        attrIdTermsAggregation.subAggregation(AggregationBuilders
                .terms(ESConstant.Aggregation.NAME_ATTR_NAME)
                .field(ESConstant.Aggregation.FIELD_ATTR_NAME)
                .size(1));
        //子聚合id的子聚合value
        attrIdTermsAggregation.subAggregation(AggregationBuilders
                .terms(ESConstant.Aggregation.NAME_ATTR_VALUE)
                .field(ESConstant.Aggregation.FIELD_ATTR_VALUE)
                .size(ESConstant.Aggregation.DEFAULT_SIZE));
        /*子聚合id的子聚合结束*/
        attrNestedAggregation.subAggregation(attrIdTermsAggregation);

        searchSourceBuilder.aggregation(brandTermsAggregation);
        searchSourceBuilder.aggregation(catalogTermsAggregation);
        searchSourceBuilder.aggregation(attrNestedAggregation);
        System.out.println(searchSourceBuilder);
        return new SearchRequest(new String[]{ESConstant.PRODUCT_INDEX}, searchSourceBuilder);
    }
}
