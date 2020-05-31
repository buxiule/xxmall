package ltd.xx.mall.controller.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 首页轮播图VO
 */
@Data
public class XxMallIndexCarouselVO implements Serializable {

    private String carouselUrl;

    private String redirectUrl;

    private Long goodsId;

//    public String getCarouselUrl() {
//        return carouselUrl;
//    }
//
//    public void setCarouselUrl(String carouselUrl) {
//        this.carouselUrl = carouselUrl;
//    }
//
//    public String getRedirectUrl() {
//        return redirectUrl;
//    }
//
//    public void setRedirectUrl(String redirectUrl) {
//        this.redirectUrl = redirectUrl;
//    }
}
