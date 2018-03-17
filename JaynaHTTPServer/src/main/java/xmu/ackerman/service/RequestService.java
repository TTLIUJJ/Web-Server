package xmu.ackerman.service;

import xmu.ackerman.utils.ParseRequestUtil;
import xmu.ackerman.utils.RequestParseState;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午4:12 18-3-15
 */
public class RequestService {
    private static int MAX_BUF = 1024;

    private static Logger logger = Logger.getLogger("Request.Service");

    /**
    * @Description: 从通道中获取数据, 考虑到不能一次全部获取的情况, 测试未通过
    * @Date: 上午10:52 18-3-16
    */
    public static boolean recvFrom(RequestMessage requestMessage, SelectionKey key){
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(MAX_BUF);
        RequestParseState state;
        try{
            while (true){
                // parse_more 1.半包数据  2.缓冲区太小
                //需要设置 等待超时时间
                //可能 读取在等待更多的数据 进行parse_more
                int cnt = client.read(buffer);
//                System.out.println("cnt == " + cnt);
                if(cnt == 0){
                    System.out.println("cnt == 0");
                    return false;
                }

                if(cnt < 0){
//                    System.out.println("cnt == " + cnt);
                    return requestError(requestMessage, key);
                }
                byte [] bytes = buffer.array();

                state = ParseRequestUtil.parseHttpRequestLine(requestMessage, bytes);
                if(state == RequestParseState.PARSE_MORE){
                    continue;
                }
                else if(state != RequestParseState.HEADER_START){
                    return requestError(requestMessage, key);
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
                    return requestError(requestMessage, key);
                }
            }

        }catch (Exception e){
            System.out.println(e);
            //TODO
        }

        return true;
    }
    /**
    * @Description: 处理错误请求 关闭通道
    * @Date: 上午10:52 18-3-16
    */
    public static boolean requestError(RequestMessage requestMessage, SelectionKey key){

        key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));

        return false;
    }
}
