package com.example.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "uploaded_file")
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sid;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;
}
