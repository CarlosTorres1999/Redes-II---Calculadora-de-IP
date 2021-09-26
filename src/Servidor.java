import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Errores.IPException;
import Errores.MaskException;

public class Servidor {
	public static void main(String[] args) throws IOException {
		
		Calc calculadora = new Calc();
		DataInputStream dis;
		DataOutputStream dos;
		try {
			ServerSocket servidor = new ServerSocket(Integer.parseInt(args[0]));
			System.out.println("servidor corriendo");
			Socket cliente = servidor.accept();
			
			System.out.println("cliente conectado");
			dis = new DataInputStream(cliente.getInputStream());
			dos = new DataOutputStream(cliente.getOutputStream());
			
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
					dos.writeUTF("ip no valida "+e.getMessage());
					dos.close();
					dis.close();
					cliente.close();
				}catch(MaskException e) {
					dos.writeUTF("mascara no valida "+e.getMessage());
					dos.close();
					dis.close();
					cliente.close();
				}
				
				int[] network = calculadora.calcularNetwork(ip, masc);
				int[] broadcast = calculadora.calcularBroadcast(ip, masc);
				int[] mascaraInvertida = calculadora.invertirMascara(masc);
				
				dos.writeUTF("IP en binario: "+Calc.mostrarOctetosBinario(calculadora.getIp()));
				dos.writeUTF("Masc en Binario: "+Calc.mostrarOctetosBinario(calculadora.getMascara()));
				dos.writeUTF("NetWork Decimal: "+Calc.mostrarOctetosDecimal(network));
				dos.writeUTF("NetWork Binario: "+Calc.mostrarOctetosBinario(network));
				dos.writeUTF("Mascara complementada: "+Calc.mostrarOctetosBinario(mascaraInvertida));
				dos.writeUTF("Broadcast en decimal: "+Calc.mostrarOctetosDecimal(broadcast));
				dos.writeUTF("Broadcast en binario: "+Calc.mostrarOctetosBinario(broadcast));
				
				cliente.close();
				
				
				
 			}else {
				dos.writeUTF("Expresion no valida, vuelva a ejecutar el programa");
				dos.close();
				cliente.close();
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
