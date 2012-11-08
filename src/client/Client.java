package client;

import server.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.Date;

public class Client {
	/*
	 * The main class
	 * Create 9 listener threads and 9 sender threads
	 * implement the RicartAgrawala Algorithm with Roucairol Carvalho optimization
	 */
	
	public static ClientSharedMemory sm = new ClientSharedMemory();
	
	static void mainThreadMessage(String message) {
		  String out_string = String.format("MainThread: %s%n",message);
		        
		  try {
		       BufferedWriter out = new BufferedWriter(new FileWriter("client_out"+ClientSharedMemory.nodeId +".txt",true));
		       out.write(out_string);
		       out.close();
		  } catch (IOException e) {
		        }
	}
	
	public static void main(String[] args) {		
		
		if (args.length > 0) {
		    try {
		    	ClientSharedMemory.nodeId = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e) {
		    	mainThreadMessage("Argument must be an integer");
		        System.exit(1);
		    }
	     }

		try {
		     initializeNode(ClientSharedMemory.nodeId);
		} catch (Exception e){
		}
	}
	
	//to initialize node
	public static void initializeNode(int nodeId) throws Exception {
		mainThreadMessage("Client: Initializing system ...\n");
		System.out.println("Client: Initializing system ...\n");
		
		//NODE_NUM's request thread
		for(int i =0 ; i< ClientSharedMemory.NODE_NUM -1 ; i++)
		{
			(new ClientSenderThread()).start();
		}
		
		//request thread to servers
	    for(int i =0 ; i< ServerSharedMemory.NODE_NUM ; i++)
		{
			(new ClientSenderThread()).start();
		}
	
		
		//determine the port
		mainThreadMessage("Client node: " + Integer.toString(ClientSharedMemory.nodeId));
	    int port = ClientNodes.getNode(ClientSharedMemory.nodeId).getHostPort();
	    
	    try {
	    	ClientSharedMemory.serverSocket = new ServerSocket(port);
	    }
	    catch (IOException e) {
	    	mainThreadMessage("Could not listen on port: " + port);
	        System.exit(1);
	    }
		
	    //NODE_NUM's listener thread
	    for(int i =0 ; i< ClientSharedMemory.NODE_NUM-1 ; i++)
	    {
	    	(new ClientListenerThread()).start();
	    }
	    
	    //listener thread for servers
	    for(int i =0 ; i< ServerSharedMemory.NODE_NUM ; i++)
		{
			(new ClientListenerThread()).start();
		}
	
	    mainThreadMessage("INIT");
	    System.out.println("INIT");
		while(true) 
	    {
	    	  if(sm.getState() == "INIT")
	    	  {
	    		    //sleep [10,50] time unit
	    		    Random randomGenerator = new Random();
	                int randomInt = randomGenerator.nextInt(41)+10;
	    		    try {
	        	          Thread.sleep(randomInt * ClientSharedMemory.TIME_UNIT);
	        	     } catch(InterruptedException ex) {
	        	          Thread.currentThread().interrupt();
	        	     }
	    		  
	    		    //to reset environment
	    		    for(int i=0 ; i<ClientSharedMemory.NODE_NUM ; i++){
	    		    	 sm.setIrFalse(i);
	    		    }
	    		    
		    	   ///to reset environment
		    	   for(int i=0 ; i<ServerSharedMemory.NODE_NUM ; i++){
		    		     sm.setIwFalse(i);
		    		}
		    	    Client.sm.resetSRNum();
	    		    
	    		    sm.incrementTtNum(1);  //request timestamp
	    		    sm.changeRtNum(sm.getTtNum());
	    		    sm.changeState("REQUEST") ;
	    		    
	    		    long current_time = new Date().getTime(); 
	    		    sm.setReqTime(current_time);
	    		    
	    	  }
	    	  
	    	  //CS
	    	  int reply_num = 0;
	    	  for(int i=0; i< ClientSharedMemory.NODE_NUM ;i++)
	    		  if(sm.getIfReplyValue(i) == 1)
	    			   reply_num ++;
	    	  if(reply_num == ClientSharedMemory.NODE_NUM -1 &&
	    			  sm.getState() == "REQUEST")
	    	  {
	    		  sm.changeState("CS");
	    		  mainThreadMessage("CS");
	    		  System.out.println("CS");
	    		  
	    	  }
	    	  
	    	  //EXIT CS
	    	  if(Client.sm.getSRNum() == ServerSharedMemory.NODE_NUM &&
	    			  sm.getState() == "CS")
	    	  {
	    		  //set cs message number
	    		  if(sm.getCsmNum(0) == 0){
	    			  sm.setCsmNum(0, sm.getMnNum());
	    			  
	    			  String temp = "CS messages num: " + Integer.toString(sm.getMnNum());
	    			  mainThreadMessage(temp);
	    		  }
	    		  else{
	    			  int last_message_num = 0;
	    			  for(int i = 0;i<sm.getCsNum();i++)
	    				  last_message_num += sm.getCsmNum(i);
	    			  int temp_num = sm.getMnNum() - last_message_num;
	    			  sm.setCsmNum(sm.getCsNum(), temp_num);
	    			  
	    			  String temp = "CS messages num: " + Integer.toString(temp_num);
	    			  mainThreadMessage(temp);
	    		  }
	    		  
	    		  //set elapsed time
	    		  long current_time = new Date().getTime();
	    		  long et = current_time - sm.getReqTime();
	    		  sm.setETime(sm.getCsNum(), et);
	    		  String temp = "CS elapsed time: " + Long.toString(et);
	    		  mainThreadMessage(temp);
	    			
	    		  sm.incrementCsNum();
	    		  sm.changeState("INIT");
	    	  }
	    	  
	    	  if (sm.getState() != "COMPLETE" && sm.getState() !="END" && sm.getCsNum() == ClientSharedMemory.CS_NUM)
	    	  {
	    		  mainThreadMessage("COMPLETE");
	    		  System.out.println("COMPLETE");
	    		  sm.changeState("COMPLETE");
	    		  
	    	  }
	    	  
	    	  //end ,only zero node can satisfy
	    	  if(sm.getState()!= "END" && sm.getCnNum() == ClientSharedMemory.NODE_NUM ){
	    		  //compute the number of message
	    		  mainThreadMessage("The total number of messages are " + sm.getMnNum());
	    		  
	    		  mainThreadMessage("END!!!");
	    		  System.out.println("END!!!"); 
	    		  sm.changeState("END");
	    	  }
	    	  
	    	  if(sm.getState()== "END" )
	    	  {
	    		  try{
	    		      Thread.sleep(10 * ClientSharedMemory.TIME_UNIT);
    	           } catch(InterruptedException ex) {
    	                 Thread.currentThread().interrupt();
    	           }
	    		  
	    		  System.exit(0);
	    	  }
	    }
		
	}
}
