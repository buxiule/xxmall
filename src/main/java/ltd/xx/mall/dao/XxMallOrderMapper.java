package ltd.xx.mall.dao;

import ltd.xx.mall.entity.XxMallOrder;
import ltd.xx.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XxMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(XxMallOrder record);

    int insertSelective(XxMallOrder record);

    XxMallOrder selectByPrimaryKey(Long orderId);

    XxMallOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(XxMallOrder record);

    int updateByPrimaryKey(XxMallOrder record);

    List<XxMallOrder> findXxMallOrderList(PageQueryUtil pageUtil);

    int getTotalXxMallOrders(PageQueryUtil pageUtil);

    List<XxMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}
