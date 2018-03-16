package xmu.ackerman.controller;

import xmu.ackerman.service.RequestMessage;
import xmu.ackerman.service.RequestService;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午3:33 18-3-15
 */
public class JaynaHttpController {

    private final static String CONFIG_FILE = "config.properties";
    private int port;
    private int threadNum;
    private Selector selector;
    private ExecutorService pool;
    private LinkedBlockingQueue<SelectionKey> interestQueue;

    public JaynaHttpController(){
        InputStream inputStream;
        try{
            inputStream = new FileInputStream(CONFIG_FILE);
            Properties properties = new Properties();
            properties.load(inputStream);
            if(properties.contains("port")){
                port = Integer.parseInt(properties.getProperty("port"));
            }
            else{
                port = 8080;
            }

            if(properties.contains("threadNum")){
                this.threadNum = Integer.parseInt(properties.getProperty("threadNum"));
            }
            else{
                this.threadNum = 5;
            }
        }catch (Exception e){
            //TODO
        }
    }

    //初始化服务器通道, 并注册selector
    private void initServerSocket(){
        ServerSocketChannel serverSocketChannel;
        Selector selector;
        try{
            serverSocketChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverSocketChannel.socket();
            InetSocketAddress address = new InetSocketAddress(this.port);
            serverSocket.bind(address);

            //将IO请求设置为非阻塞
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            this.selector = selector;
        }catch (Exception e){
            //TOD
        }
    }


    private void initAttribute(){
        this.pool = Executors.newFixedThreadPool(this.threadNum);
        this.interestQueue = new LinkedBlockingQueue<SelectionKey>();
    }


    public void start(){
        try {
            initServerSocket();
            initAttribute();

//            InterestThread r = new InterestThread(selector, interestQueue);
//            Thread t = new Thread(r);
//            t.start();

            //等待上面的线程完全启动
//            TimeUnit.SECONDS.sleep(2);

            while (true) {
                int readyChannels = 0;
                try {
                    readyChannels = selector.select();
                } catch (Exception e) {
                    //TODO
                }

                if (readyChannels == 0) {
                    continue;
                }
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();
//

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
//                    interestQueue.add(key);
//                    SelectionKey key = interestQueue.take();
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        if(client != null){
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                        }
                    } else if (key.isReadable()) {

                        SocketChannel client = (SocketChannel) key.channel();
                        RequestMessage requestMessage = new RequestMessage();
                        //接受的数据包有误, 不接受此次请求
                        //并不是404错误, 而是发送不能识别或者错误的数据包
                        if(!RequestService.recvFrom(requestMessage, client)){
                            break;
                        }


                        for(int i = 0; i < requestMessage.getMessage().size(); ++i){
                            System.out.print((char)((byte) requestMessage.getMessage().get(i)));
                        }

                        System.out.println("method: " + requestMessage.getMethod());
                        System.out.println("uri: " + requestMessage.getUri());

                        Map<String, String> headers = requestMessage.getHeaders();
                        for(Map.Entry<String, String> entry : headers.entrySet()){
                            System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
                        }

                        System.out.println("1111");

                        HTTPThread httpThread = new HTTPThread(requestMessage, key);
                        Thread thread = new Thread(httpThread);
                        thread.start();

                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        client.close();
                    }

                }
            }
        }catch (Exception e){
            //TODO
        }
    }

    public static void main(String []args){
        JaynaHttpController controller = new JaynaHttpController();
        controller.start();
    }
}
