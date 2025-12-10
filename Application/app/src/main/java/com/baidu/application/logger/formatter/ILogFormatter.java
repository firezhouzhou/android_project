package com.baidu.application.logger.formatter;

/**
 * 日志格式化接口
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public interface ILogFormatter {
    
    /**
     * 格式化对象为字符串
     *
     * @param obj 待格式化对象
     * @return 格式化后的字符串
     */
    String format(Object obj);
}
