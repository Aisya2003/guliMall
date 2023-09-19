package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.product.model.dto.CategoryDto;
import com.example.mall.product.model.po.Category;
import com.example.mall.product.mapper.CategoryMapper;
import com.example.mall.product.model.vo.Catelog2Vo;
import com.example.mall.product.service.CategoryBrandRelationService;
import com.example.mall.product.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService, CategoryService.Web {
    private final CategoryBrandRelationService categoryBrandRelationService;
    private final StringRedisTemplate stringRedisTemplate;

    public CategoryServiceImpl(CategoryBrandRelationService categoryBrandRelationService, StringRedisTemplate stringRedisTemplate) {
        this.categoryBrandRelationService = categoryBrandRelationService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public List<CategoryDto> listTree() {
        //获取分类大小
        int maxLevel = baseMapper.maxLevel();
        //获取所有的分类
        List<CategoryDto> all = baseMapper.selectList(null)
                .stream()
                .map(category -> {
                    CategoryDto cateGoryDto = new CategoryDto();
                    BeanUtils.copyProperties(category, cateGoryDto);
                    return cateGoryDto;
                })
                .sorted(Comparator.comparingInt(Category::getCatLevel))
                .collect(Collectors.toList());
        Map<Integer, CategoryDto> parentMap = new HashMap<>();
        List<CategoryDto> result = new ArrayList<>();
        //设置子节点
        for (CategoryDto categoryDto : all) {
            int catLevel = categoryDto.getCatLevel();
            int catId = categoryDto.getCatId().intValue();
            //根节点
            if (catLevel == 1) {
                result.add(categoryDto);
                parentMap.put(catId, categoryDto);
            } else if (catLevel < maxLevel) {
                //非叶子节点
                CategoryDto parent = parentMap.get(categoryDto.getParentCid().intValue());
                //建立与其父节点的关系
                List<CategoryDto> children = parent.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                }
                children.add(categoryDto);
                parent.setChildren(children);
                //为当前节点设置子节点
                categoryDto.setChildren(searchChildrenById(catId, all));
                parentMap.put(catId, categoryDto);
            }
        }

        buildChildrenSort(parentMap);

        return result
                .stream()
                .sorted(Comparator.comparingInt(category -> category.getSort() != null ? category.getSort() : 0))
                .collect(Collectors.toList());

    }

    /**
     * 重新排列结果的children
     *
     * @param result
     */
    private void buildChildrenSort(Map<Integer, CategoryDto> result) {
        List<CategoryDto> children = null;
        for (Map.Entry<Integer, CategoryDto> integerCategoryDtoEntry : result.entrySet()) {
            children = integerCategoryDtoEntry.getValue().getChildren();
            if (children != null) {
                children.stream()
                        .sorted(Comparator.comparingInt(category -> category.getSort() != null ? category.getSort() : 0))
                        .close();
            }
        }
    }

    /**
     * 收集子节点
     *
     * @param catId
     * @param all
     * @return
     */
    private List<CategoryDto> searchChildrenById(int catId, List<CategoryDto> all) {
        return all
                .stream()
                .filter(categoryDto -> categoryDto.getParentCid() == catId)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteBatch(List<Long> ids) {
        //逻辑删除
        baseMapper.deleteBatchIds(ids);
    }

    @Override
    public Long[] parentIdPath(Long id) {
        Long[] paths = new Long[3];
        getCategoryPath(id, paths);
        return paths;
    }

    @Override
    @CacheEvict(value = "category", allEntries = true)
    public void updateCascade(Category category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

    }

    @Override
    @Cacheable(value = "category", key = "#root.methodName", sync = true)
    public List<Category> getRootCategories() {
        return this.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentCid, 0));
    }

    /*
            @Override
        public Map<String, List<Catelog2Vo>> getAllCategoriesJson() {
            CacheUtils<Map<String, List<Catelog2Vo>>> cacheUtils = new CacheUtils<>(stringRedisTemplate, true);
            cacheUtils.setRedissonClient(redissonClient,RedisConstant.Product.LOCK_PREFIX + RedisConstant.Product.CATALOG_KEY);
            return cacheUtils.getCache(RedisConstant.Product.CATALOG_KEY, RedisConstant.Product.CATALOG_EXPIRE_TIME, TimeUnit.SECONDS, this::getAllCategoriesJsonFromDb);
        }
     */
    @Override
    @Cacheable(value = "category", key = "#root.methodName", sync = true)
    public Map<String, List<Catelog2Vo>> getAllCategoriesJson() {
        List<Category> allList = this.list();
        //获取一级分类
        return getRootCategories().stream()
                .collect(Collectors.toMap(key -> key.getCatId().toString(), value -> {
                    //封装当前分类下的二级分类
                    Optional<List<Category>> categoryLevel2List = Optional.ofNullable(getCategoriesByParentId(allList, value.getCatId()));
                    //遍历二级分类，封装返回数据
                    List<Catelog2Vo> catelog2VoList = null;
                    if (categoryLevel2List.isPresent()) {
                        catelog2VoList = categoryLevel2List.get().stream()
                                .map(categoryLevel2 -> {
                                            Catelog2Vo catelog2Vo = Catelog2Vo.builder()
                                                    //二级分类的id
                                                    .id(categoryLevel2.getCatId().toString())
                                                    //一级分类的id
                                                    .catalog1Id(value.getCatId().toString())
                                                    .name(categoryLevel2.getName())
                                                    .catalog3List(null)
                                                    .build();
                                            //遍历三级分类，封装三级分类
                                            Optional<List<Category>> cateLevel3List = Optional.ofNullable(getCategoriesByParentId(allList, categoryLevel2.getCatId()));
                                            List<Catelog2Vo.Catelog3Vo> catelog3VoList = null;
                                            if (cateLevel3List.isPresent()) {
                                                catelog3VoList = cateLevel3List.get().stream()
                                                        .map(category ->
                                                                Catelog2Vo.Catelog3Vo.builder()
                                                                        .name(category.getName())
                                                                        .id(category.getCatId().toString())
                                                                        .catalog2Id(categoryLevel2.getCatId().toString())
                                                                        .build()
                                                        )
                                                        .collect(Collectors.toList());
                                            }
                                            catelog2Vo.setCatalog3List(catelog3VoList);
                                            return catelog2Vo;
                                        }
                                )
                                .collect(Collectors.toList());
                    }
                    return catelog2VoList;
                }));
    }

    private List<Category> getCategoriesByParentId(List<Category> allList, Long catId) {
        return allList.stream()
                .filter(category -> category.getParentCid() == catId)
                .collect(Collectors.toList());
    }

    private void getCategoryPath(Long catId, Long[] paths) {
        Category parent = null;
        for (int i = 2; i >= 0; i--) {
            paths[i] = catId;
            parent = baseMapper.selectById(catId);
            long parentCid = parent.getParentCid();
            if (parentCid != 0) {
                catId = parentCid;
            } else {
                break;
            }
        }
    }

}
