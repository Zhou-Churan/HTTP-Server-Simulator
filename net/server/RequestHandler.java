package server;

import message.Request;
import message.Response;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHandler {

    static HashMap<String, Boolean> fileDirectory = new HashMap<>(); // to check if file has been sent

    static {
        fileDirectory.put("net/server/1.txt", false);
        fileDirectory.put("net/server/2.html", false);
        fileDirectory.put("net/server/3.jpg", false);
    }

    public Request parseRequest(String message) throws IOException {
        // TODO
        String methodPattern = "^(\\w+)\\s";
        String pathPattern = "\\s([^\\s]+)\\sHTTP/";
        String contentTypePattern = "Content-Type:\\s*([^\\r\\n]+)";
        String hostPattern = "Host:\\s*([^\\s:]+)(?::(\\d+))?";

        Pattern methodMatcher = Pattern.compile(methodPattern);
        Pattern pathMatcher = Pattern.compile(pathPattern);
        Pattern contentTypeMatcher = Pattern.compile(contentTypePattern);
        Pattern hostMatcher = Pattern.compile(hostPattern);

        Matcher methodMatch = methodMatcher.matcher(message);
        Matcher pathMatch = pathMatcher.matcher(message);
        Matcher contentTypeMatch = contentTypeMatcher.matcher(message);
        Matcher hostMatch = hostMatcher.matcher(message);

        String method = null;
        String fileName = null;
        String contentType = null;
        String hostAddress = null;
        int port = -1;

        if (methodMatch.find()) {
            method = methodMatch.group(1);
        }
        if (pathMatch.find()) {
            fileName = pathMatch.group(1);
        }
        if (contentTypeMatch.find()) {
            contentType = contentTypeMatch.group(1).trim();
        }
        if (hostMatch.find()) {
            hostAddress = hostMatch.group(1);
            if (hostMatch.group(2) != null) {
                port = Integer.parseInt(hostMatch.group(2));
            }
        }

        if (method.equals("GET")) {
            return new Request(Request.RequestType.GET, hostAddress, port);
        } else {
            return new Request(Request.RequestType.POST, hostAddress, port, Request.ContentType.valueOf(contentType), fileName);
        }

    }

    public void handleRequest(Request request) throws IOException {
        if (request.getRequestType() == Request.RequestType.GET) {
            System.out.println("---Received GET request---");
            System.out.println(request.getMessage());
        } else if (request.getRequestType() == Request.RequestType.POST) {
            System.out.println("---Received POST request---");
            System.out.println(request.getMessage());
            FileInputStream is = null;
            FileOutputStream os = null;
            String dest;
            Date data = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            dest = "server/" + formatter.format(data);
            Request.ContentType contentType = request.getContentType();
            if (contentType == Request.ContentType.TEXT_PLAIN) {
                dest += ".txt";
            } else if (contentType == Request.ContentType.TEXT_HTML) {
                dest += ".html";
            } else if (contentType == Request.ContentType.IMAGE_JPG) {
                dest += ".jpg";
            }
            try {
                is = new FileInputStream(request.getFilePath());
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
            System.out.println("Receive file " + dest);
        } else {
            System.out.println("Invalid request");
        }
    }

    public Response buildResponse() {
        System.out.println("---Sending response---");
        System.out.println("Enter file name:");
        Scanner sc = new Scanner(System.in);
        String fileName = "net/server/" + sc.next();
        String path = "../" + fileName;
        int statusCode = 200;
        String opt;
        System.out.println("Do you want to enter mode 500? [yes/no]");
        opt = sc.next();
        if (opt.equals("yes")) {
            return new Response(500, Request.ContentType.TEXT_PLAIN, fileName);
        }
        if (new File(path).exists()) {
            if (fileDirectory.get(fileName)) {
                statusCode = 304;
            } else {
                System.out.println("Do you want to enter mode 405? [yes/no]");
                opt = sc.next();
                if (opt.equals("yes")) {
                    return new Response(405, Request.ContentType.TEXT_PLAIN, fileName);
                }
                System.out.println("Do you want to move the server url (mode 301/302)? [yes/no]");
                opt = sc.next();
                if (opt.equals("yes")) {
                    System.out.println("Permanently or Temporarily? [p/t]");
                    opt = sc.next();
                    if (opt.equals("p")) {
                        statusCode = 301;
                    } else {
                        statusCode = 302;
                    }
                }
            }
            Request.ContentType contentType;
            if (fileName.endsWith(".txt")) contentType = Request.ContentType.TEXT_PLAIN;
            else if (fileName.endsWith(".html")) contentType = Request.ContentType.TEXT_HTML;
            else contentType = Request.ContentType.IMAGE_JPG;
            return new Response(statusCode, contentType, fileName);
        } else {
            return new Response(404, Request.ContentType.TEXT_PLAIN, fileName);
        }
    }

}
