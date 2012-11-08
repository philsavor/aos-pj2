package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public  class ServerListenerThread extends Thread {
	/*
	 * To receive messages,including Request,Reply,Complete messages.
	 */
	
	
    // Display a message, preceded by the name of the current thread
	// And write to file.
    static void threadWriteFile(String message) {
	    String out_string = String.format("%s%n",
	                                      message);
	    try {
	         BufferedWriter out = new BufferedWriter(new FileWriter("server_out"+ServerSharedMemory.nodeId +".txt",true));
	         out.write(out_string);
	         out.close();
	    } catch (IOException e) {
	    }
    }

	public void run()  {
	      try{
	    	    //To set the connection
	            Socket clientSocket = null;
	            try {
	                clientSocket = ServerSharedMemory.serverSocket.accept();
	            }
	            catch (IOException e) {
	                System.err.println("Accept failed.");
	                System.exit(1);
	            }

	            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

	            threadWriteFile("Server started");
	        
	            //Main loop
	            String receive_string;
	            while ((receive_string = in.readLine()) != null) {
	            	
	            	String delims = "[ ]+";
	        	    String[] tokens = (receive_string).split(delims);
	        	    
	        	    //end message
	            	if (tokens[0].equals("End")){    
	        	    	Server.sm.changeState("END");
	            	}else
	            	{
	            		//WRITE FILE
		            	threadWriteFile(receive_string);
		            	System.out.println("RECEIVE: " + receive_string);
		            	Server.sm.changeRequestWrite(receive_string);
	            	}	
	            }
	            
	            out.close();
	            in.close();
	            clientSocket.close();
	            ServerSharedMemory.serverSocket.close();
	            
	   } catch (IOException e) {
	   }
	}
}

