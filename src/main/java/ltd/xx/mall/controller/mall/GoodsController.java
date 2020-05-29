package ltd.xx.mall.controller.mall;

import ltd.xx.mall.common.Constants;
import ltd.xx.mall.common.XxMallException;
import ltd.xx.mall.controller.vo.XxMallGoodsDetailVO;
import ltd.xx.mall.controller.vo.SearchPageCategoryVO;
import ltd.xx.mall.entity.XxMallGoods;
import ltd.xx.mall.service.XxMallCategoryService;
import ltd.xx.mall.service.XxMallGoodsService;
import ltd.xx.mall.common.ServiceResultEnum;
import ltd.xx.mall.util.BeanUtil;
import ltd.xx.mall.util.PageQueryUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class GoodsController {

    @Resource
    private XxMallGoodsService xxMallGoodsService;
    @Resource
    private XxMallCategoryService xxMallCategoryService;

    @GetMapping({"/search", "/search.html"})
    public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        //封装分类数据
        if (params.containsKey("goodsCategoryId") && !StringUtils.isEmpty(params.get("goodsCategoryId") + "")) {
            Long categoryId = Long.valueOf(params.get("goodsCategoryId") + "");
            SearchPageCategoryVO searchPageCategoryVO = xxMallCategoryService.getCategoriesForSearch(categoryId);
            if (searchPageCategoryVO != null) {
                request.setAttribute("goodsCategoryId", categoryId);
                request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);
            }
        }
        //封装参数供前端回显
        if (params.containsKey("orderBy") && !StringUtils.isEmpty(params.get("orderBy") + "")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }
        String keyword = "";
        //对keyword做过滤 去掉空格
        if (params.containsKey("keyword") && !StringUtils.isEmpty((params.get("keyword") + "").trim())) {
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword", keyword);
        params.put("keyword", keyword);
        //封装商品数据
        params.put("goodsSellStatus", Constants.SELL_STATUS_UP);
        //搜索上架状态下的商品
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("pageResult", xxMallGoodsService.searchXxMallGoods(pageUtil));
        return "mall/search";
    }

    @GetMapping("/goods/detail/{goodsId}")
    public String detailPage(@PathVariable("goodsId") Long goodsId, HttpServletRequest request) {
        if (goodsId < 1) {
            return "error/error_5xx";
        }
        XxMallGoods goods = xxMallGoodsService.getXxMallGoodsById(goodsId);
        if (goods == null) {
            XxMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()) {
            XxMallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        XxMallGoodsDetailVO goodsDetailVO = new XxMallGoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        request.setAttribute("goodsDetail", goodsDetailVO);
        return "mall/detail";
    }

}
