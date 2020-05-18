package com.example.jupiter.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "file_record")
public class FileRecord {

    @Id
    private Integer id;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "create_time")
    private String createTime;

    public FileRecord() {
    }

    public FileRecord(String fileName) {
        this.fileName = fileName;
    }

    public FileRecord(String fileName, String createTime) {
        this.fileName = fileName;
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
