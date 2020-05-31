package ltd.xx.mall.controller.mall;

import ltd.xx.mall.common.Constants;
import ltd.xx.mall.common.ServiceResultEnum;
import ltd.xx.mall.controller.vo.XxMallShoppingCartItemVO;
import ltd.xx.mall.controller.vo.XxMallUserVO;
import ltd.xx.mall.entity.XxMallShoppingCartItem;
import ltd.xx.mall.service.XxMallShoppingCartService;
import ltd.xx.mall.util.Result;
import ltd.xx.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    private XxMallShoppingCartService xxMallShoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession httpSession) {
        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<XxMallShoppingCartItemVO> myShoppingCartItems = xxMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(XxMallShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                return "error/error_5xx";
            }
            //总价
            for (XxMallShoppingCartItemVO xxMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += xxMallShoppingCartItemVO.getGoodsCount() * xxMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveNewBeeMallShoppingCartItem(@RequestBody XxMallShoppingCartItem xxMallShoppingCartItem,
                                                 HttpSession httpSession) {
        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        xxMallShoppingCartItem.setUserId(user.getUserId());
        //todo 判断数量
        String saveResult = xxMallShoppingCartService.saveXxMallCartItem(xxMallShoppingCartItem);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateNewBeeMallShoppingCartItem(@RequestBody XxMallShoppingCartItem xxMallShoppingCartItem,
                                                   HttpSession httpSession) {
        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        xxMallShoppingCartItem.setUserId(user.getUserId());
        //todo 判断数量
        String updateResult = xxMallShoppingCartService.updateXxMallCartItem(xxMallShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{newBeeMallShoppingCartItemId}")
    @ResponseBody
    public Result updateNewBeeMallShoppingCartItem(@PathVariable("newBeeMallShoppingCartItemId") Long newBeeMallShoppingCartItemId,
                                                   HttpSession httpSession) {
        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = xxMallShoppingCartService.deleteById(newBeeMallShoppingCartItemId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
                             HttpSession httpSession) {
        int priceTotal = 0;
        XxMallUserVO user = (XxMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<XxMallShoppingCartItemVO> myShoppingCartItems = xxMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (XxMallShoppingCartItemVO xxMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += xxMallShoppingCartItemVO.getGoodsCount() * xxMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        request.setAttribute("goodsCarriage", Constants.GOODS_CARRIAGE);
        return "mall/order-settle";
    }
}
