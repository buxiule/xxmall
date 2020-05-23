package ltd.xx.mall.service;

import ltd.xx.mall.controller.vo.XxMallShoppingCartItemVO;
import ltd.xx.mall.entity.XxMallShoppingCartItem;

import java.util.List;

public interface XxMallShoppingCartService {

    /**
     * 保存商品至购物车中
     *
     * @param xxMallShoppingCartItem
     * @return
     */
    String saveXxMallCartItem(XxMallShoppingCartItem xxMallShoppingCartItem);

    /**
     * 修改购物车中的属性
     *
     * @param xxMallShoppingCartItem
     * @return
     */
    String updateXxMallCartItem(XxMallShoppingCartItem xxMallShoppingCartItem);

    /**
     * 获取购物项详情
     *
     * @param newBeeMallShoppingCartItemId
     * @return
     */
    XxMallShoppingCartItem getXxMallCartItemById(Long newBeeMallShoppingCartItemId);

    /**
     * 删除购物车中的商品
     *
     * @param newBeeMallShoppingCartItemId
     * @return
     */
    Boolean deleteById(Long newBeeMallShoppingCartItemId);

    /**
     * 获取我的购物车中的列表数据
     *
     * @param newBeeMallUserId
     * @return
     */
    List<XxMallShoppingCartItemVO> getMyShoppingCartItems(Long newBeeMallUserId);
}
