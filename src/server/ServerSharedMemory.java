package server;

import java.net.ServerSocket;

public class ServerSharedMemory {
	/*
	 * All the variable shared by threads
	 * 
	 */
    public static final int NODE_NUM = 3;
    
    public static int nodeId = 0;
    public static ServerSocket serverSocket = null;
    
	private Object lock_t = new Object();      //temp_num
	private Object lock_rw = new Object();     //request_write
	private Object lock_state = new Object();     //state
	
    //mutex
    private int temp_num;
    private String request_write;
    private String state;
    
    public ServerSharedMemory() {
        this.temp_num = 0;
        request_write = null;
        this.state = null;
    }
    
    //temp number
    public void incrementTempNum() {
        synchronized (lock_t) {
        	 temp_num++;
        }
    }  
    
    public int getTempNum() {
        synchronized (lock_t) {
            return temp_num;
        }
    }
    
    //request_write
    public String getRequestWrite() {
        synchronized (lock_rw) {
            return request_write;
        }
    }
    
    public void changeRequestWrite(String s) {
        synchronized (lock_rw) {
        	request_write = s;
        }
    }
    
    //state
    public String getState() {
        synchronized (lock_state) {
            return state;
        }
    }
    
    public void changeState(String s) {
        synchronized (lock_state) {
        	state = s;
        }
    }
    
}
