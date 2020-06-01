package ltd.xx.mall.util;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

public class R {

    private int code;
    private String msg;
    private Map<String, Object> map = new HashMap<>();

    public static ltd.xx.mall.util.R success() {
        ltd.xx.mall.util.R r = new ltd.xx.mall.util.R();
        r.code = 200;
        r.msg = "请求成功";
        return r;
    }

    public static ltd.xx.mall.util.R success(String msg) {
        ltd.xx.mall.util.R r = new ltd.xx.mall.util.R();
        r.code = 200;
        r.msg = msg;
        return r;
    }

    public static ltd.xx.mall.util.R error() {
        ltd.xx.mall.util.R r = new ltd.xx.mall.util.R();
        r.code = 500;
        r.msg = "请求失败";
        return r;
    }

    public static ltd.xx.mall.util.R error(String msg) {
        ltd.xx.mall.util.R r = new ltd.xx.mall.util.R();
        r.code = 500;
        r.msg = msg;
        return r;
    }

    public ltd.xx.mall.util.R add(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("code", code)
                .append("msg", msg)
                .append("map", map)
                .toString();
    }
}
