
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EchoInterface extends Remote {

	public void sayHello() throws RemoteException;
	public void sendExplorer(String nodoId_origin) throws RemoteException;
	public void sendMensaje(String nodoOrigen, String textDecifrado) throws RemoteException;
	public void sendEcho(String nodoId_origin) throws RemoteException;
	public void establecer_representante(String nodoOrigen , String nodo_representante) throws RemoteException;
}
