package ltd.xx.mall.dao;

import ltd.xx.mall.entity.XxMallGoods;
import ltd.xx.mall.entity.StockNumDTO;
import ltd.xx.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XxMallGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(XxMallGoods record);

    int insertSelective(XxMallGoods record);

//    XxMallGoods selectMallById(Long goodsId);

    XxMallGoods selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(XxMallGoods record);

//    int updateByPrimaryKeyWithBLOBs(XxMallGoods record);

//    int updateByPrimaryKey(XxMallGoods record);

    List<XxMallGoods> findNewBeeMallGoodsList(PageQueryUtil pageUtil);

    int getTotalXxMallGoods(PageQueryUtil pageUtil);

    List<XxMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<XxMallGoods> findXxMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalXxMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("newBeeMallGoodsList") List<XxMallGoods> newBeeMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

    int batchDeleteGoodsByIds(@Param("orderIds")Long[] orderIds);

}
