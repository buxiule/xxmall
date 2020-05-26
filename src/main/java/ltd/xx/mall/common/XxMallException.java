package ltd.xx.mall.common;

public class XxMallException extends RuntimeException {

    public XxMallException() {
    }

    public XxMallException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new XxMallException(message);
    }

}
