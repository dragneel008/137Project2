import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class Listener extends Thread {

	private Socket sock;
	public Listener(Socket sock){
		this.sock = sock;
	}
	
	public String sendHeader(int statusCode){
		String header = "HTTP/1.1 ";
		switch(statusCode){
			case 200: 
				header += "200 OK\r\n"; 
				break;
			case 301: 
				header += "301 Moved Permanently\r\n"; 
				break;
			case 403: 
				header += "403 Forbidden\r\n"; 
				break;
			case 404: 
				header += "404 Not Found\r\n"; 
				break;
			case 501: 
				header += "501 Not Implemented\r\n"; 
				break;
		}
		header += "Content-Type: text/html\r\n";
		header += "Server: Bot\r\n";
		return header;
	}
	
	@Override
	public void run(){

		BufferedReader input = null;
		PrintStream output = null;
		String file = null;
		String files = "default";
		String parameter = "";
		String[] parameters = new String[]{};
		int method = 0;
		
		try{
			input = new BufferedReader( new InputStreamReader( sock.getInputStream() ) );
			output = new PrintStream( new BufferedOutputStream( sock.getOutputStream() ) );

			// Process REQUEST
			String inputString = input.readLine();
			if(inputString.startsWith("GET")){
				//parse
				method = 1;
				file = inputString.split("\\s")[1];
				if(file.contains("?")){
					parameter = file.substring(file.indexOf('?')+1);
					file = file.substring(0,file.indexOf('?'));
				}
			}else if(inputString.startsWith("POST")){
				file = inputString.split("\\s")[1];
				while(input.ready() && input.readLine() != "\r\n");
				parameter = input.ready()?input.readLine():"";
			}else{//Invalid
				output.print(sendHeader(501)+"\r\n");
				output.close();
				return;
			}

			parameters = parameter.equals("")?new String[]{}:parameter.split("&");

			if(file.endsWith("/")){
				file += "index.html"; //appends
			}
	        while (file.indexOf("/") == 0)
	          file = file.substring(1);
        	if (file.indexOf(':') >= 0 || file.indexOf("..") >= 0 || file.indexOf('|') >= 0){
        		throw new FileNotFoundException();
        	}

	        if (new File(file).isDirectory()) { //append if trailing / is missing.
	          file = file.replace('\\', '/');
	          output.print(sendHeader(301) + "Location: /" + file + "/\r\n\r\n");
	          output.close();
	          return;
	        }

	        System.out.println("FileName: " + file);
	        System.out.println("Parameters: " + Arrays.toString(parameters));
        	InputStream f = new FileInputStream(file); // open the file
        	f.close();

        	files = new File(file).getName(); 
        	Files.copy(new File(file).toPath(), new File(files).toPath(), StandardCopyOption.REPLACE_EXISTING);
        	
        	//send response
        	output.print(sendHeader(200) + "\r\n");
        	output.print("<!DOCTYPE html>\r\n");
			output.print("<html>\r\n");
			output.print("<head><title>Web Server</title></head>\r\n");
			output.print("<body>\r\n");
			output.print("<p>" + file + " saved to server.</p>");

		}catch(FileNotFoundException e){
			System.out.println("File not found.");
			//send response
			output.print(sendHeader(404) + "\r\n");
        	output.print("<!DOCTYPE html>\r\n");
			output.print("<html>\r\n");
			output.print("<head><title>Web Server</title></head>\r\n");
			output.print("<body>\r\n");
			output.print("<p>" + file + " not found.</p>");
          	try{
	          	PrintWriter out = new PrintWriter(file);
	          	out.close();
	          	output.print("<p>" + file + " saved to server.</p>");
          	}catch(FileNotFoundException ex){
          		
          	}
		}catch(IOException exe){
			exe.printStackTrace();
		}finally{
			//request parameters
			output.print("<p> " + ((method==1)?"GET":"POST") + " Request Parameters </p>\r\n");
			output.print("<table border ='1'>\r\n");
			output.print("<tr>\r\n");
			output.print("<th>Key</th>\r\n");
			output.print("<th>Value</th>\r\n");
			output.print("</tr>\r\n");
			if(parameters.length != 0){
				for(int i=0; i!=parameters.length; i++){
					String[] tokens = parameters[i].split("=");
					output.print("<tr>\r\n");
					output.print("<td>" + tokens[0] + "</td>\r\n");
					output.print("<td>" + tokens[1] + "</td>\r\n");
					output.print("</tr>\r\n");
				}
			}else{
				output.print("<tr>\r\n");
				output.print("<td colspan='2'>Empty</td>\r\n");
				output.print("</tr>\r\n");
			}
			output.print("</table>\r\n");
			output.print("</body>\r\n");
			output.print("</html>\r\n");
			output.close();
		}
	}
}
