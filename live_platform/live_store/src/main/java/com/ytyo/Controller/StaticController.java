package com.ytyo.Controller;

import com.ytyo.annotation.authority.Remote;
import com.ytyo.Service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class StaticController {
    @Autowired
    ImageService imageService;

    @PostMapping(value = "/static/user/saveImage")
    //由于spring自动URLDecode,很多base64无法encode回去，所以需要encode之后再传过来
    public String saveImage(String base64, String directoryPath, String imageName, HttpServletRequest debug) {
        if (!StringUtils.hasText(base64) || !StringUtils.hasText(directoryPath) || !StringUtils.hasText(imageName))
            return null;
        String path;
        try {
            path = imageService.uploadBase64Image(base64, directoryPath, imageName);
        } catch (Exception e) {
            log.error("保存图片失败", e);
            return null;
        }
        return path;
    }

    @GetMapping(value = "/static/user/image")
    public String getImage(String directoryPath, String imageName) {
        try {
            return imageService.getImage(directoryPath, imageName);
        } catch (IOException e) {
            return null;
        }
    }

    @GetMapping(value = "/direct/static/user/image", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<?> getImageDirect(String directoryPath, String imageName) {
        try {
            InputStream stream = imageService.getDirectImage(directoryPath, imageName);
            if (stream == null)
                return ResponseEntity.notFound().build();
            byte[] bytes = new byte[stream.available()];
            IOUtils.readFully(stream, bytes);
            stream.close();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(bytes.length);
            // 返回包含图像数据的响应实体
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("读取图片失败");
        }
    }


    @GetMapping(value = "/direct/static/system/image", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<?> getSystemImageDirect(String directoryPath, String imageName) {
        try {

            InputStream stream = imageService.getDirectSysImage(directoryPath, imageName);
            if (stream == null)
                return ResponseEntity.notFound().build();
            byte[] bytes = new byte[stream.available()];
            IOUtils.readFully(stream, bytes);
            stream.close();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("图片读取失败");
        }
    }


    @PostMapping("/static/user/saveImages")
    @Remote
    public ResponseEntity<?> saveImages(@RequestBody Map<String, Object> map) {
        if (map == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }
        Object base64s = map.get("base64s");
        Object directoryPath = map.get("directoryPath");
        if (base64s == null || directoryPath == null) {
            return ResponseEntity.badRequest().body("错误的请求");
        }

        try {
            return ResponseEntity.ok(imageService.uploadBase64Images((List<String>) base64s, directoryPath.toString()));
        } catch (Exception e) {
            log.error("保存多图片失败", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("保存失败!");
        }
    }

    @GetMapping(value = "/static/system/image")
    public String getSystemImage(String directoryPath, String imageName) {
        try {
            return imageService.getSysImage(directoryPath, imageName);
        } catch (IOException e) {
            return null;
        }
    }


//    @GetMapping(value = "/static/img", produces = MediaType.IMAGE_PNG_VALUE)
//    @ResponseBody
//    public BufferedImage test(String name) {
//        if (name == null)
//            name = "f.jpg";
//        try {
//            InputStream inputStream = new ClassPathResource("/static/test/" + name).getInputStream();
//            return ImageIO.read(inputStream);
//        } catch (IOException e) {
//            System.out.println("io错误!" + e);
//            return null;
//        }
//    }
//
//    @GetMapping(value = "/static/video")
//    @ResponseBody
//    public ResponseEntity<?> test2() throws IOException {
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set("Content-Type", "video/mp4");
//        responseHeaders.set("Cache-Control", "no-cache, no-store, must-revalidate");
//        InputStream inputStream = new ClassPathResource("/static/test/111.mp4").getInputStream();
//        byte[] bytes = new byte[inputStream.available()];
//        inputStream.read(bytes);
//        return new ResponseEntity<>(bytes, responseHeaders, HttpStatus.OK);
//    }
//
//    @GetMapping(value = "/video1")
//    public String test3() throws IOException {
//        return "forward:/test/111.mp4";
//    }


}
