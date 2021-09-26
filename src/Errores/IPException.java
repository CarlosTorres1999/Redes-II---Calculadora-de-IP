package Errores;

public class IPException extends Error{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String mensaje;

	public IPException(String mensaje) {
		this.mensaje = mensaje;
	}
	
	@Override
	public String getMessage() {
		return this.mensaje;
	}
}
