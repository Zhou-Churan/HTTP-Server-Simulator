package Login.loginClient;

import client.ResponseHandler;
import message.Response;

public class LoginResponseHandler extends ResponseHandler {

    @Override
    public void handleResponse(Response response) {
        System.out.println("---Received response---");
        System.out.println(response.getMessage());
        int statusCode = response.getStatusCode();
        if (statusCode == 301) {
            System.out.println("The requested resource has been moved to a new location permanently, and you should use the new URL for future requests.");
        } else if (statusCode == 302) {
            System.out.println("The requested resource has been moved to a new location temporarily, and you should use the new URL for the immediate request.");
        } else if (statusCode == 500) {
            System.out.println("The server encountered an internal error and was unable to complete the request.");
        }
        System.out.println("\n");
    }

}
