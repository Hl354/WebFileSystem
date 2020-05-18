package com.example.jupiter.controller;

import com.example.jupiter.mapper.FileRecordMapper;
import com.example.jupiter.model.FileRecord;
import com.example.jupiter.service.FileService;
import com.example.jupiter.struct.BindEditAndFile;
import com.example.jupiter.util.FileUtil;
import com.example.jupiter.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin
public class FileController extends BaseController {

    @Autowired
    private FileRecordMapper fileRecordMapper;
    @Autowired
    private FileService fileService;

    @RequestMapping("/checkFileExists")
    public Map checkFileExists(String fileName) {
        FileRecord record = fileRecordMapper.selectOne(new FileRecord(fileName));
        if (record != null) {
            return returnTrueResult(true);
        }
        return returnTrueResult(false);
    }

    @RequestMapping("/queryAllFile")
    public Map queryAllFile() {
        return returnTrueResult(fileRecordMapper.selectAll());
    }

    @GetMapping("/queryFileInfo")
    public Map queryFileInfo(String fileName) {
        if (StringUtil.isEmpty(fileName)) {
            return returnExceptionResult("文件名称缺少!");
        }
        return returnTrueResult(fileService.queryFileInfo(fileName));
    }

    @PostMapping("/createNewFile")
    public Map createNewFile(String fileName, String fileContent) {
        if (StringUtil.isOneEmpty(new String[]{fileName, fileContent})) {
            return returnExceptionResult("文件名或者文件内容缺少!");
        }
        return returnNoDataResult(fileService.createNewFile(fileName, fileContent));
    }

    @PostMapping("updateFileContent")
    public Map updateFileContent(Integer fileId, String fileContent, String editNumber) {
        if (fileId == null) {
            return returnExceptionResult("文件ID缺少!");
        }
        if (StringUtil.isEmpty(fileContent)) {
            return returnExceptionResult("文件内容缺少!");
        }
        if (StringUtil.isEmpty(editNumber)) {
            return returnExceptionResult("当前编辑器不存在!");
        }
        if (!BindEditAndFile.getInstance().isFileCorrespondingEdit(fileId, editNumber)) {
            return returnExceptionResult("当前编辑器不具有提交权限!");
        }
        return returnNoDataResult(fileService.updateFileContent(fileId, fileContent, editNumber));
    }

    @GetMapping("/queryDraft")
    public Map queryDraft(Integer fileId) {
        if (fileId == null) {
            return returnExceptionResult("文件ID缺少!");
        }
        return returnTrueResult(fileService.queryDraft(fileId));
    }

    @PostMapping("/addDraft")
    public Map addDraft(Integer fileId, String fileContent) {
        if (fileId == null) {
            return returnExceptionResult("文件ID缺少!");
        }
        if (StringUtil.isEmpty(fileContent)) {
            return returnExceptionResult("文件内容缺少!");
        }
        return returnNoDataResult(fileService.addDraft(fileId, fileContent));
    }

    @RequestMapping("downloadFile/{fileName}")
    public void downloadFile(HttpServletRequest request,
                             HttpServletResponse response, @PathVariable String fileName) {
        try {
            String showFileName = new String(fileName.getBytes("GB2312"),"ISO-8859-1");
            response.setContentType("multipart/form-data;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;fileName="+ showFileName + ".txt");
            FileUtil.downloadFile(fileName, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/getEditNumber")
    public Map getEditNumber(Integer fileId) {
        if (fileId == null) {
            return returnExceptionResult("文件ID缺少!");
        }
        String editNumber = "EDIT" + UUID.randomUUID();
        BindEditAndFile.getInstance().addNewEdit(fileId, editNumber);
        return returnTrueResult(editNumber);
    }

    @RequestMapping("/canEdit")
    public Map canEdit(Integer fileId, String editNumber) {
        if (fileId == null) {
            return returnExceptionResult("文件ID缺少!");
        }
        if (StringUtil.isEmpty(editNumber)) {
            return returnExceptionResult("当前编辑器不存在!");
        }
        return returnTrueResult(BindEditAndFile.getInstance().canEditTime(fileId, editNumber));
    }

}
