package com.baidu.application.logger.uploader;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTP 日志上传器示例
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public class HttpLogUploader implements ILogUploader {
    
    private static final String TAG = "HttpLogUploader";
    private static final int TIMEOUT = 30000; // 30秒
    private static final int BUFFER_SIZE = 8192;
    
    private final String uploadUrl;
    private final ExecutorService executorService;

    public HttpLogUploader(String uploadUrl) {
        this.uploadUrl = uploadUrl;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void upload(File logFile, UploadCallback callback) {
        executorService.execute(() -> {
            try {
                uploadFileInternal(logFile, callback);
            } catch (Exception e) {
                Log.e(TAG, "Upload failed", e);
                if (callback != null) {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }

    @Override
    public void uploadBatch(File[] logFiles, UploadCallback callback) {
        executorService.execute(() -> {
            int total = logFiles.length;
            int success = 0;
            
            for (int i = 0; i < total; i++) {
                try {
                    uploadFileInternal(logFiles[i], null);
                    success++;
                    
                    if (callback != null) {
                        int progress = (int) ((i + 1) * 100.0 / total);
                        callback.onProgress(progress);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Upload failed: " + logFiles[i].getName(), e);
                }
            }
            
            if (callback != null) {
                if (success == total) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Uploaded " + success + "/" + total + " files");
                }
            }
        });
    }

    /**
     * 内部上传实现
     */
    private void uploadFileInternal(File logFile, UploadCallback callback) throws Exception {
        HttpURLConnection connection = null;
        FileInputStream fileInputStream = null;
        
        try {
            URL url = new URL(uploadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("File-Name", logFile.getName());
            
            fileInputStream = new FileInputStream(logFile);
            OutputStream outputStream = connection.getOutputStream();
            
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalBytes = logFile.length();
            long uploadedBytes = 0;
            
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                uploadedBytes += bytesRead;
                
                if (callback != null) {
                    int progress = (int) (uploadedBytes * 100 / totalBytes);
                    callback.onProgress(progress);
                }
            }
            
            outputStream.flush();
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (callback != null) {
                    callback.onSuccess();
                }
            } else {
                throw new Exception("Server returned code: " + responseCode);
            }
            
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 释放资源
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
