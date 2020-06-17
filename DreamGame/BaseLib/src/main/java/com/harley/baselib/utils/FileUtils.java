package com.harley.baselib.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class FileUtils {

    private static final String TAG = "FileUtils";

    public static File createNewFile(String path) {
        File file = new File(path);
        try {
            if (file.getParentFile() != null) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
            }

            if (!file.exists()){
                file.createNewFile();
            }
        } catch (IOException e) {
            LogUtils.e(TAG, e.getMessage());
        }
        return file;
    }

    public static void deleteFiles(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        // 删除当前目录
        if (!file.isFile()) {
            File[] subFiles = file.listFiles();
            assert subFiles != null;
            for (File subfile : subFiles) {
                deleteFiles(subfile.getAbsolutePath());// 删除当前目录下的子目录
            }
        }
        file.delete();// 删除文件
    }

    public static String LoadFileToString(File file) {
        if(!file.exists() || !file.isFile()) {
            LogUtils.i(TAG, "file exists:" + file.exists() + ", isFile:" + file.isFile() + ", filePath:" + file.getPath());
            return "";
        }

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            StringBuilder stringBuilder = new StringBuilder("");
            int len = 0;
            while ((len = inputStream.read(buffer)) > 0){
                stringBuilder.append(new String(buffer, 0, len));
            }
            return stringBuilder.toString();
        } catch (IOException  e) {
            LogUtils.e(TAG, e.getMessage());
        }finally {
            try{
                if (inputStream != null){
                    inputStream.close();
                }
            }catch (IOException e){
                LogUtils.e(TAG, e.getMessage());
            }
        }
        return "";
    }

    public static String LoadFileToString(String filePath) {
        File file = new File(filePath);
        return LoadFileToString(file);
    }

    public static List<String> LoadFileToStringList(File file){
        if(!file.exists() || !file.isFile()){
            LogUtils.i(TAG, "file exists:" + file.exists() + ", isFile:" + file.isFile() + ", filePath:" + file.getPath());
            return null;
        }

        List<String> stringList = new ArrayList<String>();
        FileInputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                stringList.add(line);
            }
            return stringList;
        } catch (IOException  e) {
            LogUtils.e(TAG, e.getMessage());
        }finally {
            try{
                if (bufferedReader != null){
                    bufferedReader.close();
                }
                if (inputStreamReader != null){
                    inputStreamReader.close();
                }
                if (inputStream != null){
                    inputStream.close();
                }
            }catch (IOException e){
                LogUtils.e(TAG, e.getMessage());
            }
        }
        return null;
    }

    public static List<String> LoadFileToStringList(String filePath){
        File file = new File(filePath);
        return LoadFileToStringList(file);
    }

    public static void SaveStringToFile(File file, String content, boolean append){
        if (!file.exists() || !file.isFile()){
            LogUtils.i(TAG, "file exists:" + file.exists() + ", isFile:" + file.isFile() + ", filePath:" + file.getPath());
            return;
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);
            fileWriter.write(content);
            fileWriter.flush();
        }catch (IOException e){
            LogUtils.e(e.getMessage());
        }finally {
            try{
                if (fileWriter != null){
                    fileWriter.close();
                }
            }catch (IOException e){
                LogUtils.e(TAG, e.getMessage());
            }
        }
    }

    public static void SaveStringToFile(String filePath, String content, boolean append){
        File file = createNewFile(filePath);
        SaveStringToFile(file, content, append);
    }

    public static void SaveStringToFile(File file, List<String> contentList, boolean append){
        if (!file.exists() || !file.isFile()){
            LogUtils.i(TAG, "file exists:" + file.exists() + ", isFile:" + file.isFile() + ", filePath:" + file.getPath());
            return;
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);
            for (String line : contentList){
                fileWriter.write(line);
                fileWriter.write("\r\n");
            }
            fileWriter.flush();
        }catch (IOException e){
            LogUtils.e(e.getMessage());
        }finally {
            try{
                if (fileWriter != null){
                    fileWriter.close();
                }
            }catch (IOException e){
                LogUtils.e(TAG, e.getMessage());
            }
        }
    }

    public static void SaveStringToFile(String filePath, List<String> contentList, boolean append) {
        File file = createNewFile(filePath);
        SaveStringToFile(file, contentList, append);
    }

}
