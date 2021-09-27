
import java.util.function.BinaryOperator;

import Errores.IPException;
import Errores.MaskException;

public class Calc {
	private BinaryOperator<Integer> network;
	private BinaryOperator<Integer> broadcast;
	private int[] ip;
	private int[] mascara;
	
	/**
	 * Constructor por defecto, ya viene con los operadores pre-establecido
	 */
	public Calc() {
		this.network = (a, b) -> (a & b);
		this.broadcast = (a, b) -> (a | b );
	}
	
	/**
	 * Constructor con argumentos, ya viene con los operadores pre-establecidos
	 * @param ip recibe en forma de arreglo de octetos en forma decimal
	 * @param mascara recibe en forma de arreglo de octetos en forma decimal
	 */
	public Calc(int[] ip, int[] mascara) {
		this.ip = ip;
		this.mascara = mascara;
		this.network = (a, b) -> (a & b);
		this.broadcast = (a, b) -> (a | b);
	}

	//Getters y Setter
	public int[] getIp() {
		return ip;
	}

	public void setIp(int[] ip) {
		this.ip = ip;
	}

	public int[] getMascara() {
		return mascara;
	}

	public void setMascara(int[] mascara) {
		this.mascara = mascara;
	}


	//Funciones publicas, para usar con el socket

	/**
	 * Evalua la expresion recibida
	 * @param expresion exprecion de formato ["oct1.oct2.oct3.oct4", "masc"]
	 * @return true si valida, false si no
	 */
	public static boolean evaluarExpresion(String[] expresion) {
		return expresion.length == 2 ? true : false;
	}


	
	/**
	 * Valida la mascara que se pasa por argumento, verifica los valores, y los octetos.
	 * @param mascara la mascara en formato String
	 * @return si es mascara Corta, la convierte en mascara larga, si es larga, la retorna
	 */
	public int[] validarMascara(String mascara) {
		if (esMascaraCorta(mascara)) {
			int masc = Integer.parseInt(mascara);
			if (evaluarMasc(masc)) {
				return this.crearMascaraLarga(masc);
			} else {
				throw new MaskException(
						"Mascara No valida, vuelva a ejecutar el programa y pase como corresponda masc < 32 or masc >= 0");
			}
		} else {
			int[] resultado;
			String[] masc = mascara.split("\\.");
			if (evaluarMasc(masc)) {
				resultado = new int[4];
				
				for (int i = 0; i < masc.length; i++) {
					
					int value = Integer.parseInt(masc[i]);
					String octetoBinario = completarOcteto(value);
					
					for(int j = 0; j < octetoBinario.length(); j++) {
					
						if(octetoBinario.charAt(j) == '0') {
							
							for(int k = 1; k < octetoBinario.length(); k++) {
								if(octetoBinario.charAt(k) == '1') {
									throw new MaskException("Error al cargar mascara larga, en el octeto numero: "+(i + 1 ));
								}
							}
						}
					}
					
					if (evaluarOcteto(value)) {
						resultado[i] = value;
					} else {
						throw new MaskException("Mascara no valida, error en el octeto nro :" + (i + 1)
								+ "Ingrese nuevamente, octeto <= 255, octeto >= 0");
					}
				}
			} else {
				throw new MaskException("Mascara no valida, argumentos no valido, ejecute nuevamente el programa");
			}

			return resultado;
		}
	}

	/**
	 * Evaluar la Ip, valida la cantidad de octetos, el valor mínimo y máximo
	 * @param ip el arreglo de octetos en String
	 * @return la ip como arreglo de enteros
	 */
	public int[] evaluarIp(String[] ip) {
		if (evaluarMasc(ip)) {
			int[] resultado = new int[4];

			for (int i = 0; i < ip.length; i++) {
				int value = Integer.parseInt(ip[i]);

				if (evaluarOcteto(value)) {
					resultado[i] = value;
				} else {
					throw new IPException("Ip no valido, vuelva a cargar");
				}

			}
			return resultado;
		} else {
			throw new IPException("IP no valido");
		}

	}
	
	/**
	 * Retorna una cadena mostrando los octetos en firmato decimal
	 * @param arg arreglo de octetos
	 * @return cadena de octetos unida con un punto "."
	 */
	public static String mostrarOctetosDecimal(int[] arg){
		String resultado = "";
		for(int i = 0; i < arg.length; i++) {
			resultado +=arg[i]+".";
			
		}
		return resultado.substring(0, resultado.length() - 1);
	}
	
	/**
	 * Muestra en forma de cadena los octetos en formato Binario
	 * @param arg arreglo de octetos en decimal, OJO. EN DECIMAL
	 * @return cadena de octetos en forma de binarios, unido por un punto "."
	 */
	public static String mostrarOctetosBinario(int[] arg){
		String resultado = "";
		for(int i = 0; i < arg.length; i++) {
			resultado +=completarOcteto(arg[i])+".";
			
		}
		return resultado.substring(0, resultado.length() - 1);
	}
	
	/**
	 * Calcula el BroadCast
	 * @param arg1 el Ip en forma de arreglos de octetos [oct1, oct2, oct3, cot4]
	 * @param arg2 la mascara larga [oct1, oct2, oct3, oct4]
	 * @return el broadcast
	 */
	public int[] calcularBroadcast(int[] arg1, int[] arg2) {
		int[] resultado = new int[4];
		int[] mascaraInvertida = invertirMascara(arg2);
		for (int i = 0; i < resultado.length; i++) {
			
			resultado[i] = broadcast.apply(arg1[i], mascaraInvertida[i]);
		}

		return resultado;
	}
	
