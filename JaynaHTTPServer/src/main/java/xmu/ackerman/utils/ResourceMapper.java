package xmu.ackerman.utils;

import java.io.File;
import java.util.*;

public class ResourceMapper {
    private String prefixPath;

    private HashMap<String, String> fileMap;

    private HashMap<String, String> viewResourceMap;

    private static volatile ResourceMapper resourceMapper = null;


    /**
    * @Description: 根据请求的uri, 映射对应的资源文件
    * @Date: 下午5:40 18-3-16
    */
    private ResourceMapper(){
        try{
            // suffix是不包括resources目录的文件路径名
            // < suffix, absolutePath>
            // < /sports/xxx.html, /home/ackerman/JayHTTPServer/main/java/resources/xxx.html>
            fileMap = new HashMap<String, String>();
            String projectName = System.getProperty("user.dir");
            File root = new File(projectName+"/src/main/resources/");

            //静态文件夹
            String staticDirectory = "static";
            this.prefixPath = root.toString();

            Queue<File> queue = new LinkedList<File>();
            queue.offer(root);
            while(!queue.isEmpty()){
                int size = queue.size();
                while(size-- != 0){
                    File file = queue.poll();

                    //静态文件不加入map
                    if(staticDirectory.equals(file.getName())){
                        break;
                    }
                    File []files = file.listFiles();
                    if(files == null || files.length == 0){
                        break;
                    }
                    for(int i = 0; i < files.length; ++ i){
                        if(files[i].isDirectory()){
                            queue.add(files[i]);
                        }
                        else{
                            addFileMap(files[i].getAbsolutePath());
                        }
                    }
                }

            }


            initViewResource();
        }catch (Exception e){
            //TODO
        }
    }

    public static ResourceMapper getResourceMapper(){
        if(resourceMapper == null){
            synchronized (ResourceMapper.class){
                if(resourceMapper == null){
                    resourceMapper = new ResourceMapper();
                }
            }
        }
        return resourceMapper;
    }


    // 删头去尾
    private void addFileMap(String path){
        String name = path.substring(prefixPath.length(), path.lastIndexOf('.'));

        fileMap.put(name, path);
    }

    public boolean containsFile(String filename){
        return fileMap.containsKey(filename);
    }

    public String getFilePath(String filename){
        return fileMap.get(filename);
    }


    /**
     *  <view, resourcePath>
     *  比如：
     *       requestMappering 返回  "index"
     *          在resources文件夹下查询是否有 index.html这个html文件
     *          - 如果存在, 一种可能的返回为 "/Users/xxxx/ackerman/Jayna/JaynaHTTPServer/src/main/resources/index.html"
     *          - 如果没有, 返回null
     */

    private void initViewResource(){
        try{
            viewResourceMap = new HashMap<String, String>();
            for(Map.Entry<String, String> entry: fileMap.entrySet()){
                String prevKey = entry.getKey();
                int lastDot = prevKey.lastIndexOf('/');
                String viewName = prevKey.substring(lastDot+1);
                viewResourceMap.put(viewName, entry.getValue());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getViewResource(String viewName){
        try {
            return viewResourceMap.get(viewName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String []args){
        ResourceMapper resourceMapper = ResourceMapper.getResourceMapper();
        for(Map.Entry<String, String> entry : resourceMapper.fileMap.entrySet()){
            System.out.println(entry.getKey()+", " +entry.getValue());
        }

        System.out.println("index: " + resourceMapper.getViewResource( "index"));
    }

}
