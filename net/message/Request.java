package message;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Request {

    public enum RequestType {GET, POST, INVALID};
    public enum ContentType {TEXT_PLAIN, TEXT_HTML, IMAGE_JPG};

    private final RequestType requestType;
    private final String hostAddress;
    private final int port;
    private ContentType contentType = null;
    private int contentLength = 0;
    private final String body;

    // GET
    public Request(RequestType requestType, String hostAddress, int port, String path) {
        this.requestType = requestType;
        this.hostAddress = hostAddress;
        this.port = port;
        this.body = path;
    }

    // POST
    public Request(RequestType requestType, String hostAddress, int port, ContentType contentType, String path) throws FileNotFoundException {
        this.requestType = requestType;
        this.hostAddress = hostAddress;
        this.port = port;
        this.contentType = contentType;
        this.body = path;
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

    // INVALID
    public Request(String hostAddress, int port, String method) {
        this.requestType = RequestType.INVALID;
        this.hostAddress = hostAddress;
        this.port = port;
        this.body = method;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getFilePath() {
        return body;
    }

    public String getMessage() {
        return requestType + " " + body + " HTTP/1.1" + "\nHost: " + hostAddress + ":" + port + (requestType == RequestType.GET ? "" : "\nContent-Type: " + contentType + "\nContent-Length: " + contentLength);
    }

}
