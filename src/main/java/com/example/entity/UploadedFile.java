package com.example.entity;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "uploaded_file") // avoid using reserved keywords like "File"
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sid;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData;   // store file content

    @Column(nullable = false)
    private String fileName;   // original file name

    @Column(nullable = false)
    private String filePath;   // optional, if you want to also save path
}
