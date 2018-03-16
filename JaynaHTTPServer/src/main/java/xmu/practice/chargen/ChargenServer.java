package xmu.practice.chargen;

import xmu.ackerman.ParseUtil;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class ChargenServer {

    public static void main(String []args){
        int port = 8080;
        byte []rotation = new byte[95*2];
        for(byte i = ' '; i <= '~'; ++i){
            rotation[i - ' '] = i;
            rotation[i + 95 - ' '] = i;
        }

        ServerSocketChannel serverSocketChannel;
        Selector selector;

        try{
            serverSocketChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverSocketChannel.socket();
            InetSocketAddress address = new InetSocketAddress(port);
            serverSocket.bind(address);

            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (Exception e){
            System.out.println("init arguments" + e);
            return ;
        }

        while(true){
            try{
                selector.select();
            }catch (Exception e){
                System.out.println("select()" + e);
                break;
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();

                try{
                    if(key.isAcceptable()){
                        ServerSocketChannel server = (ServerSocketChannel)key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from " + client);
                        client.configureBlocking(false);
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);

                        System.out.println("leave acceptable");
                    }
                    else if(key.isReadable()){
                        System.out.println("read");

                        SocketChannel client = (SocketChannel) key.channel();


                        ByteBuffer readBuffer = ByteBuffer.allocate(128);
                        client.read(readBuffer);
                        readBuffer.flip();
                        String msg = Charset.forName("UTF-8").newDecoder().decode(readBuffer).toString();
                        System.out.println(msg);


                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE);
//                        clientKey.attach(buffer);
                        System.out.println("read end");
                    }

                    else if(key.isWritable()){
                        System.out.println("write");
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.wrap(Util.response().getBytes());
                        client.write(buffer);
                        key.cancel();
                        client.socket().close();
                    }
                }catch (Exception e){
                    key.cancel();
                    try{
                        key.channel().close();
                    }catch (Exception ee){
                        System.out.println("close channel " + e);
                    }
                    System.out.println("process readyKey " + e);
                }
            }
        }
    }
}
