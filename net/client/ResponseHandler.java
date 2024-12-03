package client;

import message.Request;
import message.Response;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseHandler {

    public Response parseResponse(String message) {

        String statusLinePattern = "^HTTP/\\d+\\.\\d+\\s+(\\d+)\\s+.+$";
        String contentTypePattern = "^Content-Type:\\s*([^\\r\\n]+)$";

        Pattern statusLineMatcher = Pattern.compile(statusLinePattern, Pattern.MULTILINE);
        Pattern contentTypeMatcher = Pattern.compile(contentTypePattern, Pattern.MULTILINE);

        Matcher statusLineMatch = statusLineMatcher.matcher(message);
        Matcher contentTypeMatch = contentTypeMatcher.matcher(message);

        int statusCode = -1;
        String contentType = null;
        String path = null;

        if (statusLineMatch.find()) {
            statusCode = Integer.parseInt(statusLineMatch.group(1));
        }

        if (contentTypeMatch.find()) {
            contentType = contentTypeMatch.group(1).trim();
        }

        if (message.split("\n").length <= 3) return new Response(statusCode);

        path = message.split("\n")[4];

        return new Response(statusCode, Request.ContentType.valueOf(contentType), path);
    }

    public void handleResponse(Response response) throws IOException {
        System.out.println("---Received response---");
        System.out.println(response.getMessage());
        int statusCode = response.getStatusCode();
        if (response.getFilePath() != null && statusCode < 400 && statusCode != 304) {
            FileInputStream is = null;
            FileOutputStream os = null;
            String dest;
            Date data = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            dest = "net/client/" + formatter.format(data);
            Request.ContentType contentType = response.getContentType();
            if (contentType == Request.ContentType.TEXT_PLAIN) {
                dest += ".txt";
            } else if (contentType == Request.ContentType.TEXT_HTML) {
                dest += ".html";
            } else if (contentType == Request.ContentType.IMAGE_JPG) {
                dest += ".jpg";
            }
            try {
                is = new FileInputStream(response.getFilePath());
                os = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } finally {
                is.close();
                os.close();
            }
            System.out.println("Receive file! Saved at : " + dest);
            if (statusCode == 301) {
                System.out.println("The requested resource has been moved to a new location permanently, and you should use the new URL for future requests.");
            } else if (statusCode == 302) {
                System.out.println("The requested resource has been moved to a new location temporarily, and you should use the new URL for the immediate request.");
            }
        } else if (statusCode == 304) {
            System.out.println("The requested resource has already been requested and not been modified, you can continue to use the cached version.");
        } else if (statusCode == 404) {
            System.out.println("The requested resource was not found on the server.");
        } else if (statusCode == 405) {
            System.out.println("The method specified in the request is not allowed for the requested resource.");
        } else if (statusCode == 500) {
            System.out.println("The server encountered an internal error and was unable to complete the request.");
        }
        System.out.println("\n");
    }

}
