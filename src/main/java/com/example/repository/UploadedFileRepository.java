package com.example.repository;

import com.example.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.Optional;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer> {
    Optional<UploadedFile> findByFilePath(String filePath);

    @Modifying
    @Transactional
    void deleteByFilePath(String filePath);

    Optional<UploadedFile> findByFileName(String fileName);
}
