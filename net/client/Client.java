package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import message.Request;
import message.Request.RequestType;
import message.Response;

public class Client extends Thread {

    protected final Socket clientSocket;

    public Client(String serverName, int port) throws IOException {
        clientSocket = new Socket(serverName, port);
        clientSocket.setSoTimeout(60 * 1000);
    }

    private final ResponseHandler handler = new ResponseHandler();

    public ResponseHandler getResponseHandler() {
        return handler;
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("Connecting to Server : " + clientSocket.getRemoteSocketAddress() + ", Port : " + clientSocket.getPort());
            System.out.println("Remote IP : " + clientSocket.getRemoteSocketAddress());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            String exit = "no";
            while (exit.equals("no")) {

                // sending request
                System.out.println("Enter request type: ");
                String method = sc.next();
                Request request;
                if (method.equals("POST")) {
                    System.out.println("Enter file name: ");
                    String fileName = sc.next();
                    String path = "net/client/" +fileName;
                    Request.ContentType contentType;
                    if (path.endsWith(".txt")) {
                        contentType = Request.ContentType.TEXT_PLAIN;
                    } else if (path.endsWith(".html")) {
                        contentType = Request.ContentType.TEXT_HTML;
                    } else if (path.endsWith(".jpg")) {
                        contentType = Request.ContentType.IMAGE_JPG;
                    } else {
                        System.out.println("Invalid file type");
                        System.out.println("\n");
                        System.out.println("Do you want to exit the program? [yes/no]");
                        exit = sc.next();
                        continue;
                    }
                    request = new Request(RequestType.POST, clientSocket.getLocalSocketAddress().toString(), clientSocket.getLocalPort(), contentType, path);
                } else if (method.equals("GET")) {
                    System.out.println("Enter file name: ");
                    String fileName = sc.next();
                    String path = "net/server/" +fileName;
                    request = new Request(RequestType.GET, clientSocket.getLocalSocketAddress().toString(), clientSocket.getLocalPort(), path);
                } else {
                    request = new Request(clientSocket.getLocalSocketAddress().toString(), clientSocket.getLocalPort(), method);
                }
                System.out.println("\n");
                out.writeUTF(request.getMessage());

                // getting response
                ResponseHandler responseHandler = getResponseHandler();
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                Response response = responseHandler.parseResponse(in.readUTF());
                responseHandler.handleResponse(response);
                System.out.println("Do you want to exit the program? [yes/no]");
                exit = sc.next();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter hostname: ");
        String serverName = sc.next();
        System.out.print("Enter port: ");
        int port = sc.nextInt();
        try {
            Thread t = new Client(serverName, port);
            t.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
