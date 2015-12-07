import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Webserver {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerSocket s;
		System.out.println("Connecting to Port 64062.");
		
		try{
			s = new ServerSocket(64062); //my student number
			System.out.println("Port Connected.");
			while(true){
				Socket socket = s.accept();
				new Listener(socket).start();
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		
	}

}
