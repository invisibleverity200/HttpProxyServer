import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            int port = Integer.valueOf(args[0]);
            new Thread(new RequestHandler(port)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

