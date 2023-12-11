package com.dormitory.backend.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ReadFile {
    private ReadFile readFile = new ReadFile();
    private ReadFile(){}
    public static String readSQLFile(File file) {
        String result = "";
        StringBuilder sb = new StringBuilder();
        FileInputStream fis = null;
        InputStreamReader read = null;
        BufferedReader reader = null;
        try {
            if (file != null && file.isFile() && file.exists()) {
                fis = new FileInputStream(file);
                read = new InputStreamReader(fis, StandardCharsets.UTF_8);
                reader = new BufferedReader(read);
                String line;
                boolean ck = false;
                while ((line = reader.readLine()) != null) {
                    if(ck){//第一行忽略
                        if (line != null && !line.toString().contains("--")) {//没注释的
                            sb.append(line).append(" ");
                        }else if (line != null && line.toString().contains("--")) {//有注释的
                            if(line.split("--").length>0 && line.split("--")[0].trim().length()>1){
                                sb.append(line.split("--")[0] + " ");
                            }
                        }
                    }
                    ck = true;
                }
                result = sb.toString();
                sb.setLength(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
