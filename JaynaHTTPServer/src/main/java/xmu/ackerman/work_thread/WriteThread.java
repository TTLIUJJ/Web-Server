package xmu.ackerman.work_thread;

import xmu.ackerman.HttpResponse;
import xmu.ackerman.zparse_http.ParseResponseUtil;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WriteThread implements Runnable {

    private SelectionKey key;

    public WriteThread(SelectionKey key){
        this.key = key;
    }

    public void run(){
        SocketChannel client = null;
        try{
            System.out.println("write");
            client = (SocketChannel) key.channel();

            HttpResponse response = (HttpResponse) key.attachment();
            ParseResponseUtil.doResponse(response);
//            ByteBuffer buffer = ByteBuffer.wrap(Util.response().getBytes());
//            client.write(buffer);


//            client.socket().close();

        }catch (Exception e){
            //TODO
            System.out.print(e);
            if(client != null){
                try{
                    client.close();
                }catch (Exception ee){
                    //TODO
                }
            }
        }
    }
}
