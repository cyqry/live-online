package com.ytyo.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@Slf4j
public class ImageService {


    public static final String ProjectRootPath = new File("").getAbsolutePath() + "/live_platform/live_store";
    public static final String UserImageRoot = "user/images";
    public static final String SystemImageRoot = "system/images";

    public List<String> uploadBase64Images(List<String> base64s, String directoryPath) throws IOException {
        if (!StringUtils.hasText(directoryPath) || base64s == null) {
            throw new IllegalArgumentException("Base64 image, image path and image name must not be empty");
        }
        directoryPath = directoryPath.startsWith("/") ? directoryPath : ("/" + directoryPath);

        HashMap<String, byte[]> map = new HashMap<>();

        for (String base64 : base64s) {
            map.put(base64, Base64.getDecoder().decode(base64.getBytes()));
        }

        File targetDirectory = new File(ProjectRootPath, UserImageRoot + directoryPath);
        if (!targetDirectory.exists()) {
            boolean mkdirs = targetDirectory.mkdirs();
            log.info("文件夹{}创建", targetDirectory.getAbsolutePath());
        }

        ArrayList<String> list = new ArrayList<>();
        for (byte[] bytes : map.values()) {
            String imageName = UUID.randomUUID().toString();
            File targetFile = new File(targetDirectory, imageName);
            if (!targetFile.exists()) {
                boolean created = targetFile.createNewFile();
            }
            FileCopyUtils.copy(bytes, targetFile);
            list.add("static/user/image?directoryPath=" + directoryPath + "&imageName=" + imageName);
        }

        return list;
    }

    public String uploadBase64Image(String base64Image, String directoryPath, String imageName) throws IOException {
        if (!StringUtils.hasText(base64Image) || !StringUtils.hasText(imageName) || !StringUtils.hasText(directoryPath)) {
            throw new IllegalArgumentException("Base64字符串、图片名称和路径不能为空");
        }
        directoryPath = directoryPath.startsWith("/") ? directoryPath : ("/" + directoryPath);

        byte[] imageBytes = Base64.getDecoder().decode(base64Image.getBytes());
        File targetDirectory = new File(ProjectRootPath, UserImageRoot + directoryPath);
        if (!targetDirectory.exists()) {
            boolean mkdirs = targetDirectory.mkdirs();
        }
        File targetFile = new File(targetDirectory, imageName);
        if (!targetFile.exists()) {
            boolean created = targetFile.createNewFile();
        }

        FileCopyUtils.copy(imageBytes, targetFile);

        return "static/user/image?directoryPath=" + directoryPath + "&imageName=" + imageName;
    }

    public String getImage(String directoryPath, String imageName) throws IOException {
        if (directoryPath == null || imageName == null)
            return null;
        Path path = Path.of(ProjectRootPath, UserImageRoot, directoryPath.endsWith("/") ? directoryPath : (directoryPath + "/"), imageName);

        if (!Files.exists(path)) {
            return null;
        }
        byte[] bytes = Files.readAllBytes(path);
        byte[] encode = Base64.getEncoder().encode(bytes);
        return new String(encode, StandardCharsets.UTF_8);
    }

    public InputStream getDirectImage(String directoryPath, String imageName) throws IOException {
        if (directoryPath == null || imageName == null)
            return null;
        Path path = Path.of(ProjectRootPath, UserImageRoot, directoryPath.endsWith("/") ? directoryPath : (directoryPath + "/"), imageName);

        if (!Files.exists(path)) {
            return null;
        }
        return Files.newInputStream(path);
    }

    public String getSysImage(String directoryPath, String imageName) throws IOException {
        if (directoryPath == null || imageName == null)
            return null;
        Path path = Path.of(ProjectRootPath, SystemImageRoot, directoryPath.endsWith("/") ? directoryPath : (directoryPath + "/"), imageName);

        if (!Files.exists(path)) {
            return null;
        }
        byte[] bytes = Files.readAllBytes(path);
        byte[] encode = Base64.getEncoder().encode(bytes);
        return new String(encode, StandardCharsets.UTF_8);
    }

    public InputStream getDirectSysImage(String directoryPath, String imageName) throws IOException {
        if (directoryPath == null || imageName == null)
            return null;
        Path path = Path.of(ProjectRootPath, SystemImageRoot, directoryPath.endsWith("/") ? directoryPath : (directoryPath + "/"), imageName);

        if (!Files.exists(path)) {
            return null;
        }
        return Files.newInputStream(path);
    }
}
