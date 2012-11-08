package client;

import server.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.*;
import java.io.*;

public  class ClientSenderThread extends Thread 
{
	/*
	 * to send messages
	 */
	
	 private static Lock lock = new ReentrantLock(); 
	
	 static void threadMessage(String message) {
	        String threadName =
	            Thread.currentThread().getName();
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
	 
        public void run()  
        {
        	Socket echoSocket = null;
            PrintWriter out = null;
            
            //determine the port
            int port = 0, rq_num = 0 , if_to_server = 0;
            ///
            //String host_name = null;
            String host_ip = null;
            lock.lock();
            if (Client.sm.getTempNum() < ClientSharedMemory.NODE_NUM  )
            {
            	  if (Client.sm.getTempNum() != ClientSharedMemory.nodeId)
                  {
                  	port = ClientNodes.getNode(Client.sm.getTempNum()).getHostPort();
                  	//host_name = ClientNodes.getNode(Client.sm.getTempNum()).getHostName();
                  	///
                  	host_ip = ClientNodes.getNode(Client.sm.getTempNum()).getHostIp();
                  	rq_num = Client.sm.getTempNum();
                  	Client.sm.incrementTempNum();
                  }else if (Client.sm.getTempNum() != ClientSharedMemory.NODE_NUM -1)
                  {
                  	Client.sm.incrementTempNum();
                  	port = ClientNodes.getNode(Client.sm.getTempNum()).getHostPort();
                  	//host_name = ClientNodes.getNode(Client.sm.getTempNum()).getHostName();
                  	///
                  	host_ip = ClientNodes.getNode(Client.sm.getTempNum()).getHostIp();
                  	rq_num = Client.sm.getTempNum();
                  	Client.sm.incrementTempNum();
                  }else    //case:nodeId = NODE_NUM -1
                  {
                	  Client.sm.incrementTempNum();
                	  
                	  if_to_server = 1;
                  	  port = ServerNodes.getNode(Client.sm.getServerTempNum()).getHostPort();
                      //host_name = ClientNodes.getNode(Client.sm.getTempNum()).getHostName();
                      ///
                      host_ip = ServerNodes.getNode(Client.sm.getServerTempNum()).getHostIp();
                      rq_num = Client.sm.getServerTempNum();
                      Client.sm.incrementServerTempNum();
                  }
            }
            else  //server nodes
            {
            	if_to_server = 1;
            	port = ServerNodes.getNode(Client.sm.getServerTempNum()).getHostPort();
              	//host_name = ClientNodes.getNode(Client.sm.getTempNum()).getHostName();
              	///
              	host_ip = ServerNodes.getNode(Client.sm.getServerTempNum()).getHostIp();
              	rq_num = Client.sm.getServerTempNum();
              	Client.sm.incrementServerTempNum();
            }
            
          
            lock.unlock();
          
            while(true)
            {
            	try 
            	{
                      echoSocket = new Socket(host_ip, port);
                      out = new PrintWriter(echoSocket.getOutputStream(), true);
                      break;
                } catch (UnknownHostException e) {
                       System.err.println("Don't know about host: localhost.");
                       //System.exit(1);
                } catch (IOException e) {
                }
            }
                    
        try
        {     
        	 String temp_string,request_string,reply_string,complete_string;
        	 String end_string;
        	 while(true)
    	      {  
        		  //request
    	    	  if(     if_to_server == 0                 &&
    	    			  Client.sm.getState() == "REQUEST" && 
    	    			  Client.sm.getIrValue(rq_num) == 0 &&
    	    			  Client.sm.getIfReplyValue(rq_num) == 0)
    	    	  {
    	    		     //format:Request nodeId request_timestamp
    	    		     int rt = Client.sm.getRtNum();
    	    		  
    	    		     //sent request message
    	    		     request_string = "Request " + rt + " " + ClientSharedMemory.nodeId  +
    	    		    		          " -> " + rq_num + " port: " + port;
    	    		     threadMessage("SENT:" + request_string);
    	    		     System.out.println("SENT:" + request_string);
    	    		     out.println(request_string);
    	    		     
    	    		     Client.sm.incrementMnNum(1);
    	    		     Client.sm.setIrTrue(rq_num);
    	    		     
    	    	  }
    	    	  
    	    	  //complete
    	    	  if(     if_to_server == 0                  &&
    	    			  Client.sm.getState() == "COMPLETE" && 
    	    			  rq_num == 0                        &&
    	    			  Client.sm.getIcNum() == 0)
    	    	  {
                         //RicartAgrawala.sm.incrementMnNum(1);
                         int message_number =  Client.sm.getMnNum();
                         
                         //compute max and min number of exchanged messages
                         int max_num = 0, min_num =Client.sm.getMnNum();
                         for(int j=0 ;j< ClientSharedMemory.CS_NUM ;j++){
                        	 int num = Client.sm.getCsmNum(j);
                        	 if(num > max_num)
                        		 max_num = num;
                        	 if(num < min_num)
                        		 min_num = num;
                         }
                         
    	    		     complete_string = "Complete " + message_number
    	    		    		          + " " + max_num + " " + min_num 
    	    		    		          + " " + "Node: " + ClientSharedMemory.nodeId ;
    	    		     
    	    		     //elapsed time
    	    		     for(int j=0;j<ClientSharedMemory.CS_NUM;j++)
    	    		    	 complete_string += " " + Client.sm.getETime(j);
    	    		    	 
    	    		     threadMessage("SENT:" + complete_string);
    	    		     out.println(complete_string);
    	    		     
    	    		     Client.sm.setIcTrue();
    	    	  }
    	    	  
    	    	  //complete node0
    	    	  if(     if_to_server == 0                  &&
    	    			  Client.sm.getState() == "COMPLETE" && 
    	    			  ClientSharedMemory.nodeId == 0     && 
    	    			  Client.sm.getIzNum() == 0)
    	    	  {
    	    		  Client.sm.incrementCnNum();
    	    		  Client.sm.setIzTrue();
    	    	  }
    	    	  
    	    	  //sent reply
    	    	  if(    if_to_server == 0         &&
    	    			 Client.sm.getState() != "CS" )
    	    	  {
    	    		  temp_string = Client.sm.getRqHeadMember(rq_num);
        	    	  if(temp_string != null)
        	    	  {
        	    		  Client.sm.incrementTtNum(1);  //reply timestamp
        	    		 
        	    		  //format:Reply nodeId reply_timestamp
     	    		      int tt = Client.sm.getTtNum();
        	    		  reply_string = "Reply " + tt + " " + ClientSharedMemory.nodeId +
        	    				         " -> " + rq_num + " port: " + port;
     	    		      out.println(reply_string);
     	    		      
     	    		      Client.sm.setIfReplyFalse(rq_num);
        	    	  }
    	    	  }
    	    	  
    	    	  //sent to servers
    	    	  if(    if_to_server == 1            &&
    	    			 Client.sm.getState() == "CS" &&
    	    			 Client.sm.getIwValue(rq_num) == 0)
    	    	  {
    	    		  String host_name = ClientNodes.getNode(ClientSharedMemory.nodeId).getHostName();
    	    		  temp_string = "< " + ClientSharedMemory.nodeId + " , "
    	    				        + Client.sm.getCsNum() + " , " + host_name + " >";
     	    		  out.println(temp_string);
     	    		  threadMessage("SENT:" + temp_string);
     	    		  System.out.println("SENT:" + temp_string);
     	    		  
     	    		  Client.sm.setIwTrue(rq_num);
    	    	  }
    	    	  
    	    	  //sent end message
    	    	  int mark = 0;
    	    	  if(   Client.sm.getState() == "END"      && 
    	    			ClientSharedMemory.nodeId == 0)
    	    	  {
    	    		  end_string = "End "+ ClientSharedMemory.nodeId +" -> " + rq_num + " port: " + port;
  		              out.println(end_string);
  		              mark = 1;
    	    	  }
    	    	  
    	    	  if(     if_to_server == 0              &&
    	    			  Client.sm.getState() == "END"  && 
    	    			  mark == 1)
    	    	  {
    	    		   try{
    	    		      Thread.sleep(10 * ClientSharedMemory.TIME_UNIT);
        	           } catch(InterruptedException ex) {
        	                 Thread.currentThread().interrupt();
        	           }
    	    		  
    	    		  break;
    	    	  }
    	       }
         
           out.close();
       	   echoSocket.close();
    
        }catch(IOException e) {
        }
        
      }
          
  }
