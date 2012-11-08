package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public  class ClientListenerThread extends Thread {
	/*
	 * To receive messages,including Request,Reply,Complete messages.
	 */
	
	private static Lock lock = new ReentrantLock(); 
	
    // Display a message, preceded by the name of the current thread
	// And write to file.
    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
	    String out_string = String.format("%s: %s%n",
	                                     threadName,
	                                      message);
	    try {
	         BufferedWriter out = new BufferedWriter(new FileWriter("client_out"+ClientSharedMemory.nodeId +".txt",true));
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
	                clientSocket = ClientSharedMemory.serverSocket.accept();
	            }
	            catch (IOException e) {
	                System.err.println("Accept failed.");
	                System.exit(1);
	            }

	            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

	            threadMessage("Server started");
	        
	            //Main loop
	            String receive_string;
	            while ((receive_string = in.readLine()) != null) {
	            	
	            	String delims = "[ ]+";
	        	    String[] tokens = (receive_string).split(delims);
	        	    
	        	    if (tokens[0].equals("Request")){        //Request message
	        	    	 lock.lock();
	        	    	 int temp_tt = Integer.parseInt(tokens[1]);
	        	    	 if (temp_tt > Client.sm.getTtNum())
	        	    		 Client.sm.changeTtNum(temp_tt+1);
	        	    	 else 
	        	    		 Client.sm.incrementTtNum(1);
	        	    	 lock.unlock();
	        	    	 
	            	     Client.sm.addRequest(receive_string);
	        	    }else if (tokens[0].equals("Reply")){     //Reply message
	        	    	 lock.lock();
	        	    	 int temp_tt = Integer.parseInt(tokens[1]);
	        	    	 if (temp_tt > Client.sm.getTtNum())
	        	    		 Client.sm.changeTtNum(temp_tt+1);
	        	    	 else 
	        	    		 Client.sm.incrementTtNum(1);
	        	    	 lock.unlock();
	        	    	
	        	    	 //to compute the number of messages 
	        	    	 Client.sm.incrementMnNum(1);
	        	    	 
	        	    	 threadMessage("RECEIVE:" + receive_string);
    	    		     System.out.println("RECEIVE:" + receive_string);
	        	    	 Client.sm.setIfReplyTrue(Integer.parseInt(tokens[2]));
	        	    }else if (tokens[0].equals("Complete")){   //Complete message
	        	    	 threadMessage("RECEIVE:" + receive_string);
	        	    	 Client.sm.incrementCnNum();
	        	    	 
	        	    	 //compute the number of messages
	        	    	 Client.sm.incrementMnNum(Integer.parseInt(tokens[2]));
	        	    }else if (tokens[0].equals("End")){    //End message
	        	    	Client.sm.changeState("END");
	        	    }else if (tokens[0].equals("Server")){    //End message
	        	    	threadMessage("RECEIVE:" + receive_string);
	        	    	System.out.println("RECEIVE:" + receive_string);
	        	    	Client.sm.incrementSRNum();
	        	    }
	            }
	            
	            out.close();
	            in.close();
	            clientSocket.close();
	            ClientSharedMemory.serverSocket.close();
	            
	   } catch (IOException e) {
	   }
	}
}

