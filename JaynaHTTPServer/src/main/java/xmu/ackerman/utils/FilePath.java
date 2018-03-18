package xmu.ackerman.utils;

import java.io.File;
import java.util.*;

public class FilePath {
    private String prefixPath;

    private HashMap<String, String> fileMap;

    private static volatile FilePath filePath = null;


    /**
    * @Description: 根据请求的uri, 映射对应的资源文件
    * @Date: 下午5:40 18-3-16
    */
    private FilePath(){
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
        }catch (Exception e){
            //TODO
        }
    }

    public static FilePath getResourcesFile(){
        if(filePath == null){
            synchronized (FilePath.class){
                if(filePath == null){
                    filePath = new FilePath();
                }
            }
        }
        return filePath;
    }

    private void addFileMap(String path){
        String name = path.substring(prefixPath.length(), path.length());
        fileMap.put(name, path);
    }

    public boolean containsFile(String filename){
        return fileMap.containsKey(filename);
    }

    public String getFilePath(String filename){
        return fileMap.get(filename);
    }



    public static void main(String []args){
        FilePath filePath = FilePath.getResourcesFile();
        for(Map.Entry<String, String> entry : filePath.fileMap.entrySet()){
            System.out.println(entry.getKey()+", " +entry.getValue());
        }
    }

}
