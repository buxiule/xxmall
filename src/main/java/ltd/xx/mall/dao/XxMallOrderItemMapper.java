package ltd.xx.mall.dao;

import ltd.xx.mall.entity.XxMallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XxMallOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(XxMallOrderItem record);

    int insertSelective(XxMallOrderItem record);

    XxMallOrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<XxMallOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<XxMallOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<XxMallOrderItem> orderItems);

    int updateByPrimaryKeySelective(XxMallOrderItem record);

    int updateByPrimaryKey(XxMallOrderItem record);
}
