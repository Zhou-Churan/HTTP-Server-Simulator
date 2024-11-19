package message;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Request {

    public enum RequestType {GET, POST};
    public enum ContentType {TEXT_PLAIN, TEXT_HTML, IMAGE_JPG};

    private final RequestType requestType;
    private final String hostAddress;
    private final int port;
    private ContentType contentType = null;
    private int contentLength = 0;
    private String body = null;

    // GET
    public Request(RequestType requestType, String hostAddress, int port) {
        this.requestType = requestType;
        this.hostAddress = hostAddress;
        this.port = port;
    }

    // POST
    public Request(RequestType requestType, String hostAddress, int port, ContentType contentType, String fileName) throws FileNotFoundException {
        this.requestType = requestType;
        this.hostAddress = hostAddress;
        this.port = port;
        this.contentType = contentType;
        this.body = fileName;
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

    public RequestType getRequestType() {
        return requestType;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getFilePath() {
        return "../" + body;
    }

    public String getMessage() {
        return requestType + (requestType == RequestType.GET ? "" : " " + body) + " HTTP/1.1" + "\nHost: " + hostAddress + ":" + port + (requestType == RequestType.GET ? "" : "\nContent-Type: " + contentType + "\nContent-Length: " + contentLength);
    }

}
