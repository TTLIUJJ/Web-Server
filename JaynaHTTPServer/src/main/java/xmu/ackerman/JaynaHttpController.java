package xmu.ackerman;

import xmu.ackerman.context.HttpRequest;
import xmu.ackerman.service.TimeMonitorService;
import xmu.ackerman.thread.ReadThread;
import xmu.ackerman.thread.WriteThread;
import xmu.ackerman.thread.RejectedStrategy;
import xmu.ackerman.service.RequestMessage;

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
    private ThreadPoolExecutor threadPoolExecutor;
    private TimeMonitorService timeMonitorService;

    //用于测试 使用keepAlive性能下降多少
    private boolean keepAlive;

    public JaynaHttpController(boolean keepAlive){
        this.keepAlive = keepAlive;
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
//        this.pool = Executors.newFixedThreadPool(this.threadNum);
        try {
            threadPoolExecutor = new ThreadPoolExecutor(
                    2,
                    10,
                    60,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(200),
                    new RejectedStrategy()
            );
            timeMonitorService = new TimeMonitorService();

        }catch (Exception e){
            System.out.println("InitAttribute: " + e);
        }
    }


    public void start(){
        try {
            initServerSocket();
            initAttribute();

            System.out.println("start httpserver");
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

                if(keepAlive) {
                    timeMonitorService.checkExpiredKey();
                }
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        if (key.isValid() && key.isAcceptable()) {
                            ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            SocketChannel client = server.accept();
                            if (client != null) {
                                client.configureBlocking(false);
                                RequestMessage requestMessage = new RequestMessage();

                                HttpRequest request = new HttpRequest(requestMessage);

                                SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);
                                clientKey.attach(request);
                                if(keepAlive) {
                                    timeMonitorService.addMonitorKey(clientKey);
                                }
                            }
                        } else if (key.isValid() && key.isReadable()) {
//                            System.out.println("read");
                            //防止多个线程 处理一个READ_KEY
                            key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
                            ReadThread readThread = new ReadThread(selector, key);
                            Thread thread = new Thread(readThread);
                            threadPoolExecutor.execute(thread);

                        } else if (key.isValid() && key.isWritable()) {
                            key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
                            HttpRequest request = (HttpRequest) key.attachment();
                            WriteThread writeThread = new WriteThread(request, key, keepAlive);
                            Thread thread = new Thread(writeThread);
                            threadPoolExecutor.execute(thread);
                        }
                    }catch (Exception e){
                        System.out.println("iterator.next in loop: " + e);
                    }

                }

            }
        }catch (Exception e){
            //TODO
            System.out.println("aaa"+e);
        }
    }

    public static void main(String []args){
        try {
            boolean keepAlive = false;
            if(args.length > 0){
                keepAlive = Boolean.parseBoolean(args[0]);
            }
            JaynaHttpController controller = new JaynaHttpController(keepAlive);
            controller.start();
        }catch (Exception e){
            System.out.println("main: " + e);
        }
    }
}
