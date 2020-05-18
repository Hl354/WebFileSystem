package com.example.jupiter.service;

import com.example.jupiter.mapper.FileContentMapper;
import com.example.jupiter.mapper.FileDraftMapper;
import com.example.jupiter.mapper.FileRecordMapper;
import com.example.jupiter.model.FileContent;
import com.example.jupiter.model.FileDraft;
import com.example.jupiter.model.FileRecord;
import com.example.jupiter.struct.BindEditAndFile;
import com.example.jupiter.util.DateUtil;
import com.example.jupiter.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class FileService {

    @Autowired
    private FileRecordMapper fileRecordMapper;
    @Autowired
    private FileContentMapper fileContentMapper;
    @Autowired
    private FileDraftMapper fileDraftMapper;

    /**
     * 查询一个文件的文件内容历史
     * @param fileName
     * @return
     */
    public List<Map<String, Object>> queryFileInfo(String fileName) {
        List<Map<String, Object>> resultList = fileRecordMapper.queryFile(fileName);
        if (resultList != null && resultList.size() > 0) {
            for (int i = 0; i < resultList.size(); i++) {
                byte[] bytes = (byte[])resultList.get(i).get("fileContent");
                resultList.get(i).put("fileContent", new String(bytes));
            }
        }
        return resultList;
    }

    /**
     * 添加一个新的文件
     * @param fileName
     * @param fileContent
     * @return
     */
    @Transactional
    public String createNewFile(String fileName, String fileContent) {
        if (fileRecordMapper.selectOne(new FileRecord(fileName)) != null) {
            return "该文件已存在!";
        }
        FileRecord fileRecord = new FileRecord(fileName, DateUtil.nowDateTime());
        int result = fileRecordMapper.insertSelective(fileRecord);
        if (result < 1) {
            return "创建文件记录失败!";
        }
        fileRecord = fileRecordMapper.selectOne(fileRecord);
        result = fileContentMapper.insert(
                new FileContent(fileRecord.getId(), fileContent.getBytes(), fileRecord.getCreateTime()));
        if (result < 1) {
            return "创建文件内容失败!";
        }
        boolean nativeFile = FileUtil.createFile(fileName, fileContent);
        if (!nativeFile) {
            return "创建本地文件失败!";
        }
        return "创建文件成功";
    }

    /**
     * 更新文件内容，首先需要在数据库中插入一条新的文件历史记录，再修改文件
     * @param fileId
     * @param fileContent
     * @return
     */
    @Transactional
    public String updateFileContent(Integer fileId, String fileContent, String editNumber) {
        int result = fileContentMapper.insert(
                new FileContent(fileId, fileContent.getBytes(), DateUtil.nowDateTime()));
        if (result < 1) {
            return "更新文件内容失败!";
        }
        FileRecord fileRecord = fileRecordMapper.selectByPrimaryKey(fileId);
        boolean nativeFile = FileUtil.createFile(fileRecord.getFileName(), fileContent);
        if (!nativeFile) {
            return "更新本地文件失败!";
        }
        BindEditAndFile.getInstance().removeEdit(fileId, editNumber);
        return "更新文件内容成功!";
    }

    /**
     * 获取文件草稿
     * @param fileId
     * @return
     */
    public String queryDraft(Integer fileId) {
        FileDraft fileDraft = fileDraftMapper.selectOne(new FileDraft(fileId));
        if (fileDraft != null) {
            return new String(fileDraft.getFileDraft());
        }
        return "";
    }

    /**
     * 为文件在编辑时添加草稿
     * @param fileId
     * @param fileContent
     * @return
     */
    public String addDraft(Integer fileId, String fileContent) {
        FileDraft fileDraft = fileDraftMapper.selectOne(new FileDraft(fileId));
        int result = 0;
        if (fileDraft == null) {
            result = fileDraftMapper.insert(
                    new FileDraft(fileId, fileContent.getBytes(), DateUtil.nowDateTime()));
        } else {
            fileDraft.setFileDraft(fileContent.getBytes());
            fileDraft.setUpdateTime(DateUtil.nowDateTime());
            result = fileDraftMapper.updateByPrimaryKey(fileDraft);
        }
        if (result < 1) {
            return "保存草稿内容失败!";
        }
        return "保存草稿内容成功!";
    }

}
