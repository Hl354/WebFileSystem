package com.example.jupiter.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "file_draft")
public class FileDraft {

    @Id
    private Integer id;
    @Column(name = "file_id")
    private Integer fileId;
    @Column(name = "file_draft")
    private byte[] fileDraft;
    @Column(name = "update_time")
    private String updateTime;

    public FileDraft() {
    }

    public FileDraft(Integer fileId) {
        this.fileId = fileId;
    }

    public FileDraft(Integer fileId, byte[] fileDraft, String updateTime) {
        this.fileId = fileId;
        this.fileDraft = fileDraft;
        this.updateTime = updateTime;
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

    public byte[] getFileDraft() {
        return fileDraft;
    }

    public void setFileDraft(byte[] fileDraft) {
        this.fileDraft = fileDraft;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
