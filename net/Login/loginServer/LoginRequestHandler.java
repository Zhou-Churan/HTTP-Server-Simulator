package Login.loginServer;

import message.Request;
import message.Response;
import server.RequestHandler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginRequestHandler extends RequestHandler {

    @Override
    public Response handleRequest(Request request) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("---Received POST request---");
        System.out.println(request.getMessage());
        FileInputStream is = null;
        FileOutputStream os = null;
        String dest;
        Date data = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        dest = "net/Login/loginServer/" + formatter.format(data);
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
        System.out.println("Receive file! Saved at : " + dest);
        int statusCode = 200;
        String opt;
        System.out.println("Do you want to enter mode 500? [yes/no]");
        opt = sc.next();
        if (opt.equals("yes")) {
            return new Response(500, null);
        } else {
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
            String message = null;

            is = new FileInputStream(dest);
            String userInfo = new String(is.readAllBytes());
            is.close();
            String regex = "(Login|Signup):([^&]+)&([^&]+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(userInfo);
            String serviceType = null;
            String username = null;
            String password = null;
            if (matcher.find()) {
                serviceType = matcher.group(1);
                username = matcher.group(2);
                password = matcher.group(3);
            }

            is = new FileInputStream("net/Login/loginServer/userData.txt");
            String[] userData = new String(is.readAllBytes()).split("\r\n");
            is.close();

            if (serviceType.equals("Login")) {
                for (String x : userData) {
                    if (x.equals(username+"&"+password)) {
                        message = "Log in successfully";
                        break;
                    }
                }
                if (message == null) message = "Invalid username or password";
            } else {
                for (String x : userData) {
                    if (x.split("&")[0].equals(username)) {
                        message = "Username already exists";
                        break;
                    }
                }
                if (message == null) {
                    message = "Sign up successfully";
                    os = new FileOutputStream("net/Login/loginServer/userData.txt", true);
                    os.write((username+"&"+password+"\r\n").getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
            System.out.println("\n");
            return new Response(statusCode, message);
        }
    }

}
