import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Cliente {
	
	public static void main(String[] args) {
		final String HOST = args[0];
		final int PUERTO = Integer.parseInt(args[1]);
		Scanner entrada = new Scanner(System.in);
		
		try {
			Socket skCliente = new Socket(HOST, PUERTO);
			
			InputStream is = skCliente.getInputStream();
			DataInputStream dis = new DataInputStream( is );
			
			System.out.println("Ingrese la IP/MASCARA :D");
			String ipMasc = entrada.nextLine();
			
			OutputStream os = skCliente.getOutputStream();
			
			DataOutputStream dos = new DataOutputStream( os );
			
			
			dos.writeUTF( ipMasc );
			
			
			
			while(skCliente.isConnected()) {
				String flujoEntrada = dis.readUTF();
				System.out.println(flujoEntrada);
			}
			
			
			
			
			
		} catch (UnknownHostException e) {
			System.err.println("Error, Host Desconocido "+e.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
