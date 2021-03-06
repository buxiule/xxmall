package ltd.xx.mall.controller.admin;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import ltd.xx.mall.common.Constants;
import ltd.xx.mall.common.PayStatusEnum;
import ltd.xx.mall.common.ServiceResultEnum;
import ltd.xx.mall.common.XxMallOrderStatusEnum;
import ltd.xx.mall.config.AlipayConfig;
import ltd.xx.mall.controller.common.BusinessException;
import ltd.xx.mall.controller.vo.CountMallVO;
import ltd.xx.mall.controller.vo.XxMallOrderItemVO;
import ltd.xx.mall.controller.vo.XxMallUserVO;
import ltd.xx.mall.entity.XxMallOrder;
import ltd.xx.mall.service.XxMallOrderService;
import ltd.xx.mall.util.HttpUtil;
import ltd.xx.mall.util.PageQueryUtil;
import ltd.xx.mall.util.Result;
import ltd.xx.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.*;


@Controller
@RequestMapping("/admin")
public class XxMallOrderController {

    @Resource
    private XxMallOrderService xxMallOrderService;


    @GetMapping("/orders")
    public String ordersPage(HttpServletRequest request) {
        request.setAttribute("path", "orders");
        return "admin/xx_mall_order";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/orders/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(xxMallOrderService.getNewBeeMallOrdersPage(pageUtil));
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/orders/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody XxMallOrder xxMallOrder) {
        if (Objects.isNull(xxMallOrder.getTotalPrice())
                || Objects.isNull(xxMallOrder.getOrderId())
                || xxMallOrder.getOrderId() < 1
                || xxMallOrder.getTotalPrice() < 1
                || StringUtils.isEmpty(xxMallOrder.getUserAddress())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = xxMallOrderService.updateOrderInfo(xxMallOrder);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/order-items/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        List<XxMallOrderItemVO> orderItems = xxMallOrderService.getOrderItems(id);
        if (!CollectionUtils.isEmpty(orderItems)) {
            return ResultGenerator.genSuccessResult(orderItems);
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }
    /**
     * 报表
     */
    @GetMapping("/table")
    public String index(HttpServletRequest request) {
        List<CountMallVO> countMallVOS = xxMallOrderService.countMallTransactionAmount();
        ArrayList<String> xAxisData = new ArrayList<>();
        ArrayList<Long> seriesData = new ArrayList<>();
        for (CountMallVO countMallVO : countMallVOS) {
            xAxisData.add(countMallVO.getDays());
            seriesData.add(countMallVO.getTotalPrice());
        }
        request.setAttribute("xAxisData", xAxisData);
        request.setAttribute("seriesData", seriesData);
        request.setAttribute("path", "table");
        return "admin/table";
    }
    /**
     * 配货
     */
    @RequestMapping(value = "/orders/checkDone", method = RequestMethod.POST)
    @ResponseBody
    public Result checkDone(@RequestBody Long[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = xxMallOrderService.checkDone(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 出库
     */
    @RequestMapping(value = "/orders/checkOut", method = RequestMethod.POST)
    @ResponseBody
    public Result checkOut(@RequestBody Long[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = xxMallOrderService.checkOut(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 关闭订单
     */
    @RequestMapping(value = "/orders/close", method = RequestMethod.POST)
    @ResponseBody
    public Result closeOrder(@RequestBody Long[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = xxMallOrderService.closeOrder(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }




}
