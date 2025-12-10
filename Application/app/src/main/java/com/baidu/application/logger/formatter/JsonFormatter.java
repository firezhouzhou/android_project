package com.baidu.application.logger.formatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON 格式化器
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public class JsonFormatter implements ILogFormatter {
    
    private static final int INDENT_SPACES = 4;

    @Override
    public String format(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        try {
            String jsonStr;
            if (obj instanceof String) {
                jsonStr = (String) obj;
            } else {
                jsonStr = obj.toString();
            }
            
            // 尝试解析为 JSONObject
            if (jsonStr.trim().startsWith("{")) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                return jsonObject.toString(INDENT_SPACES);
            }
            
            // 尝试解析为 JSONArray
            if (jsonStr.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonStr);
                return jsonArray.toString(INDENT_SPACES);
            }
            
            return jsonStr;
        } catch (JSONException e) {
            return "Invalid JSON: " + obj.toString();
        }
    }
}
