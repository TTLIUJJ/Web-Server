package xmu.ackerman.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class FileType {
    private ConcurrentHashMap<String, String> map;

    volatile private static FileType fileType = null;

    private FileType(){
        //从资源文件获取新增的类型
        try {
            String fileName = "file_type.properties";
            InputStream inputStream = new FileInputStream(fileName);
            Properties properties = new Properties();
            properties.load(inputStream);

            map = new ConcurrentHashMap<String, String>();
            for(Object s : properties.keySet()){
                String suffix = (String) s;
                String fileType = properties.getProperty(suffix);

                map.put(suffix, fileType);
            }
        }catch (Exception e){
            //TODO
        }

    }

    public static FileType getFileType(){
        if(fileType == null){
            synchronized(FileType.class){
                if(fileType == null){
                    fileType = new FileType();
                }
            }
        }

        return fileType;
    }

    public void addFileType(String suffix, String type){
        if(fileType == null){
            fileType = FileType.getFileType();
        }

        map.put(suffix, type);
    }

    public boolean containsType(String suffix){
        if(fileType == null){
            fileType = FileType.getFileType();
        }
        return map.containsKey(suffix);
    }

    private String getContentType(String suffix, String defaultType){
        if(fileType == null){
            fileType = FileType.getFileType();
        }
        if(map.containsKey(suffix)){
            return map.get(suffix);
        }
        return defaultType;
    }

    /**
    * @Description: 根据文件的后缀, 返回对应的contentTYpe
    * @Date: 上午11:24 18-3-16
    */
    public String getContentTypeOrDefault(String filename, String defaultType){
        int lastDot = filename.length()-1;
        while (lastDot >= 0){
            char ch = filename.charAt(lastDot);
            if(ch == '.'){
                break;
            }
            --lastDot;
        }
        if(lastDot < 0){
            return defaultType;
        }
        String suffix = filename.substring(lastDot, filename.length());

        return getContentType(suffix, defaultType);
    }

    public void test(){
        for(Map.Entry<String, String> entry : map.entrySet()){
            System.out.println(entry.getKey() + ": " + entry.getValue());
            if(entry.getKey().equals("NULL")){
                System.out.print("................");
            }
        }
    }

    public static void main(String []args){
        FileType x = FileType.getFileType();
        x.test();

        x.addFileType(".jpg", "image/jpg");
        x.test();
    }

}
