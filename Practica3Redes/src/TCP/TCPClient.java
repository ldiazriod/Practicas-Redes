package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.Scanner;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCallerOptions;
import com.github.rcaller.rstuff.RCode;

public class TCPClient {
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
		Socket s = null;
		try {
			int serverPort = 7896;
			Vector<Double> allLatency = new Vector<Double>(0,1);
			Scanner sc = new Scanner(System.in);
			int numOfPackages = sc.nextInt();
			for(int i=0; i < numOfPackages; i++) {
				long timePrePackage = System.currentTimeMillis();
				s = new Socket("localhost", serverPort);
				DataInputStream in = new DataInputStream(s.getInputStream());
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				out.writeUTF(args[0]);
				String data = in.readUTF();
				System.out.println("Received: " + data);
				long timePostPackage = System.currentTimeMillis();
				allLatency.addElement((double) timePostPackage - timePrePackage);
			}
			TCPClient.statistics(allLatency);
		}catch(UnknownHostException e) {
			System.out.println("Socket: " + e.getMessage());
		}catch(EOFException e) {
			System.out.println("EOF: " + e.getMessage());
		}catch(IOException e) {
			System.out.println("readline: " + e.getMessage());
		}finally {
			if(s != null) {
				try {
					s.close();
				}catch(IOException e) {
					System.out.println("close: " + e.getMessage());
				}
			}
		}
	}
}
