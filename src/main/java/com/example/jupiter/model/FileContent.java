package com.example.jupiter.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "file_content")
public class FileContent {

    @Id
    private Integer id;
    @Column(name = "file_id")
    private Integer fileId;
    @Column(name = "file_content")
    private byte[] fileContent;
    @Column(name = "create_time")
    private String createTime;

    public FileContent() {
    }

    public FileContent(Integer fileId, byte[] fileContent, String createTime) {
        this.fileId = fileId;
        this.fileContent = fileContent;
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
