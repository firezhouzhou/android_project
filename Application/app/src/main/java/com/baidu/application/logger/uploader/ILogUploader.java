package com.baidu.application.logger.uploader;

import java.io.File;

/**
 * 日志上传接口
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public interface ILogUploader {
    
    /**
     * 上传日志文件
     *
     * @param logFile 日志文件
     * @param callback 上传回调
     */
    void upload(File logFile, UploadCallback callback);
    
    /**
     * 批量上传日志文件
     *
     * @param logFiles 日志文件数组
     * @param callback 上传回调
     */
    void uploadBatch(File[] logFiles, UploadCallback callback);
    
    /**
     * 上传回调接口
     */
    interface UploadCallback {
        /**
         * 上传成功
         */
        void onSuccess();
        
        /**
         * 上传失败
         *
         * @param error 错误信息
         */
        void onFailure(String error);
        
        /**
         * 上传进度
         *
         * @param progress 进度（0-100）
         */
        void onProgress(int progress);
    }
}
