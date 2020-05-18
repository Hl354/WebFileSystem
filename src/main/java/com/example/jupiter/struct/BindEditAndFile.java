package com.example.jupiter.struct;

import java.util.*;

/**
 * 文件与编辑器绑定的处理，包括时间的记录和是否可编辑的判断
 * 因为是测试项目所以就用这个来写，最好使用Redis，既可以设置过期时间又可以持久化
 */
public class BindEditAndFile {

    private static volatile BindEditAndFile instance;

    private final int LOCK_TIME = 60;

    private final int MAX_TIME = Integer.MAX_VALUE;

    // 文件开始编辑时间，第一个编辑器加入时间
    private HashMap<Integer, Long> startEditTime = new HashMap<>();

    // 文件的所有编辑器开始的编辑时间
    private HashMap<Integer, LinkedHashMap<String, Long>> fileEditStart = new HashMap<>();

    // 编辑器循环到末尾次数保存
    private HashMap<String, Integer> loopCount = new HashMap<>();

    // 编辑器最大循环次数，以防直接关闭浏览器或者页面导致一直在排队
    private final int MAX_LOOP_COUNT = 100;

    private BindEditAndFile() {
    }

    public static BindEditAndFile getInstance() {
        if (instance == null) {
            synchronized (BindEditAndFile.class) {
                if (instance == null) {
                    instance = new BindEditAndFile();
                }
            }
        }
        return instance;
    }

    /**
     * 为一个文件添加一个编辑器
     *
     * @param fileId
     * @param editNumber
     */
    public void addNewEdit(Integer fileId, String editNumber) {
        long currentTimeMillis = System.currentTimeMillis();
        if (startEditTime.get(fileId) == null) {
            startEditTime.put(fileId, currentTimeMillis);
            fileEditStart.put(fileId, createNewMap(editNumber, currentTimeMillis));
        } else {
            fileEditStart.get(fileId).put(editNumber, getLastEditStartTime(fileId) + LOCK_TIME * 1000);
        }
    }

    /**
     * 得到一个文件编辑的时间
     * 返回值负数代表剩余已编辑时间，正数代表多少秒之后可编辑
     *
     * @return
     */
    public long canEditTime(Integer fileId, String editNumber) {
        Long fileStart = startEditTime.get(fileId);
        if (fileStart == null) {
            return 0;
        } else {
            LinkedHashMap<String, Long> map = fileEditStart.get(fileId);
            Long editStart = map.get(editNumber);
            if (editStart == null) {
                return MAX_TIME;
            }
            // 如果一个编辑器的时间小于等于-LOCK_TIME的话，说明此编辑器的可编辑时间已经使用完毕，那么需要将此编辑器放到末尾
            long gapTime = editStart - System.currentTimeMillis();
            if (gapTime <= -LOCK_TIME * 1000) {
                map.remove(editNumber);
                map.put(editNumber, getLastEditStartTime(fileId) + LOCK_TIME * 1000);
                editStart = fileEditStart.get(fileId).get(editNumber);
                gapTime = editStart - System.currentTimeMillis();
                loopCountPlusOne(editNumber);
                moreThanMaxLoopCount(fileId, editNumber);
            }
            return gapTime / 1000;
        }
    }

    /**
     * 判断当前文件ID的第一个编辑器是不是某编辑器
     * @param fileId
     * @param editNumber
     * @return
     */
    public boolean isFileCorrespondingEdit(Integer fileId, String editNumber) {
        LinkedHashMap<String, Long> map = fileEditStart.get(fileId);
        if (map != null) {
            for (Map.Entry<String, Long> entry : map.entrySet()) {
                if (entry.getKey().equals(editNumber)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 移除一个编辑器
     * @param fileId
     * @param editNumber
     */
    public void removeEdit(Integer fileId, String editNumber) {
        LinkedHashMap<String, Long> map = fileEditStart.get(fileId);
        if (map == null || map.size() == 0) {
            return;
        }
        // 移除第一个编辑器开始的时间，如果当前只有一个编辑器那么之际移除，并且文件开始时间记录也要移除
        // 如果当前编辑器有很多个，那么后续编辑器的开始时间需要修改，因为一个编辑器可能只用了锁定时间的一部分
        if (map.size() == 1) {
            map.remove(editNumber);
            fileEditStart.remove(fileId);
            startEditTime.remove(fileId);
        } else {
            // 获取当前编辑器开始时间
            long start = map.get(editNumber);
            // 计算剩余时间
            long gap = LOCK_TIME - (System.currentTimeMillis() - start) / 1000;
            // 移除当前编辑器
            map.remove(editNumber);
            // 其余编辑器的开始时间减去剩余时间
            for (Map.Entry<String, Long> entry : map.entrySet()) {
                map.put(entry.getKey(), entry.getValue() - gap * 1000);
            }
        }
    }

    /**
     * 获取当前最后一个编辑器的开始时间，下一个编辑器的开始时间为当前时间加上LOCK_TIME
     *
     * @param fileId
     * @return
     */
    private Long getLastEditStartTime(Integer fileId) {
        long lastEditStartTime = 0L;
        LinkedHashMap<String, Long> map = fileEditStart.get(fileId);
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            lastEditStartTime = entry.getValue();
        }
        return lastEditStartTime;
    }

    /**
     * 循环次数加一，从头部到尾部
     * @param editNumber
     */
    private void loopCountPlusOne(String editNumber) {
        Integer loopC = loopCount.get(editNumber);
        if (loopC == null) {
            loopC = 0;
        }
        loopCount.put(editNumber, loopC++);
    }

    /**
     * 超过最大循环次数移除
     * @param fileId
     * @param editNumber
     */
    private void moreThanMaxLoopCount(Integer fileId, String editNumber) {
        Integer loopC = loopCount.get(editNumber);
        if (loopC != null && loopC > MAX_LOOP_COUNT) {
            LinkedHashMap<String, Long> map = fileEditStart.get(fileId);
            if (map != null) {
                map.remove(editNumber);
            }
        }
    }

    private LinkedHashMap<String, Long> createNewMap(String key, long value) {
        LinkedHashMap<String, Long> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }

}
