package server;

import client.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;

public class Server {
	/*
	 * The main class
	 * Create 9 listener threads and 9 sender threads
	 * implement the RicartAgrawala Algorithm with Roucairol Carvalho optimization
	 */
	
	public static ServerSharedMemory sm = new ServerSharedMemory();
	
	static void mainThreadMessage(String message) {
		  String out_string = String.format("MainThread: %s%n",message);
		        
		  try {
		       BufferedWriter out = new BufferedWriter(new FileWriter("server_out"+ServerSharedMemory.nodeId +".txt",true));
		       out.write(out_string);
		       out.close();
		  } catch (IOException e) {
		        }
	}
	
	public static void main(String[] args) {		
		
		if (args.length > 0) {
		    try {
		    	ServerSharedMemory.nodeId = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e) {
		    	mainThreadMessage("Argument must be an integer");
		        System.exit(1);
		    }
	     }

		try {
		     initializeNode(ServerSharedMemory.nodeId);
		} catch (Exception e){
		}
	}
	
	//to initialize node
	public static void initializeNode(int nodeId) throws Exception {
		//mainThreadMessage("Server: Initializing system ...\n");
		System.out.println("Server: Initializing system ...\n");
		
		//sender threads for all client nodes
		for(int i =0 ; i< ClientSharedMemory.NODE_NUM ; i++)
		{
			(new ServerSenderThread()).start();
		}
	
		
		//determine the port
		//mainThreadMessage("Server node: " + Integer.toString(ServerSharedMemory.nodeId));
	    int port = ServerNodes.getNode(ServerSharedMemory.nodeId).getHostPort();
	    
	    try {
	    	ServerSharedMemory.serverSocket = new ServerSocket(port);
	    }
	    catch (IOException e) {
	    	mainThreadMessage("Could not listen on port: " + port);
	        System.exit(1);
	    }
		
	    //listener threads for all client nodes
	    for(int i =0 ; i< ClientSharedMemory.NODE_NUM ; i++)
	    {
	    	(new ServerListenerThread()).start();
	    }
	
	    //mainThreadMessage("INIT");
	    System.out.println("INIT");
		while(true) 
	    {
			 if(sm.getState()== "END" )
	    	  {
	    		  try
	    		  {
	    		      Thread.sleep(10 * ClientSharedMemory.TIME_UNIT);
   	              } catch(InterruptedException ex) {
   	                 Thread.currentThread().interrupt();
   	              }
	    		  
	    		  System.exit(0);
	    	  }
	    }
		
	}
}
