package com.athome.gmall.manage.util;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;


public class PmsUploadUtil {
    public static String uploadImage(MultipartFile multipartFile) {
        String imgUrl = urlconstant.imageUrl;
        ;
        try {
            byte[] bytes = multipartFile.getBytes();//获得上传的二进制对象
            //获得文件后缀名
            String originalFilename = multipartFile.getOriginalFilename();//获取文件全名
            int l = originalFilename.lastIndexOf(".");//找到最后一个"."
            String extName = originalFilename.substring(l + 1);
            String file = PmsUploadUtil.class.getResource("/tracker.conf").getFile();
            ClientGlobal.init(file);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, null);
            String[] uploadinfo = storageClient.upload_file(bytes, extName, null);

            for (String uploadinfos : uploadinfo) {
                imgUrl += "/" + uploadinfos;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgUrl;
    }
}
