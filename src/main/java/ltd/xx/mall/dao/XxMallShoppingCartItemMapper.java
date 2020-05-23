package ltd.xx.mall.dao;

import ltd.xx.mall.entity.XxMallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XxMallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(XxMallShoppingCartItem record);

    int insertSelective(XxMallShoppingCartItem record);

    XxMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    XxMallShoppingCartItem selectByUserIdAndGoodsId(@Param("xxMallUserId") Long newBeeMallUserId, @Param("goodsId") Long goodsId);

    List<XxMallShoppingCartItem> selectByUserId(@Param("xxMallUserId") Long newBeeMallUserId, @Param("number") int number);

    int selectCountByUserId(Long newBeeMallUserId);

    int updateByPrimaryKeySelective(XxMallShoppingCartItem record);

    int updateByPrimaryKey(XxMallShoppingCartItem record);

    int deleteBatch(List<Long> ids);
}
