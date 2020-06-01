package ltd.xx.mall.controller.mall;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import ltd.xx.mall.common.*;
import ltd.xx.mall.config.AlipayConfig;
import ltd.xx.mall.controller.common.BusinessException;
import ltd.xx.mall.controller.vo.XxMallOrderDetailVO;
import ltd.xx.mall.controller.vo.XxMallShoppingCartItemVO;
import ltd.xx.mall.controller.vo.XxMallUserVO;
import ltd.xx.mall.entity.XxMallOrder;
import ltd.xx.mall.service.XxMallOrderService;
import ltd.xx.mall.service.XxMallShoppingCartService;
import ltd.xx.mall.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
public class OrderController {

    @Resource
    private XxMallShoppingCartService xxMallShoppingCartService;
    @Resource
    private XxMallOrderService xxMallOrderService;
    @Autowired
    private AlipayConfig alipayConfig;

    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        XxMallOrderDetailVO orderDetailVO = xxMallOrderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        if (orderDetailVO == null) {
            return "error/error_5xx";
        }
        orderDetailVO.setGoodsCarriage(Constants.GOODS_CARRIAGE);
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }

    @GetMapping("/alipayDeal")
    public String alipayDeal(HttpServletRequest request, @RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType, HttpSession httpSession) {
        XxMallOrder order = judgeOrderUserId(orderNo, httpSession);
        if (ObjectUtils.isEmpty(order)) {
            return "error/error_5xx";
        }
        String payResult = xxMallOrderService.paySuccess(orderNo, payType);
        if (!ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return "error/error_5xx";
        }
        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        XxMallOrderDetailVO orderDetailVO = xxMallOrderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        if (orderDetailVO == null) {
            return "error/error_5xx";
        }
        orderDetailVO.setGoodsCarriage(Constants.GOODS_CARRIAGE);
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }


    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", xxMallOrderService.getMyOrders(pageUtil));
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }



    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession httpSession) {
        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<XxMallShoppingCartItemVO> myShoppingCartItems = xxMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (StringUtils.isEmpty(user.getAddress().trim())) {
            //无收货地址
            XxMallException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
        }
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物车中无数据则跳转至错误页
            XxMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }
        //保存订单并返回订单号
        String saveOrderResult = xxMallOrderService.saveOrder(user, myShoppingCartItems);
        //跳转到订单详情页
        return "redirect:/orders/" + saveOrderResult;
    }

    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String cancelOrderResult = xxMallOrderService.cancelOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String finishOrderResult = xxMallOrderService.finishOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession) {
        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        XxMallOrder xxMallOrder = xxMallOrderService.getNewBeeMallOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", xxMallOrder.getTotalPrice());
        return "mall/pay-select";
    }

//    @GetMapping("/payPage")
//    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType) {
//        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
//        XxMallOrder xxMallOrder = xxMallOrderService.getNewBeeMallOrderByOrderNo(orderNo);
//        //todo 判断订单userId
//        //todo 判断订单状态
//        request.setAttribute("orderNo", orderNo);
//        request.setAttribute("totalPrice", xxMallOrder.getTotalPrice());
//        if (payType == 1) {
//            return "alipay11";
//        } else {
//            return "mall/wxpay";
//        }
//    }
    /**
     * 判断订单关联用户id和当前登陆用户是否一致
     *
     * @param orderNo
     * @param session
     * @return
     */
    private XxMallOrder judgeOrderUserId(String orderNo, HttpSession session) {
        XxMallUserVO mallUserVO = (XxMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        XxMallOrder order = xxMallOrderService.findOrderByNo(orderNo);
        //todo 判断订单userId
        if (order == null || !order.getUserId().equals(mallUserVO.getUserId())) {
            throw new BusinessException("当前订单用户异常");
        }
        return order;
    }

    /**
     * 支付宝沙箱测试AlipayConfig
     */
    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession session, @RequestParam("payType") int payType) throws UnsupportedEncodingException {
        XxMallOrder order = judgeOrderUserId(orderNo, session);
        //todo 判断订单状态
        if (order.getOrderStatus() != XxMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()
                || order.getPayStatus() != PayStatusEnum.PAY_ING.getPayStatus()) {
            throw new BusinessException("订单结算异常");
        }
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", order.getTotalPrice());
        if (payType == 1) {
            request.setCharacterEncoding("utf-8");
            // 初始化
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getGateway(), alipayConfig.getAppId(),
                    alipayConfig.getMerchant_private_key(), alipayConfig.getFormat(), alipayConfig.getCharset(), alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getSigntype());
            // 创建API对应的request
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            // 在公共参数中设置回跳和通知地址
            String url = HttpUtil.getRequestContext(request);
            alipayRequest.setReturnUrl(url + "/alipayDeal?orderNo=" + order.getOrderNo() + "&payType=" + 1);
//            alipayRequest.setReturnUrl(url + "/orders/" + order.getOrderNo());
            alipayRequest.setNotifyUrl(url + "/alipaySuccess" );

            // 填充业务参数

            // 必填
            // 商户订单号，需保证在商户端不重复
            String out_trade_no = order.getOrderNo(); //+ new Random().nextInt(9999);
            // 销售产品码，与支付宝签约的产品码名称。目前仅支持FAST_INSTANT_TRADE_PAY
            String product_code = "FAST_INSTANT_TRADE_PAY";
            // 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]。
            String total_amount = order.getTotalPrice() + "";
            // 订单标题
            String subject = "支付宝测试";

            // 选填
            // 商品描述，可空
            String body = "商品描述";

            alipayRequest.setBizContent("{" + "\"out_trade_no\":\"" + out_trade_no + "\"," + "\"product_code\":\""
                    + product_code + "\"," + "\"total_amount\":\"" + total_amount + "\"," + "\"subject\":\"" + subject
                    + "\"," + "\"body\":\"" + body + "\"}");
            // 请求
            String form = "";
            try {
                form = alipayClient.pageExecute(alipayRequest).getBody();//调用SDK生成表单
                request.setAttribute("form", form);
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
            return "mall/alipay";
        } else {
            return "mall/wxpay";
        }
    }

