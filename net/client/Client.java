package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import message.Request;
import message.Request.RequestType;

public class Client {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter hostname: ");
        String serverName = sc.next();
        System.out.print("Enter port: ");
        int port = sc.nextInt();
        try {
            System.out.println("Connecting to Server : " + serverName + ", Port : " + port);
            Socket client = new Socket(serverName, port);
            System.out.println("Remote IP : " + client.getRemoteSocketAddress());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            String exit = "no";
            while (exit.equals("no")) {
                System.out.println("Enter request type: ");
                String method = sc.next();
                Request request = null;
                if (method.equals("POST")) {
                    System.out.println("Enter file name: ");
                    String fileName = sc.next();
                    String path = "net/client/" +fileName;
                    Request.ContentType contentType = null;
                    if (path.endsWith(".txt")) {
                        contentType = Request.ContentType.TEXT_PLAIN;
                    } else if (path.endsWith(".html")) {
                        contentType = Request.ContentType.TEXT_HTML;
                    } else if (path.endsWith(".jpg")) {
                        contentType = Request.ContentType.IMAGE_JPG;
                    } else {
                        System.out.println("Invalid file type");
                        System.out.println("\n\n\n");
                        System.out.println("Do you want to exit the program? [yes/no]");
                        exit = sc.next();
                        continue;
                    }
                    request = new Request(RequestType.POST, client.getLocalSocketAddress().toString(), port, contentType, path);
                } else if (method.equals("GET")) {
                    request = new Request(RequestType.GET, client.getLocalSocketAddress().toString(), port);
                } else {
                    System.out.println("Invalid request type");
                    System.out.println("\n\n\n");
                    System.out.println("Do you want to exit the program? [yes/no]");
                    exit = sc.next();
                    continue;
                }
                System.out.println("\n\n\n");
                out.writeUTF(request.getMessage());
                DataInputStream in = new DataInputStream(client.getInputStream());
                String response = in.readUTF();
                System.out.println("---Received response---");
                System.out.println(response);
                System.out.println("\n\n\n");
                System.out.println("Do you want to exit the program? [yes/no]");
                exit = sc.next();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
