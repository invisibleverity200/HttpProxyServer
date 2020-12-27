import java.io.IOException;

public class Main {
    public static void main(String[] args) {
            try {
                new Thread(new RequestHandler(7236)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}

