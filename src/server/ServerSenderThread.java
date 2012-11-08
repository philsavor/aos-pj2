package server;

import client.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.*;
import java.io.*;

public  class ServerSenderThread extends Thread 
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
	            BufferedWriter out = new BufferedWriter(new FileWriter("server_out"+ServerSharedMemory.nodeId +".txt",true));
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
            int port = 0, rq_num = 0;
            ///
            //String host_name = null
            String host_ip = null;
            lock.lock();
            
            port = ClientNodes.getNode(Server.sm.getTempNum()).getHostPort();
            //host_name = Nodes.getNode(RicartAgrawala.sm.getTempNum()).getHostName();
            ///
            host_ip = ClientNodes.getNode(Server.sm.getTempNum()).getHostIp();
            rq_num = Server.sm.getTempNum();
            Server.sm.incrementTempNum();
            
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
        	     int i = 0;
        	     String temp_string;
        	     while(true)
    	         {   
        	    	 temp_string = Server.sm.getRequestWrite();
        	    	 if(temp_string != null)
        	    	 {
        	    		   String delims = "[ ]+";
                		   String[] tokens = (temp_string).split(delims);
                		   
                		   int num = Integer.parseInt(tokens[1]);
                		   //find the right thread to send reply
                		   if(num == rq_num)
                		   {
                			   temp_string = "Server " + ServerSharedMemory.nodeId +
      	    				         " -> " + rq_num + " port: " + port;
   	    		               out.println(temp_string);
   	    		               System.out.println("SENT: " + temp_string);
   	    		               
   	    		               Server.sm.changeRequestWrite(null);
                		   }
                		   
        	    	 }
        		     if ( i ==1 ) break;
    	         }
         
                 out.close();
       	         echoSocket.close();
    
            }catch(IOException e) {
            }
        
       }
          
  }
