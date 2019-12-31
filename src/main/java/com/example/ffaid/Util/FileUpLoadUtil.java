package com.example.ffaid.Util;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author DIX
 * @version 1.0
 * @description
 * @date 2019/12/9 17:46
 */
public class FileUpLoadUtil {
    public static String upload(MultipartFile file, String path) {
        if (!file.isEmpty()) {
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(path)));
                System.out.println(file.getOriginalFilename());
                out.write(file.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed" + e.getMessage();
            }
            return "Success";
        } else {
            return "Empty";
        }
    }
}