//    @GetMapping("/paySuccess")
//    @ResponseBody
//    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
//        String payResult = xxMallOrderService.paySuccess(orderNo, payType);
//        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
//            return ResultGenerator.genSuccessResult();
//        } else {
//            return ResultGenerator.genFailResult(payResult);
//        }
//    }

    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
        String payResult = xxMallOrderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

    @PostMapping("/alipaySuccess")
    @ResponseBody
    public Result paySuccess(HttpServletRequest request)  {

//        Map params = MallUtils.aliPayGetParams(request.getParameterMap());
//        if (params == null) {
//            return ResultGenerator.genFailResult("Fail");
//        }
        if (request == null) {
            return ResultGenerator.genFailResult("Fail");
        }
        String orderNo = null;
        try {
            orderNo = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (orderNo == null) {
            return ResultGenerator.genFailResult("miss order");
        }
        String payResult = xxMallOrderService.paySuccess(orderNo, 1);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }
//        boolean signVerified = AlipaySignature.rsaCheckV1(params,alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSigntype());
//        if(signVerified) {//验证成功
//            //商户订单号
//            String out_trade_no = new String(request.getParameter("").getBytes("ISO-8859-1"),"UTF-8");
//
//            //支付宝交易号
////            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
//
//            //交易状态
////            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
//
////            if(trade_status.equals("TRADE_FINISHED")){
////                //判断该笔订单是否在商户网站中已经做过处理
////                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
////                //如果有做过处理，不执行商户的业务程序
////
////                //注意：
////                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
////            }else if (trade_status.equals("TRADE_SUCCESS")){
////                //判断该笔订单是否在商户网站中已经做过处理
////                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
////                //如果有做过处理，不执行商户的业务程序
////
////                //注意：
////                //付款完成后，支付宝系统发送该交易状态通知
////            }
//
//
//        }else {//验证失败
//            out.println("fail");
//
//            //调试用，写文本函数记录程序运行情况是否正常
//            //String sWord = AlipaySignature.getSignCheckContentV1(params);
//            //AlipayConfig.logResult(sWord);
//        }
//    }
//    @GetMapping("/paySuccess")
//    @ResponseBody
//    public R paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType, HttpSession session) {
//        XxMallOrder order = judgeOrderUserId(orderNo, session);
//        if (order != null) {
//            //todo 判断订单状态
//            if (order.getOrderStatus() != XxMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()
//                    || order.getPayStatus() != PayStatusEnum.PAY_ING.getPayStatus()) {
//                throw new BusinessException("订单关闭异常");
//            }
//            order.setOrderStatus((byte) XxMallOrderStatusEnum.OREDER_PAID.getOrderStatus());
//            order.setPayType((byte) payType);
//            order.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
//            order.setPayTime(new Date());
//            order.setUpdateTime(new Date());
//            xxMallOrderService.updateOrderInfo(order);
//        }
//        return R.success();
//    }

}
