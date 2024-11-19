package message;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import message.Request.ContentType;

public class Response {
    public enum StatusType {OK, Moved_Permanently, Found, Not_Modified, Not_Found, Method_Not_Allowed, Internal_Server_Error}
    public static EnumMap<StatusType, Integer> status = new EnumMap<>(StatusType.class);
    static {
        status.put(StatusType.OK, 200);
        status.put(StatusType.Moved_Permanently, 301);
        status.put(StatusType.Found, 302);
        status.put(StatusType.Not_Modified, 304);
        status.put(StatusType.Not_Found, 404);
        status.put(StatusType.Method_Not_Allowed, 405);
        status.put(StatusType.Internal_Server_Error, 500);
    }
    private final int statusCode;
    private final ContentType contentType;
    private int contentLength = 0;
    private final String body;

    public Response(int statusCode, ContentType contentType, String fileName) {
        this.statusCode = statusCode;
        this.body = fileName;
        if (statusCode >= 400) {
            this.contentType = ContentType.TEXT_PLAIN;
            this.contentLength = -1;
        } else {
            this.contentType = contentType;
            if (body != null) {
                String path = "../" + fileName;
                FileInputStream f = null;
                try {
                    f = new FileInputStream(path);
                    int read = f.read();
                    while (read != -1) {
                        contentLength++;
                        read = f.read();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (f != null) {
                        try {
                            f.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public String getMessage() {
        StatusType statusType = null;
        for (Map.Entry<StatusType, Integer> m : status.entrySet()) {
            if (m.getValue() == statusCode) {
                statusType = m.getKey();
                break;
            }
        }
        return "HTTP/1.1 " + statusCode + " " + statusType + "\nContent-Type: " + contentType + "\nContent-Length: " + contentLength + "\nConnection: keep-alive";
    }
}
