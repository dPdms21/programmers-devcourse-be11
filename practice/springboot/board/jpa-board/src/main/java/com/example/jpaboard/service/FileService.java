package com.example.jpaboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.util.UUID;

@Slf4j
@Service
public class FileService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    // getBytes() + Files.write() 방식과 비교했을 때의 핵심 차이
    // (기존) byte[] bytes = file.getBytes(); Files.write(...);
    // 메모리: getBytes()는 파일 "전체"를 byte[]로 힙 메모리에 올림 -> 큰 파일/동시 업로드 시 OOM(Out Of Memory) 위험
    // 반면 transferTo는 통째로 올리지 않고 옮기며, 같은 디스크면 복사가 아니라 이동방식이라 가볍고 빠름

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 절대 경로로 다뤄 실행 위치에 영향받지 않게 함
            File dir = new File(uploadDir).getAbsoluteFile();

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(dir, storedFileName);

            file.transferTo(dest);

            log.info("파일 저장: originalFileName = {}, storedFileName = {}", file.getOriginalFilename(), storedFileName);

            return dest.getPath();
        }
        catch (Exception e) {
            throw new IllegalStateException("파일 저장 실패", e);
        }
    }

    public Resource downloadFile(String fileName) {
        try {
            File file = new File(new File(uploadDir).getAbsoluteFile(), fileName);

            // 파일을 가리키는 Resource 생성(실제로 읽어 들이는 게 아니라 '위치만' 잡아두는 단계)
            Resource resource = new UrlResource(file.toURI());

            if (!resource.exists() || !resource.isReadable()) {
                throw new IOException("파일 읽어오는데 실패");
            }

            return resource;
        }
        catch (MalformedInputException e) {
            throw new IllegalStateException("파일 경로 잘못되었음 fileName: " + fileName, e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return;
        }

        File file = new File(filePath);

        if (!file.exists()) {
            return;
        }

        boolean deleted = file.delete();

        if (!deleted) {
            log.warn("첨부파일 삭제 실패 (디스크에 남음): filePath = {} ", filePath);
        }
    }
}
