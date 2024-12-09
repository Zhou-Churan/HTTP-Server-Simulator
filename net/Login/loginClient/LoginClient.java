package Login.loginClient;

import client.Client;
import client.ResponseHandler;
import message.Request;
import message.Response;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LoginClient extends Client {

    public LoginClient(String serverName, int port) throws IOException {
        super(serverName, port);
    }

    private final ResponseHandler handler = new LoginResponseHandler();

    @Override
    public ResponseHandler getResponseHandler() {
        return handler;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("Connecting to Server : " + clientSocket.getRemoteSocketAddress() + ", Port : " + clientSocket.getPort());
            System.out.println("Remote IP : " + clientSocket.getRemoteSocketAddress());
            System.out.println("\n");
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            String exit = "no";
            while (exit.equals("no")) {
                System.out.println("Log in or Sign up? [1/2]");
                String opt = sc.next();
                // sending request
                System.out.print("Enter Username: ");
                String username = sc.next();
                System.out.print("Enter Password: ");
                String password = sc.next();
                String userInfo = (opt.equals("1") ? "Login" : "Signup") + ":" + username + "&" + password;
                String path = "net/Login/loginClient/userInfo.txt";
                try (FileOutputStream os = new FileOutputStream(path)) {
                    os.write(userInfo.getBytes(StandardCharsets.UTF_8), 0, userInfo.getBytes().length);
                }
                Request request = new Request(Request.RequestType.POST, clientSocket.getLocalSocketAddress().toString(), clientSocket.getLocalPort(), Request.ContentType.TEXT_PLAIN, path);
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
        try {
            Thread t = new LoginClient("localhost", 80);
            t.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
