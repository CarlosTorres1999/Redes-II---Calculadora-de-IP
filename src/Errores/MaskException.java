package Errores;

public class MaskException extends Error{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mensaje;
	
	public MaskException(String mensaje) {
		this.mensaje = mensaje;
	}
	
	
	@Override
	public String getMessage() {
		return this.mensaje;
	}
	
}
