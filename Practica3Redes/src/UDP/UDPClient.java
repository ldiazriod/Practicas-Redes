package UDP;

import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCallerOptions;
import com.github.rcaller.rstuff.RCode;

import TCP.RUtils;

import java.lang.Object;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URISyntaxException;

public class UDPClient {
	//Statistics function created
	public static void statistics(Vector<Double> allLatency) throws URISyntaxException, IOException {
		String rScript = RUtils.getStatisticsRScriptCode();
		RCode rCode = RCode.create();
		rCode.addRCode(rScript);
		double[] allLatencyArr = new double[allLatency.size()];
		for(Double elem : allLatency) {
			allLatencyArr[allLatency.indexOf(elem)] = elem;
		}
		rCode.addDoubleArray("input", allLatencyArr);
		rCode.addRCode("result <- statistics(input)");
		RCaller caller = RCaller.create(rCode, RCallerOptions.create());
		caller.runAndReturnResult("result");

		double aux[] = caller.getParser().getAsDoubleArray("result");
		System.out.println("Media: " + aux[0]);
		System.out.println("Mediana: " + aux[1]);
		System.out.println("Desviación típica: " + aux[2]);
		System.out.println("Varianza: " + aux[3]);
	}
	
	public static void main(String[] args) throws URISyntaxException {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] m = args[0].getBytes();
			InetAddress aHost = InetAddress.getLocalHost();
			int serverPort = 6789;
			DatagramPacket request = new DatagramPacket(m, args[0].length(), aHost, serverPort);
			Vector<Double> latency = new Vector<Double>(0,1); //Vector created
			System.out.println("Meta el número de paquetes que quiere intercambiar: "); //Print created
			Scanner sc = new Scanner(System.in); //Scanner created
			int numOfPackages = sc.nextInt(); //For loop created
			for(int i=0; i < numOfPackages; i++) {
				long timePrePackage = System.currentTimeMillis();
				aSocket.send(request);
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);
				long timePostPackage = System.currentTimeMillis();
				latency.addElement((double) timePostPackage - timePrePackage);
			}
			UDPClient.statistics(latency); //Declaration created.
		}catch(SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		}catch(IOException e) {
			System.out.println("IO: " + e.getMessage());
		}finally {
			if(aSocket != null) {
				aSocket.close();
			}
		}
	}
}
