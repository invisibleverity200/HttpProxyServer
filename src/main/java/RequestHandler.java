import java.io.IOException;
import java.net.ServerSocket;

public class RequestHandler extends ServerSocket implements Runnable {

    RequestHandler(int port) throws IOException {
        super(port);
    }

    @Override
    public void run() {
        int threadNumber = 0;
        while (true) {
            try {
                new Thread(new ProxyThread(this.accept(), "Thread" + String.valueOf(threadNumber))).start();
                System.out.println("Thread: " + Util.ANSI_GREEN+"Thread" + String.valueOf(threadNumber) + Util.ANSI_RESET + " created");
                threadNumber++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
