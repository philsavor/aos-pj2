package server;

public enum ServerNodes {
	/*
	 * Contain the information about nodes
	 */
	
	 //Ten nodes
	 n0 ("net20.utdallas.edu", "127.0.0.1", 7000),
	 n1 ("net21.utdallas.edu", "127.0.0.1", 7001),
	 n2 ("net22.utdallas.edu", "127.0.0.1", 7002),
	 n3 ("net23.utdallas.edu", "127.0.0.1", 7003),
	 n4 ("net24.utdallas.edu", "127.0.0.1", 7004),
	 n5 ("net25.utdallas.edu", "127.0.0.1", 7005);
	 
	 private final String hostName;
	 private final String hostIp;
	 private final int hostPort;
	 
	 ServerNodes(String hostName, String hostIp, int hostPort){
		 this.hostName = hostName;
		 this.hostIp = hostIp;
		 this.hostPort = hostPort;
	 }
	 
	 public String getHostName() {
			return hostName;
	 }

	 public String getHostIp() {
			return hostIp;
	 }

	 public int getHostPort() {
			return hostPort;
	 }

	 //To get nodes' infomation
     public static ServerNodes getNode(int nodeId){
			 return ServerNodes.values()[nodeId];

	 }
}
