package Login.loginServer;

import server.RequestHandler;
import server.Server;

import java.io.IOException;
import java.util.HashMap;

public class LoginServer extends Server {

    public LoginServer(int port) throws IOException {
        super(port);
    }

    private final RequestHandler handler = new LoginRequestHandler();

    @Override
    public RequestHandler getRequestHandler() {
        return handler;
    }

    private final HashMap<String, String> userInfo = new HashMap<>();

    public HashMap<String, String> getUserInfo() {
        return userInfo;
    }

    public static void main(String[] args) {
        Server.main(args);
    }

}
