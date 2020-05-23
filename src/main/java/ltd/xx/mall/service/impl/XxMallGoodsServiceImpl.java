package ltd.xx.mall.service.impl;

import ltd.xx.mall.common.ServiceResultEnum;
import ltd.xx.mall.controller.vo.XxMallSearchGoodsVO;
import ltd.xx.mall.dao.XxMallGoodsMapper;
import ltd.xx.mall.entity.XxMallGoods;
import ltd.xx.mall.service.XxMallGoodsService;
import ltd.xx.mall.util.BeanUtil;
import ltd.xx.mall.util.PageQueryUtil;
import ltd.xx.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class XxMallGoodsServiceImpl implements XxMallGoodsService {

    @Autowired
    private XxMallGoodsMapper goodsMapper;

    @Override
    public PageResult getXxMallGoodsPage(PageQueryUtil pageUtil) {
        List<XxMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalXxMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveXxMallGoods(XxMallGoods goods) {
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveXxMallGoods(List<XxMallGoods> newBeeMallGoodsList) {
        if (!CollectionUtils.isEmpty(newBeeMallGoodsList)) {
            goodsMapper.batchInsert(newBeeMallGoodsList);
        }
    }

    @Override
    public String updateXxMallGoods(XxMallGoods goods) {
        XxMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public XxMallGoods getXxMallGoodsById(Long id) {

        return goodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult getXxMallGoodsPageById(Long id){
        List<XxMallGoods> goodsList = new ArrayList<>();
        XxMallGoods goods = goodsMapper.selectByPrimaryKey(id);
        List<XxMallSearchGoodsVO> xxMallSearchGoodsVOS = new ArrayList<>();
        int total = 0;
        if (!ObjectUtils.isEmpty(goods)) {
            total++;
            goodsList.add(goods);
            xxMallSearchGoodsVOS = BeanUtil.copyList(goodsList, XxMallSearchGoodsVO.class);
            for (XxMallSearchGoodsVO xxMallSearchGoodsVO : xxMallSearchGoodsVOS) {
                String goodsName = xxMallSearchGoodsVO.getGoodsName();
                String goodsIntro = xxMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    xxMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    xxMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }

        }
        PageResult pageResult = new PageResult(xxMallSearchGoodsVOS, total, 1, 1);
        return pageResult;
    }

    @Override
    public Boolean batchDeleteGoodsByIds(Long[] ids){
        return goodsMapper.batchDeleteGoodsByIds(ids) > 0;
    }

    @Override
    public PageResult searchXxMallGoods(PageQueryUtil pageUtil) {
        List<XxMallGoods> goodsList = goodsMapper.findXxMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalXxMallGoodsBySearch(pageUtil);
        List<XxMallSearchGoodsVO> xxMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            xxMallSearchGoodsVOS = BeanUtil.copyList(goodsList, XxMallSearchGoodsVO.class);
            for (XxMallSearchGoodsVO xxMallSearchGoodsVO : xxMallSearchGoodsVOS) {
                String goodsName = xxMallSearchGoodsVO.getGoodsName();
                String goodsIntro = xxMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    xxMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    xxMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(xxMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
