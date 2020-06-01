package ltd.xx.mall.util;

import ltd.xx.mall.controller.common.BusinessException;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.*;

/**
 * @author 13
 */
public class MallUtils {

    public static URI getHost(URI uri) {
        URI effectiveURI = null;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (Throwable var4) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    public static Map<String, String> aliPayGetParams(Map<String,String[]> requestParams) {
        Map<String,String> params = new HashMap<String,String>();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            try {
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            }catch (Exception e){
                throw new BusinessException("fail");
            }
            params.put(name, valueStr);
        }
        return params;
    }
//    Map<String,String> params = new HashMap<String,String>();
//    Map<String,String[]> requestParams = request.getParameterMap();


    public static String cleanString(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        value = value.toLowerCase();
        value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        value = value.replaceAll("'", "& #39;");
        value = value.replaceAll("onload", "0nl0ad");
        value = value.replaceAll("xml", "xm1");
        value = value.replaceAll("window", "wind0w");
        value = value.replaceAll("click", "cl1ck");
        value = value.replaceAll("var", "v0r");
        value = value.replaceAll("let", "1et");
        value = value.replaceAll("function", "functi0n");
        value = value.replaceAll("return", "retu1n");
        value = value.replaceAll("$", "");
        value = value.replaceAll("document", "d0cument");
        value = value.replaceAll("const", "c0nst");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "scr1pt");
        value = value.replaceAll("insert", "1nsert");
        value = value.replaceAll("drop", "dr0p");
        value = value.replaceAll("create", "cre0ate");
        value = value.replaceAll("update", "upd0ate");
        value = value.replaceAll("alter", "a1ter");
        value = value.replaceAll("from", "fr0m");
        value = value.replaceAll("where", "wh1re");
        value = value.replaceAll("database", "data1base");
        value = value.replaceAll("table", "tab1e");
        value = value.replaceAll("tb", "tb0");
        return value;
    }
}
