package client;

public enum ClientNodes {
	/*
	 * Contain the information about nodes
	 */
	
	 //Ten nodes
	 n0 ("net01.utdallas.edu", "127.0.0.1", 8010),
	 n1 ("net02.utdallas.edu", "127.0.0.1", 8001),
	 n2 ("net03.utdallas.edu", "127.0.0.1", 8002),
	 n3 ("net04.utdallas.edu", "127.0.0.1", 8003),
	 n4 ("net05.utdallas.edu", "127.0.0.1", 8004),
	 n5 ("net06.utdallas.edu", "127.0.0.1", 8005),
	 n6 ("net07.utdallas.edu", "127.0.0.1", 8006),
	 n7 ("net08.utdallas.edu", "127.0.0.1", 8007),
	 n8 ("net09.utdallas.edu", "127.0.0.1", 8008),
	 n9 ("net10.utdallas.edu", "127.0.0.1", 8009);
	 
	 private final String hostName;
	 private final String hostIp;
	 private final int hostPort;
	 
	 ClientNodes(String hostName, String hostIp, int hostPort){
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
     public static ClientNodes getNode(int nodeId){
			 return ClientNodes.values()[nodeId];

	 }
}
