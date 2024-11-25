package Login.loginClient;

import client.Client;
import client.ResponseHandler;

import java.io.IOException;

public class LoginClient extends Client {

    public LoginClient(String serverName, int port) throws IOException {
        super(serverName, port);
    }

    private final ResponseHandler handler = new LoginResponseHandler();

    @Override
    public ResponseHandler getResponseHandler() {
        return handler;
    }

    public static void main(String[] args) {
        Client.main(args);
    }

}
