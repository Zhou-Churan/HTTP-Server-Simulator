package server;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import message.Request;
import message.Response;

public class Server extends Thread {

    private final ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(60 * 1000);
    }

    private final RequestHandler handler = new RequestHandler();

    public RequestHandler getRequestHandler() {
        return handler;
    }

    public void run() {
        Socket server = null;
        while (true) {
            try {
                if (server == null) {
                    System.out.println("Port : " + serverSocket.getLocalPort() + ", Waiting for connection to Client...");
                    server = serverSocket.accept();
                    System.out.println("Connected, remote IP : " + server.getRemoteSocketAddress());
                }
                System.out.println("\n");
                DataInputStream in = new DataInputStream(server.getInputStream());
                String message = in.readUTF();
                RequestHandler requestHandler = getRequestHandler();
                Request request = requestHandler.parseRequest(message);
                Response response = requestHandler.handleRequest(request);
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF(response.getMessage());
            }  catch (SocketTimeoutException e) {
                System.out.println("Socket timed out");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter port: ");
        int port = sc.nextInt();
        try {
            Thread t = new Server(port);
            t.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
