package xmu.ackerman.http.handler;

import xmu.ackerman.http.context.Context;

/**
 * @Author: Ackerman
 * @Description: 处理事件接口
 * @Date: Created in 下午3:24 18-3-15
 */
public interface Handler {
    /**
    * @Description: 初始化
    * @Date: 下午3:25 18-3-15
    */
    public void init(Context context);


    public void service(Context context);

    public void doGet(Context context);

    public void doPost(Context context);

    public void destroy(Context context);

}
