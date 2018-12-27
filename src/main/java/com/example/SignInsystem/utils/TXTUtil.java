package com.example.SignInsystem.utils;

import java.io.*;

/**
 * @ClassName TXTUtile
 * @Description TODO
 * @Author q
 * @Date 18-9-3 下午3:21
 */
public class TXTUtil {

//    private static final String PATH = "C:/data.txt";
    private static final String PATH = "/home/ahri/Desktop/data.txt";

    /**
     * 读取txt文件内容
     * @return
     */
    public static String readContent(){
        File file = new File(PATH);
        /* 存储从txt中读取出的内容 */
        StringBuffer datas = new StringBuffer();

        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file),"utf-8");

            BufferedReader bufferedReader = new BufferedReader(reader);
            /* 存放读取一行的内容 */
            String line = "";
            while (line != null){
                /* 读取一行 */
                line = bufferedReader.readLine();
                if (line == null || line.equals("")){
                    break;
                }
                datas.append(line);
            }
            System.out.println("读取内容："+datas);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datas.toString();
    }

    /**
     * 去掉mac地址中的 “-”
     * @param localMac
     * @return
     */
    public static String convertMac(String localMac){
        String localMacString = localMac.replace("-","");
        return localMacString;
    }


}
