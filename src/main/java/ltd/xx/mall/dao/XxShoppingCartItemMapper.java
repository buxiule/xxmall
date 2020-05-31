package ltd.xx.mall.dao;

import ltd.xx.mall.entity.NoUseMallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XxShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(NoUseMallShoppingCartItem record);

    int insertSelective(NoUseMallShoppingCartItem record);

    NoUseMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    NoUseMallShoppingCartItem selectByUserIdAndGoodsId(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("goodsId") Long goodsId);

    List<NoUseMallShoppingCartItem> selectByUserId(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("number") int number);

    int selectCountByUserId(Long newBeeMallUserId);

    int updateByPrimaryKeySelective(NoUseMallShoppingCartItem record);

    int updateByPrimaryKey(NoUseMallShoppingCartItem record);

    int deleteBatch(List<Long> ids);
}
