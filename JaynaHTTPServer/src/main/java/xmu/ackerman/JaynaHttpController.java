package xmu.ackerman;

import xmu.ackerman.context.HttpRequest;
import xmu.ackerman.thread.ReadThread;
import xmu.ackerman.thread.TimeMonitorThread;
import xmu.ackerman.thread.WriteThread;
import xmu.ackerman.thread.RejectedStrategy;
import xmu.ackerman.service.RequestMessage;
import xmu.ackerman.service.RequestService;
import xmu.ackerman.utils.RequestParseState;

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
//    private ArrayBlockingQueue<SelectionKey> queue;
//    private LinkedBlockingQueue<SelectionKey> queue;
//    private ConcurrentHashMap<SelectionKey, Long> map;

    private LinkedList<SelectionKey> queue;
    private HashMap<SelectionKey, Long> map;
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
//        this.pool = Executors.newFixedThreadPool(this.threadNum);
        try {
            threadPoolExecutor = new ThreadPoolExecutor(
                    2,
                    10,
                    60,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(100),
                    new RejectedStrategy()
            );
//            queue = new ArrayBlockingQueue<SelectionKey>(10000);
//            queue = new LinkedBlockingQueue<SelectionKey>();
//            map = new ConcurrentHashMap<SelectionKey, Long>();
            queue = new LinkedList<SelectionKey>();
            map = new HashMap<SelectionKey, Long>();
            Runnable r = new TimeMonitorThread(queue, map);
            Thread t = new Thread(r);
            t.start();
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
//                    System.out.println("in loop");

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

//                    SelectionKey key = interestQueue.take();
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        if(client != null){
                            client.configureBlocking(false);

                            RequestMessage requestMessage = new RequestMessage();
                            HttpRequest request = new HttpRequest(requestMessage);

                            SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);
//                            System.out.println("acceptKey:" + clientKey.toString());
                            clientKey.attach(request);
                        }
                    } else if (key.isReadable()) {
//                        System.out.println("read");
//                        SocketChannel client = (SocketChannel) key.channel();
//                        HttpRequest request = (HttpRequest) key.attachment();
////                        //接受的数据包有误, 不接受此次请求
////                        //并不是404错误, 而是发送不能识别或者错误的数据包
//                        RequestParseState state = RequestService.recvFrom(request, key);
//                        switch (state){
//                            case PARSE_ERROR:
//                                key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
//                                key.channel().close();
//                                continue;
//
//                            case PARSE_MORE:
//                                System.out.println("parse more");
//                                client.register(selector, SelectionKey.OP_READ, request);
//                                continue;
//
//                            case PARSE_OK:
//                                break;
//                        }
//
//                        WriteThread writeThread = new WriteThread(request, key, selector);
//                        threadPoolExecutor.execute();
//                        TimeUnit.SECONDS.sleep(1);

                        //防止多个线程 处理一个READ_KEY
                        key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
//                        System.out.println("readKey: " + key.toString());
                        ReadThread readThread = new ReadThread(selector, key);
                        Thread thread = new Thread(readThread);
                        threadPoolExecutor.execute(thread);

                    } else if (key.isWritable()) {
//                        System.out.println("writable");
                        key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));

                        SocketChannel client = (SocketChannel) key.channel();
                        HttpRequest request = (HttpRequest) key.attachment();
                        WriteThread writeThread = new WriteThread(request, key, queue, map);
                        Thread thread = new Thread(writeThread);
                        threadPoolExecutor.execute(thread);
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
            JaynaHttpController controller = new JaynaHttpController();
            controller.start();
        }catch (Exception e){

        }finally {

        }
    }
}
