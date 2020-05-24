package ltd.xx.mall.service;

import ltd.xx.mall.entity.XxMallGoods;
import ltd.xx.mall.util.PageQueryUtil;
import ltd.xx.mall.util.PageResult;

import java.util.List;

public interface XxMallGoodsService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getXxMallGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String saveXxMallGoods(XxMallGoods goods);

    /**
     * 批量新增商品数据
     *
     * @param newBeeMallGoodsList
     * @return
     */
    void batchSaveXxMallGoods(List<XxMallGoods> newBeeMallGoodsList);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updateXxMallGoods(XxMallGoods goods);

    /**
     * 查询商品
     *
     * @param
     * @return
     */
    PageResult getXxMallGoodsPageById(PageQueryUtil pageUtil, Long id);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    XxMallGoods getXxMallGoodsById(Long id);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids,int sellStatus);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchDeleteGoodsByIds(Long[] ids);

    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResult searchXxMallGoods(PageQueryUtil pageUtil);
}
