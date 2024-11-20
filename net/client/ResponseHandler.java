package client;

import message.Request;
import message.Response;
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
        String path = message.split("\n")[4];

        if (statusLineMatch.find()) {
            statusCode = Integer.parseInt(statusLineMatch.group(1));
        }

        if (contentTypeMatch.find()) {
            contentType = contentTypeMatch.group(1).trim();
        }

        return new Response(statusCode, Request.ContentType.valueOf(contentType), path);
    }

    public void handle(Response response) {
        // TODO
        // handle statusCode
        System.out.println("---Received response---");
        String[] lines = response.getMessage().split("\n");
        for (int i = 0; i < lines.length - 1; i++) {
            System.out.println(lines[i]);
        }
        System.out.println("\n\n\n");
    }

}
