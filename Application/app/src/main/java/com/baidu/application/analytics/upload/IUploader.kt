package com.baidu.application.analytics.upload

import com.baidu.application.analytics.model.EventModel

/**
 * 上传器接口
 *
 * 可自定义实现不同的上传策略
 */
interface IUploader {

    /**
     * 上传事件
     *
     * @param events 待上传的事件列表
     * @return 上传结果
     */
    suspend fun upload(events: List<EventModel>): Result<Unit>
}
