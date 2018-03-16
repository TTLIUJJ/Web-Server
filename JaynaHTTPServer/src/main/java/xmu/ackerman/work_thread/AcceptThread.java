package xmu.ackerman.work_thread;


import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptThread implements Runnable{

    private Selector selector;
    private SelectionKey key;

    public AcceptThread(Selector selector, SelectionKey key){
        this.selector = selector;
        this.key = key;
    }

    public void run(){
        ServerSocketChannel server = null;
        try{
            server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            if(client == null){
                return;
            }
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        }catch (Exception e){
            //TODO
            e.printStackTrace();
//            System.err.println("xxxxx" + e);
        }
    }

}
