package cn.itruschina.crl.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author: yang_yunxiang
 * @Date: 2019/4/24 15:33
 */
public class CommonUtil {
    public static void writeJsonResponse(HttpServletResponse response, String json) {
        if (response != null) {
            try {
                byte[] content = json.getBytes("UTF-8");
                response.setContentType("text/json");
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setCharacterEncoding("UTF-8");
                response.setContentLength(content.length);
                OutputStream stream = response.getOutputStream();
                stream.write(content);
                stream.flush();
                stream.close();
            } catch (IOException ignore) {
            }
        }
    }

}
