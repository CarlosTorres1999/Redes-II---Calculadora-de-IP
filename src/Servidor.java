import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Errores.IPException;
import Errores.MaskException;

public class Servidor {
	public static void main(String[] args) throws IOException {
		
		Calc calculadora = new Calc();
		OutputStream out;
		DataInputStream dis;
		DataOutputStream dos;
		try {
			
			ServerSocket servidor = new ServerSocket(Integer.parseInt(args[0]));
			System.out.println("servidor corriendo");
			Socket cliente = servidor.accept();
			out = cliente.getOutputStream();
			System.out.println("cliente conectado");
			dis = new DataInputStream(cliente.getInputStream());
			dos = new DataOutputStream(out);
			
			String[] expresion = dis.readUTF().split("/");
			
			
			if(Calc.evaluarExpresion(expresion)) {
				
				int[] ip = new int[4];
				int[] masc = new int[4];
				
				try {
					ip  = calculadora.evaluarIp(expresion[0].split("\\."));
					masc = calculadora.validarMascara(expresion[1]);
					
					calculadora.setIp(ip);
					calculadora.setMascara(masc);
				}catch(IPException e) {
					dos.writeBytes(e.getMessage());
					dos.close();
					dis.close();
					cliente.close();
					System.exit(-1);
				}catch(MaskException e) {
					dos.writeBytes(e.getMessage());
					dos.close();
					dis.close();
					cliente.close();
					System.exit(-1);
				}
				
				int[] network = calculadora.calcularNetwork(ip, masc);
				int[] broadcast = calculadora.calcularBroadcast(ip, masc);
				int[] mascaraInvertida = calculadora.invertirMascara(masc);
				
				
				
				dos.writeBytes("IP en binario: "+Calc.mostrarOctetosBinario(calculadora.getIp()));
				dos.writeBytes("\nMasc en Binario: "+Calc.mostrarOctetosBinario(calculadora.getMascara()));
				dos.writeBytes("\nNetWork Decimal: "+Calc.mostrarOctetosDecimal(network));
				dos.writeBytes("\nNetWork Binario: "+Calc.mostrarOctetosBinario(network));
				dos.writeBytes("\nMascara complementada: "+Calc.mostrarOctetosBinario(mascaraInvertida));
				dos.writeBytes("\nBroadcast en decimal: "+Calc.mostrarOctetosDecimal(broadcast));
				dos.writeBytes("\nBroadcast en binario: "+Calc.mostrarOctetosBinario(broadcast));
				
				dos.close();
				cliente.close();
				
				
				
 			}else {
				dos.writeBytes("Expresion no valida, vuelva a ejecutar el programa");
				dos.close();
				cliente.close();
				System.exit(-1);
			}
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
