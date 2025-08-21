package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.UploadedFile;

import java.util.Optional;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer>
{
    Optional<UploadedFile> findByFileName(String filename);
}
