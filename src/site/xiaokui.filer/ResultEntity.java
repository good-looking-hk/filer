package site.xiaokui.filer;

import cn.hutool.json.JSONObject;

import java.util.HashMap;

/**
 * @author HK
 * @date 2019-10-30 09:49
 */
public class ResultEntity extends HashMap<String, Object> {

    public ResultEntity(Integer code, String msg) {
        super(4);
        put("code", code);
        put("msg", msg);
    }

    public static ResultEntity ok() {
        return new ResultEntity(200, "ok");
    }

    public static ResultEntity ok(String msg) {
        return new ResultEntity(200, msg);
    }


    public static ResultEntity failed() {
        return new ResultEntity(400, "failed");
    }

    public static ResultEntity failed(String msg) {
        return new ResultEntity(400, msg);
    }

    public static ResultEntity error() {
        return new ResultEntity(500, "异常错误");
    }

    public static ResultEntity error(String msg) {
        return new ResultEntity(500, msg);
    }

    @Override
    public ResultEntity put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }
}
