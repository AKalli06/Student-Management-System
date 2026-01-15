import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class client {
    public static void output(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            if (line.equals("")) break;
            System.out.println(line);
        }

        if (line == null) {
            System.out.println("Server closed connection.");
        }
    }

    public static void main(String[] args) {
        try {
            //Socket socket = new Socket("192.168.10.121", 50); //laptop ip
            Socket socket = new Socket("192.168.10.92", 50);
            System.out.println("Established connection with the server.");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner keyboard = new Scanner(System.in);
            InetAddress localHost = null;
            try {
                localHost=InetAddress.getLocalHost();
                out.println("A new user was connected:");
                out.println("Host name => " + localHost.getHostName());
                out.println("IP address => " + localHost.getHostAddress());
                out.println("");
            } catch (UnknownHostException e) {
                System.out.println("error");
            }


            int choice = 0;
            while (choice != 7) {
                // 1. Read menu
                output(in);
                // 2. Send choice
                System.out.print("Choose an option: ");
                String choiceStr = keyboard.nextLine();
                out.println(choiceStr);
                try{
                    choice=Integer.parseInt(choiceStr);
                }
                catch (Exception e){
                    output(in);
                    continue;
                }
                switch (choice) {
                    case 1:
                        output(in);   // response
                        output(in);   // next menu
                        break;
                    case 2, 3, 4:
                        output(in);   // prompt
                        String clientInput = keyboard.nextLine();
                        out.println(clientInput);
                        output(in);   // response
                        output(in);   // next menu
                        break;
                    case 5:
                        output(in);
                        break;
                    case 6:
                        for(int i=0; i<4; i++){
                            output(in);
                            String sId=keyboard.nextLine();
                            out.println(sId);
                        }
                        output(in);
                        break;
                    case 7:
                        output(in);
                        out.println("A user was disconnected:");
                        out.println("Host name => "+localHost.getHostName());
                        out.println("IP address => "+localHost.getHostAddress());
                        out.println("");
                        break;
                    default:
                        output(in);
                        break;
                }
            }
            socket.close();
        } catch (Exception e) {
            System.out.println("Unable to establish connection with the server");
        }
    }
}