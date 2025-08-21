package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.UploadedFile;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer>
{

}
