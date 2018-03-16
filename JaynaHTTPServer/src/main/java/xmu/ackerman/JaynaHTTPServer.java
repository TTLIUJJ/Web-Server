package xmu.ackerman;


import xmu.ackerman.work_thread.ReadThread;
import xmu.ackerman.work_thread.WriteThread;
import xmu.practice.chargen.Util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JaynaHTTPServer {

    private final static String CONFIG_FILE = "config.properties";
    private int port;
    private int threadNum;
    private Selector selector;
    private ExecutorService pool;

    public JaynaHTTPServer(String configFile){
        InputStream inputStream;
        try{
            if(configFile == null) {
                inputStream = new FileInputStream(CONFIG_FILE);
            }
            else{
                inputStream = new FileInputStream(configFile);
            }
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


    private void initThreadPool(){
        this.pool = Executors.newFixedThreadPool(this.threadNum);
    }

    //初始化计时器
    private void initTimer(){

    }

    public void start(){
        initServerSocket();
        initThreadPool();
        initTimer();

        try {
            while (true) {
                try {
                    selector.select();
                } catch (Exception e) {
                    //TODO
                    break;
                }

                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();


                //使用多个if, 而不使用多个 else if
                //因为一个通道目前可以是可读, 可写同时存在
                //如果 if(readable) {} else if(writable)
                //那么一定是先处理readable
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    //客户端通道一旦连接上了服务器的ServerSocketChannel
                    //不会再次触发OP_ACCEPTED
                    if (key.isAcceptable()) {
                        //Acceptable中 key连接的通道是与服务端通信的
                        //所以不能使用key.interestOps(OP_WRITE)

                        //而在readable中 key连接的是与客户端通信的
                        //所以能够使用key.interestOps(OP_READ)
                        System.out.println("acceptThread");
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);

//                        Runnable r = new AcceptThread(selector, key);
//                        Thread t = new Thread(r);
//                        pool.execute(t);

                    }
                    if (key.isReadable()) {
                        //如果通道目前是OP_READ
                        //然后使用了client.register(selector, SelectionKey.OP_WRITE)
                        //那么之前的事件会被注销, 并且只剩下OP_WRITE
//                        SocketChannel client = (SocketChannel) key.channel();

//                        HttpRequest request = new HttpRequest(key);
//                        client.read(request.getBuff());
//                        request.getBuff().flip();
//                        String msg = Charset.forName("UTF-8").newDecoder().decode(request.getBuff()).toString();
//                        System.out.println(msg);
//                        request.getBuff().flip();

//                        测试parseUtil
//                        HttpRequest request = new HttpRequest(key);
//                        ParseRequestUtil.doRequest(request);
//                        System.out.println("method: " + request.getMethod());
//
//                        LinkedList<HttpHeaderKV> list = request.getHeaderList();
//                        System.out.println("list size: " + list.size());
//                        for(int i = 0; i < list.size(); ++i){
//                            HttpHeaderKV kv = list.get(i);
//                            System.out.println("key: " + kv.getHeaderKey() +", value: " + kv.getHeaderValue());
//                        }
//                        key.interestOps(SelectionKey.OP_WRITE);

                        Runnable r = new ReadThread(selector, key);
                        Thread  t = new Thread(r);
                        pool.execute(t);
                    }
                    if (key.isWritable()) {
//                        System.out.println("writeThread");
//                        System.out.println("write");
//                        SocketChannel client = (SocketChannel) key.channel();
//                        ByteBuffer buffer = ByteBuffer.wrap(Util.response().getBytes());
//                        client.write(buffer);
//                        key.cancel();
//                        client.socket().close();

                        Runnable r = new WriteThread(key);
                        Thread t = new Thread(r);
                        pool.execute(t);
                    }

                }
            }
        }catch (Exception e){
            //TODO
            System.out.print(".........." + e);
        } finally{
            pool.shutdown();
        }
    }

    public static void main(String []args){
        JaynaHTTPServer jaynaHTTPServer;

        try {
            jaynaHTTPServer = new JaynaHTTPServer(null);
            jaynaHTTPServer.start();
        }catch ( Exception e){
            //TODO
        }

    }
}
