package xmu.ackerman.service;

import xmu.ackerman.utils.ParseRequestUtil;
import xmu.ackerman.utils.RequestParseState;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午4:12 18-3-15
 */
public class RequestService {
    private static int MAX_BUF = 1024;

    /**
    * @Description: 从通道中获取数据, 考虑到不能一次全部获取的情况, 测试未通过
    * @Date: 上午10:52 18-3-16
    */
    public static boolean recvFrom(RequestMessage requestMessage, SocketChannel client){
        ByteBuffer buffer = ByteBuffer.allocate(MAX_BUF);
        RequestParseState state;
        try{
            while (true){
                // parse_more 1.半包数据  2.缓冲区太小
                //需要设置 等待超时时间
                //可能 读取在等待更多的数据 进行parse_more
                int cnt = client.read(buffer);
                if(cnt < 0){
                    break;
                }
                byte [] bytes = buffer.array();

                state = ParseRequestUtil.parseHttpRequestLine(requestMessage, bytes);
                if(state == RequestParseState.PARSE_MORE){
                    continue;
                }
                else if(state != RequestParseState.HEADER_START){
                    return requestError(requestMessage, client);
                }

                // 目前只支持处理请求 "GET"
                // 还未支持 "POST"
                state = ParseRequestUtil.parseHttpRequestHeader(requestMessage, bytes);
                if(state == RequestParseState.PARSE_MORE){
                    continue;
                }
                else if(state == RequestParseState.PARSE_OK){
                    break;
                }
                else{
                    return requestError(requestMessage, client);
                }
            }

        }catch (Exception e){
            //TODO
        }

        return true;
    }
    /**
    * @Description: 处理错误请求 关闭通道
    * @Date: 上午10:52 18-3-16
    */
    public static boolean requestError(RequestMessage requestMessage, SocketChannel client){

        return false;
    }


    public static void sendTo(){}
}
