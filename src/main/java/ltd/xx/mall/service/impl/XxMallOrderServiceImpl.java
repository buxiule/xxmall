package ltd.xx.mall.service.impl;

import ltd.xx.mall.common.*;
import ltd.xx.mall.controller.vo.*;
import ltd.xx.mall.dao.XxMallGoodsMapper;
import ltd.xx.mall.dao.XxMallOrderItemMapper;
import ltd.xx.mall.dao.XxMallOrderMapper;
import ltd.xx.mall.dao.XxMallShoppingCartItemMapper;
import ltd.xx.mall.entity.XxMallGoods;
import ltd.xx.mall.entity.XxMallOrder;
import ltd.xx.mall.entity.XxMallOrderItem;
import ltd.xx.mall.entity.StockNumDTO;
import ltd.xx.mall.service.XxMallOrderService;
import ltd.xx.mall.util.BeanUtil;
import ltd.xx.mall.util.NumberUtil;
import ltd.xx.mall.util.PageQueryUtil;
import ltd.xx.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class XxMallOrderServiceImpl implements XxMallOrderService {

    @Autowired
    private XxMallOrderMapper xxMallOrderMapper;
    @Autowired
    private XxMallOrderItemMapper xxMallOrderItemMapper;
    @Autowired
    private XxMallShoppingCartItemMapper xxMallShoppingCartItemMapper;
    @Autowired
    private XxMallGoodsMapper xxMallGoodsMapper;

    @Override
    public PageResult getNewBeeMallOrdersPage(PageQueryUtil pageUtil) {
        List<XxMallOrder> xxMallOrders = xxMallOrderMapper.findXxMallOrderList(pageUtil);
        int total = xxMallOrderMapper.getTotalXxMallOrders(pageUtil);
        PageResult pageResult = new PageResult(xxMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(XxMallOrder xxMallOrder) {
        XxMallOrder temp = xxMallOrderMapper.selectByPrimaryKey(xxMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(xxMallOrder.getTotalPrice());
            temp.setUserAddress(xxMallOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (xxMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<XxMallOrder> orders = xxMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (XxMallOrder xxMallOrder : orders) {
                if (xxMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += xxMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (xxMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += xxMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (xxMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<XxMallOrder> orders = xxMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (XxMallOrder xxMallOrder : orders) {
                if (xxMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += xxMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (xxMallOrder.getOrderStatus() != 1 && xxMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += xxMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (xxMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<XxMallOrder> orders = xxMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (XxMallOrder xxMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (xxMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += xxMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (xxMallOrder.getOrderStatus() == 4 || xxMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += xxMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (xxMallOrderMapper.closeOrder(Arrays.asList(ids), XxMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(XxMallUserVO user, List<XxMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(XxMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(XxMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<XxMallGoods> xxMallGoodsList = xxMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        Map<Long, XxMallGoods> newBeeMallGoodsMap = xxMallGoodsList.stream().collect(Collectors.toMap(XxMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (XxMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!newBeeMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                XxMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > newBeeMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                XxMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(xxMallGoodsList)) {
            if (xxMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = xxMallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    XxMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                XxMallOrder xxMallOrder = new XxMallOrder();
                xxMallOrder.setOrderNo(orderNo);
                xxMallOrder.setUserId(user.getUserId());
                xxMallOrder.setUserAddress(user.getAddress());
                //总价
                for (XxMallShoppingCartItemVO xxMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += xxMallShoppingCartItemVO.getGoodsCount() * xxMallShoppingCartItemVO.getSellingPrice();
                }
                priceTotal += Constants.GOODS_CARRIAGE;
                if (priceTotal < 1) {
                    XxMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                xxMallOrder.setTotalPrice(priceTotal);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入沙箱，故该字段暂时设为空字符串
                String extraInfo = "";
                xxMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (xxMallOrderMapper.insertSelective(xxMallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<XxMallOrderItem> xxMallOrderItems = new ArrayList<>();
                    for (XxMallShoppingCartItemVO xxMallShoppingCartItemVO : myShoppingCartItems) {
                        XxMallOrderItem xxMallOrderItem = new XxMallOrderItem();
                        //使用BeanUtil工具类将newBeeMallShoppingCartItemVO中的属性复制到newBeeMallOrderItem对象中
                        BeanUtil.copyProperties(xxMallShoppingCartItemVO, xxMallOrderItem);
                        //NewBeeMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        xxMallOrderItem.setOrderId(xxMallOrder.getOrderId());
                        xxMallOrderItems.add(xxMallOrderItem);
                    }
                    //保存至数据库
                    if (xxMallOrderItemMapper.insertBatch(xxMallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    XxMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                XxMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            XxMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        XxMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public XxMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        XxMallOrder xxMallOrder = xxMallOrderMapper.selectByOrderNo(orderNo);
        if (xxMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            List<XxMallOrderItem> orderItems = xxMallOrderItemMapper.selectByOrderId(xxMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<XxMallOrderItemVO> xxMallOrderItemVOS = BeanUtil.copyList(orderItems, XxMallOrderItemVO.class);
                XxMallOrderDetailVO xxMallOrderDetailVO = new XxMallOrderDetailVO();
                BeanUtil.copyProperties(xxMallOrder, xxMallOrderDetailVO);
                xxMallOrderDetailVO.setOrderStatusString(XxMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(xxMallOrderDetailVO.getOrderStatus()).getName());
                xxMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(xxMallOrderDetailVO.getPayType()).getName());
                xxMallOrderDetailVO.setXxMallOrderItemVOS(xxMallOrderItemVOS);
                return xxMallOrderDetailVO;
            }
        }
        return null;
    }

    @Override
    public XxMallOrder getNewBeeMallOrderByOrderNo(String orderNo) {
        return xxMallOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = xxMallOrderMapper.getTotalXxMallOrders(pageUtil);
        List<XxMallOrder> xxMallOrders = xxMallOrderMapper.findXxMallOrderList(pageUtil);
        List<XxMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(xxMallOrders, XxMallOrderListVO.class);
            //设置订单状态中文显示值
            for (XxMallOrderListVO xxMallOrderListVO : orderListVOS) {
                xxMallOrderListVO.setOrderStatusString(XxMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(xxMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = xxMallOrders.stream().map(XxMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<XxMallOrderItem> orderItems = xxMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<XxMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(XxMallOrderItem::getOrderId));
                for (XxMallOrderListVO xxMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(xxMallOrderListVO.getOrderId())) {
                        List<XxMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(xxMallOrderListVO.getOrderId());
                        //将NewBeeMallOrderItem对象列表转换成NewBeeMallOrderItemVO对象列表
                        List<XxMallOrderItemVO> xxMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, XxMallOrderItemVO.class);
                        xxMallOrderListVO.setXxMallOrderItemVOS(xxMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        XxMallOrder xxMallOrder = xxMallOrderMapper.selectByOrderNo(orderNo);
        if (xxMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            if (xxMallOrderMapper.closeOrder(Collections.singletonList(xxMallOrder.getOrderId()), XxMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        XxMallOrder xxMallOrder = xxMallOrderMapper.selectByOrderNo(orderNo);
        if (xxMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            xxMallOrder.setOrderStatus((byte) XxMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            xxMallOrder.setUpdateTime(new Date());
            if (xxMallOrderMapper.updateByPrimaryKeySelective(xxMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        XxMallOrder xxMallOrder = xxMallOrderMapper.selectByOrderNo(orderNo);
        if (xxMallOrder != null) {
            //todo 订单状态判断 非待支付状态下不进行修改操作
            xxMallOrder.setOrderStatus((byte) XxMallOrderStatusEnum.OREDER_PAID.getOrderStatus());
            xxMallOrder.setPayType((byte) payType);
            xxMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            xxMallOrder.setPayTime(new Date());
            xxMallOrder.setUpdateTime(new Date());
            if (xxMallOrderMapper.updateByPrimaryKeySelective(xxMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<XxMallOrderItemVO> getOrderItems(Long id) {
        XxMallOrder xxMallOrder = xxMallOrderMapper.selectByPrimaryKey(id);
        if (xxMallOrder != null) {
            List<XxMallOrderItem> orderItems = xxMallOrderItemMapper.selectByOrderId(xxMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<XxMallOrderItemVO> xxMallOrderItemVOS = BeanUtil.copyList(orderItems, XxMallOrderItemVO.class);
                return xxMallOrderItemVOS;
            }
        }
        return null;
    }
}
