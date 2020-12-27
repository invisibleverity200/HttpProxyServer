import java.io.IOException;
import java.net.ServerSocket;

public class RequestHandler extends ServerSocket implements Runnable {

    RequestHandler(int port) throws IOException {
        super(port);
    }

    @Override
    public void run() {
        while (true) {
            try {
                new Thread(new ProxyThread(this.accept())).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
