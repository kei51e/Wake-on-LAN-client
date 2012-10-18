import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;

/**
 * A simple implementation of Wake-on-LAN client.
 * 
 * usage: java WOL [IP address] [Subnet mask] [mac address] ex) java
 * WakeUpCaller 192.168.1.5 255.255.255.0 11:11:11:11:11:11
 * 
 */
public class WOL {
  public static void main(String[] args) throws Exception {
    if (args.length != 3) {
      System.err.println("usage: java WOL [IP address] [Subnet mask] [mac address]");
      System.err.println("ex) java WOL 192.168.1.5 255.255.255.0 11:11:11:11:11:11");
      return;
    }

    String hostname = args[0];
    String subnet = args[1];
    String macAddr = args[2];

    // mac address
    byte[] mac = new byte[6];

    // Parse mac address
    StringTokenizer st = new StringTokenizer(macAddr, " :-");
    if (st.countTokens() != 6) {
      System.err.println("Invalid mac address");
      return;
    }

    for (int i = 0; i < 6; i++) 
      mac[i] = (byte) Integer.parseInt(st.nextToken(), 16);

    // Calculate broadcast address
    byte[] hostAddr = InetAddress.getByName(hostname).getAddress();
    byte[] subnetAddr = InetAddress.getByName(subnet).getAddress();
    byte[] addr = new byte[4];
    for (int i = 0; i < 4; i++) 
      addr[i] = (byte) (hostAddr[i] | (~subnetAddr[i]));

    InetAddress address = InetAddress.getByAddress(addr);

    // Create data
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    // Add 6 bytes with 0xff.
    for (int i = 0; i < 6; i++) 
      buf.write((byte) 0xff);
    
    // Repeat target mac address 16 times.
    for (int i = 0; i < 16; i++) 
      buf.write(mac);

    buf.flush();
    buf.close();
    byte[] data = buf.toByteArray();

    // Create UDP packet.
    DatagramPacket packet = new DatagramPacket(data, data.length, address, 9);
    // Create UDP socket.
    DatagramSocket socket = new DatagramSocket();
    // Send packet.
    socket.send(packet);
    // Close socket.
    socket.close();
  }
}
