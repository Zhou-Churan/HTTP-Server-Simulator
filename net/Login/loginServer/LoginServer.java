package Login.loginServer;

import server.RequestHandler;
import server.Server;

import java.io.IOException;

public class LoginServer extends Server {

    public LoginServer(int port) throws IOException {
        super(port);
    }

    private final RequestHandler handler = new LoginRequestHandler();

    @Override
    public RequestHandler getRequestHandler() {
        return handler;
    }

    public static void main(String[] args) {
        try {
            Thread t = new LoginServer(80);
            t.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
