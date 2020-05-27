package ltd.xx.mall.service.impl;

import ltd.xx.mall.common.Constants;
import ltd.xx.mall.common.ServiceResultEnum;
import ltd.xx.mall.controller.vo.XxMallShoppingCartItemVO;
import ltd.xx.mall.dao.XxMallGoodsMapper;
import ltd.xx.mall.dao.XxMallShoppingCartItemMapper;
import ltd.xx.mall.entity.XxMallGoods;
import ltd.xx.mall.entity.XxMallShoppingCartItem;
import ltd.xx.mall.service.XxMallShoppingCartService;
import ltd.xx.mall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class XxMallShoppingCartServiceImpl implements XxMallShoppingCartService {

    @Autowired
    private XxMallShoppingCartItemMapper xxMallShoppingCartItemMapper;

    @Autowired
    private XxMallGoodsMapper xxMallGoodsMapper;

    //todo 修改session中购物项数量

    @Override
    public String saveXxMallCartItem(XxMallShoppingCartItem xxMallShoppingCartItem) {
        XxMallShoppingCartItem temp = xxMallShoppingCartItemMapper.selectByUserIdAndGoodsId(xxMallShoppingCartItem.getUserId(), xxMallShoppingCartItem.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            //todo count = tempCount + 1
            
            temp.setGoodsCount(xxMallShoppingCartItem.getGoodsCount());
            return updateXxMallCartItem(temp);
        }
        XxMallGoods XxMallGoodsInStore = xxMallGoodsMapper.selectByPrimaryKey(xxMallShoppingCartItem.getGoodsId());
        //商品为空
        if (XxMallGoodsInStore == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = xxMallShoppingCartItemMapper.selectCountByUserId(xxMallShoppingCartItem.getUserId());
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //保存记录
        if (xxMallShoppingCartItemMapper.insertSelective(xxMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateXxMallCartItem(XxMallShoppingCartItem xxMallShoppingCartItem) {
        XxMallShoppingCartItem xxMallShoppingCartItemUpdate = xxMallShoppingCartItemMapper.selectByPrimaryKey(xxMallShoppingCartItem.getCartItemId());
        if (xxMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出最大数量
        if (xxMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //todo 数量相同不会进行修改
        //todo userId不同不能修改
        xxMallShoppingCartItemUpdate.setGoodsCount(xxMallShoppingCartItem.getGoodsCount());
        xxMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (xxMallShoppingCartItemMapper.updateByPrimaryKeySelective(xxMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public XxMallShoppingCartItem getXxMallCartItemById(Long newBeeMallShoppingCartItemId) {
        return xxMallShoppingCartItemMapper.selectByPrimaryKey(newBeeMallShoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long newBeeMallShoppingCartItemId) {
        //todo userId不同不能删除
        return xxMallShoppingCartItemMapper.deleteByPrimaryKey(newBeeMallShoppingCartItemId) > 0;
    }

    @Override
    public List<XxMallShoppingCartItemVO> getMyShoppingCartItems(Long newBeeMallUserId) {
        List<XxMallShoppingCartItemVO> xxMallShoppingCartItemVOS = new ArrayList<>();
        List<XxMallShoppingCartItem> xxMallShoppingCartItems = xxMallShoppingCartItemMapper.selectByUserId(newBeeMallUserId,
                Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(xxMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> newBeeMallGoodsIds = xxMallShoppingCartItems.stream().map(XxMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<XxMallGoods> newBeeMallGoods = xxMallGoodsMapper.selectByPrimaryKeys(newBeeMallGoodsIds);
            Map<Long, XxMallGoods> newBeeMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(newBeeMallGoods)) {
                newBeeMallGoodsMap = newBeeMallGoods.stream().collect(Collectors.toMap(XxMallGoods::getGoodsId,
                        Function.identity(), (entity1, entity2) -> entity1));
            }
            for (XxMallShoppingCartItem xxMallShoppingCartItem : xxMallShoppingCartItems) {
                XxMallShoppingCartItemVO xxMallShoppingCartItemVO = new XxMallShoppingCartItemVO();
                BeanUtil.copyProperties(xxMallShoppingCartItem, xxMallShoppingCartItemVO);
                if (newBeeMallGoodsMap.containsKey(xxMallShoppingCartItem.getGoodsId())) {
                    XxMallGoods newBeeMallGoodsTemp = newBeeMallGoodsMap.get(xxMallShoppingCartItem.getGoodsId());
                    xxMallShoppingCartItemVO.setGoodsCoverImg(newBeeMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = newBeeMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    xxMallShoppingCartItemVO.setGoodsName(goodsName);
                    xxMallShoppingCartItemVO.setSellingPrice(newBeeMallGoodsTemp.getSellingPrice());
                    xxMallShoppingCartItemVOS.add(xxMallShoppingCartItemVO);
                }
            }
        }
        return xxMallShoppingCartItemVOS;
    }
}