	/**
	 * Funcion para invertir mascara
	 * @param mascara mascara en decimal
	 * @return mascara en complemento a1
	 */
	public int[] invertirMascara(int[] mascara) {
		int[] resultadoBinario = new int[4];
		int[] resultado = new int[4];
		for(int i = 0; i < mascara.length; i++) {
			String octetoViejoStr = completarOcteto(mascara[i]);
			
			int octetoNuevo = Integer.parseInt(
					octetoViejoStr
					.replace("0", "2")
					.replace("1", "0")
					.replace("2","1"));
			resultadoBinario[i] = octetoNuevo;
		}
		resultado = binarioADecimal(resultadoBinario);
		
		return resultado;
	}
	
	/**
	 * Calcula el Network
	 * @param arg1 la ip en forma de arreglo de octetos [oct1, oct2, oct3, oct4]
	 * @param arg2 la mascara larga [oct1, oct2, oct3, oct4]
	 * @return el network
	 */
	public int[] calcularNetwork(int[] arg1, int[] arg2) {
		int[] resultado = new int[4];
		for (int i = 0; i < resultado.length; i++) {
			resultado[i] = network.apply(arg1[i], arg2[i]);
		}

		return resultado;
	}


	/**
	 * Convierte un arraglo de octetos binarios a decimal
	 * @param arg Arreglo de octetos en formato binario
	 * @return el arreglo de octetos en forma decimal
	 */
	public int[] binarioADecimal(int arg[]) {
		int resultado[] = new int[4];
		for (int i = 0; i < arg.length; i++) {
			resultado[i] = Integer.parseUnsignedInt(Integer.toString(arg[i]), 2);
		}
		return resultado;
	}
	

	

	public static void main(String[] args) {
		
		Calc calculadora = new Calc();
		String[] expresion = args[0].split("/");

		if (!evaluarExpresion(expresion)) {
			throw new Error("Argumento no valido");
		} else {
			
			String ipStr = expresion[0];
			String mascStr = expresion[1];
			int [] mascara = calculadora.validarMascara(mascStr);
			int [] ip = calculadora.evaluarIp(ipStr.split("\\."));
			
			
			calculadora.setIp(ip);
			calculadora.setMascara(mascara);
			
			int[] resultadoNetworkDecimal = calculadora.calcularNetwork(calculadora.getIp(), calculadora.getMascara());
			int[] resultadoBroadcastDecimal = calculadora.calcularBroadcast(calculadora.getIp(), calculadora.getMascara());
			
			
			
			
			System.out.println("Valor Ingresado: "+args[0]);
			System.out.println("Ip: "+mostrarOctetosDecimal(calculadora.getIp()));
			System.out.println("IP en binario "+mostrarOctetosBinario(calculadora.getIp()));
			System.out.println("Mascara: "+mostrarOctetosDecimal(calculadora.getMascara()));
			System.out.println("Mascara en Binario:  "+mostrarOctetosBinario(calculadora.getMascara()));
			System.out.println("Network en Decimal "+mostrarOctetosDecimal(resultadoNetworkDecimal));
			System.out.println("Network en Binario "+mostrarOctetosBinario(resultadoNetworkDecimal));
			System.out.println("Complemento de mascara: "+mostrarOctetosBinario(calculadora.invertirMascara(calculadora.getMascara())));
			System.out.println("Broadcast en Decimal "+mostrarOctetosDecimal(resultadoBroadcastDecimal));
			System.out.println("Broadcast en Binario "+mostrarOctetosBinario(resultadoBroadcastDecimal));
			System.out.println("fin del programa");
		}
	}
	
	
	// Metodos privados, Funciones que se implementan en otras que son publicas
	private static boolean evaluarOcteto(int octeto) {
		return octeto < 0 || octeto > 255 ? false : true;
	}

	private static boolean evaluarMasc(int mascara) {
		return mascara < 0 || mascara > 32 ? false : true;
	}

	private static boolean evaluarMasc(String[] mascara) {
		return mascara.length == 4 ? true : false;
	}
	
	
	private static boolean esMascaraCorta(String mascara) {
		return mascara.contains(".") ? false : true;
	}
	

	
	private int[] crearMascaraLarga(int mascaraCorta) {
		
		int[] resultado = new int[4];
		
		String buff = "";
		
		for(int i = 1; i <= 32; i++ ) {
			if(i <= mascaraCorta) {
				buff += 1;
			}
			else {
				buff += 0;
			}
		}
		
		

		for(int i = 0; i < 4; i++ ) {
			resultado[i] = Integer.parseInt(buff.substring(i*8, (i + 1)*8));
			
		}
		
		return binarioADecimal(resultado);
	}
	private static String completarOcteto(int octeto) {
		String resultado = "";
		String octetoBinario = Integer.toBinaryString(octeto);
		String buff = "";
		int longMaxOcteto = 8;
		int longOcteto = octetoBinario.length();
		
		for(int i = 0; i < longMaxOcteto - longOcteto ; i++ ) {
			buff += 0;
		}
		resultado = buff + octetoBinario;
		return resultado;
	}

	
}
