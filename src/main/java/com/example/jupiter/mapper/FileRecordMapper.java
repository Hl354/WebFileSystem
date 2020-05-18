package com.example.jupiter.mapper;

import com.example.jupiter.model.FileRecord;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface FileRecordMapper extends Mapper<FileRecord> {

    @Select("SELECT fc.file_id fileId, fc.file_content fileContent, fc.create_time createTime " +
            "FROM file_record fr, file_content fc WHERE fr.file_name = #{fileName} AND fr.id = fc.file_id ORDER BY fc.create_time DESC")
    public List<Map<String, Object>> queryFile(String fileName);

}
