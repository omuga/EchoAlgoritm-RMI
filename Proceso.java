import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;


public class Proceso implements EchoInterface {
	static int numero;
	static String  nodoId;
	static boolean iniciador; // si el nodo es iniciador
	static String ruta_archivo; //Ruta del archivo que contiene el texto a decifrar
	static String ip_server; //Ip del server al cual se conecta el representante
	static boolean comprometido; // si el algoritmo esta comprometido
	static int n; //numero de mensajes recibidos
	static int origen; //id del primer mensaje recibido
	static String textcipher; //Aqui guardaremos el texto cifrado
	static String decipherText; //Aqui guardaremos el texto decifrado
	static EchoInterface stub;
	static boolean representante_establecido = false;
	static boolean mensajeDescifrado_recibido = false;
	static String proceso_representante;
    static ArrayList<String> nodos_vecinos  = new ArrayList<>(); //vecinos del nodo
	public Proceso(){
	}

	public static void main(String[] args) {
		numero = 9;
		nodoId = args[0];
		System.out.println(" ///////////////// Pantalla del Proceso " + nodoId + "//////////////////////");
		String[] parse = args[1].split("-");
		iniciador = Boolean.valueOf(args[2]);
		for (int i = 0; i < parse.length; i++){
			nodos_vecinos.add(parse[i]);
		}
		if (!iniciador){
			comprometido = false;	
		}
		else{
			comprometido = true;
		}
		Proceso obj = new Proceso();
		try {
			stub = (EchoInterface) UnicastRemoteObject.exportObject(obj,0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(nodoId,stub);
			stub.sayHello();
			if (iniciador){
				ruta_archivo = args[3];
				ip_server = args[4];
				//Se envia el mensaje explorar al vecino del nodo iniciador
				for (int i = 0; i < nodos_vecinos.size(); i++){
					Registry reg = LocateRegistry.getRegistry();
					EchoInterface stub2;
					try{
						stub2 = (EchoInterface) reg.lookup(nodos_vecinos.get(i));
						System.out.println("Transmitiendo Mensaje EXPLORADOR a " + nodos_vecinos.get(i));
						stub2.sendExplorer(nodoId);
					} catch (NotBoundException e){
						e.printStackTrace();
					}
				}
				

			}
		} catch (RemoteException e){
			System.out.println("Error ");
			e.printStackTrace();
		}
	}
	
	@Override
	public void  sayHello() throws RemoteException {
		System.out.println("Inicio del Algoritmo para el Proceso " + nodoId);

	}

        @Override
	public void  sendExplorer(String nodoId_origin)  throws RemoteException {
		if (!comprometido){
			System.out.println("Mensaje EXPLORADOR del Proceso " + nodoId_origin + " recibido y establecido como origen");
			comprometido = true;
			n = 0;
			origen = Integer.valueOf(nodoId_origin);
			for (int i = 0; i <nodos_vecinos.size(); i++){
	                        Registry reg = LocateRegistry.getRegistry();
				EchoInterface stub;
				if (!nodos_vecinos.get(i).equals(nodoId_origin)){
					try{
						stub = (EchoInterface) reg.lookup(nodos_vecinos.get(i));
						System.out.println("Enviando un Mensaje Explorador a " + nodos_vecinos.get(i));
						stub.sendExplorer(nodoId);
					} catch (NotBoundException e){
						e.printStackTrace();
					}
				}
			}
		}
		else {
			System.out.println("Mensaje EXPLORADOR del Proceso " + nodoId_origin + " recibido y extinguido");
		}
		n = n + 1;
		if (n == nodos_vecinos.size()){
			System.out.println("Transmitiendo  Mensaje ECO a " + Integer.toString(origen));
			Registry reg = LocateRegistry.getRegistry();
			EchoInterface stub;
			try {
				stub = (EchoInterface) reg.lookup(Integer.toString(origen));
				stub.sendEcho(nodoId);
			} catch (NotBoundException e){
				e.printStackTrace();
			}
		}
	}
	@Override
	public void sendMensaje(String nodoOrigen, String textDecifrado) throws RemoteException{
		if (!mensajeDescifrado_recibido){
			mensajeDescifrado_recibido = true;
			System.out.println("El Mensaje descifrado es: " +  textDecifrado);
        	for (int i = 0; i < nodos_vecinos.size(); i++){
	            Registry reg = LocateRegistry.getRegistry();
	            EchoInterface stub;
				if (!nodos_vecinos.get(i).equals(nodoOrigen)){
	                try{
	                    stub = (EchoInterface) reg.lookup(nodos_vecinos.get(i));
						System.out.println("Enviando mensaje descifrado a " + nodos_vecinos.get(i));
	                    stub.sendMensaje(nodoId , textDecifrado);
	                } catch (NotBoundException e){
	                    e.printStackTrace();
	                }	 	
	            }
			}
		}

	}

	public void ejecutarConsulta() throws Exception {
		// Luego que es elegido, el representante se conecta al servidor
        InterfaceServer instanceServer = (InterfaceServer) Naming.lookup("//"+ip_server+"/PublicKey");
        //Este try/catch es para leer el texto cifrado del archivo que damos como input
        try (
        	BufferedReader brTexto = new BufferedReader(new FileReader(ruta_archivo))) {

			String sCurrentLine;

			while ((sCurrentLine = brTexto.readLine()) != null) {
				textcipher = sCurrentLine;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
        
        String publicKey = instanceServer.getKey("grupo_2") ; //Obtenemos la publicKey del servidor
        System.out.println("Llave obtenida del server: "+ publicKey);
        System.out.println("Texto cifrado del server: "+ textcipher);
        //Finalmente mostramos el texto decifrado, debiese ser un arbol y nuestro grupo
        decipherText = instanceServer.decipher("grupo_2", textcipher, publicKey );
        System.out.println("Texto descifrado del server: "+ decipherText); 
	}
	
	@Override
	public void sendEcho(String nodoId_origen) throws RemoteException {
		System.out.println("Mensaje ECO recibido de " + nodoId_origen);
		n = n + 1;
		if ( n == nodos_vecinos.size()){
			comprometido = false;
			if (iniciador){
				representante_establecido = true;
				System.out.println("FIN DEL ALGORITMO DE ELECCION");
				System.out.println("ID del Proceso Representante: " + nodoId);
				proceso_representante = nodoId;
				Registry reg = LocateRegistry.getRegistry();
				EchoInterface stub;
				for (int i = 0; i < nodos_vecinos.size(); i++){
					try{
						System.out.println("Enviando el Id del Representante a " + nodos_vecinos.get(i));
						stub = (EchoInterface) reg.lookup(nodos_vecinos.get(i));
						stub.establecer_representante(nodoId ,  proceso_representante);
						//Agregamos el enviar el texto cifrado
						try {
							ejecutarConsulta();
						}catch(Exception e){
							e.printStackTrace();
						}
						stub.sendMensaje(nodoId, decipherText);
					} catch (NotBoundException e){
						e.printStackTrace();
					}
				}		
		
			
				
			}
			else {
				Registry reg = LocateRegistry.getRegistry();
				EchoInterface stub;
				try{
					stub = (EchoInterface) reg.lookup(Integer.toString(origen));
					stub.sendEcho(nodoId);
				} catch (NotBoundException e){
					e.printStackTrace();
				} 
			}

		}
	} 
	
	@Override
	public void establecer_representante( String nodoOrigen , String id_representante) throws RemoteException{

		if (!representante_establecido){
			representante_establecido = true;
			proceso_representante = id_representante;
			System.out.println("El Proceso Representante es " +  id_representante);
                        for (int i = 0; i < nodos_vecinos.size(); i++){
                        Registry reg = LocateRegistry.getRegistry();
                        EchoInterface stub;
				if (!nodos_vecinos.get(i).equals(nodoOrigen)){
                        		try{
                                        	stub = (EchoInterface) reg.lookup(nodos_vecinos.get(i));
						System.out.println("Enviando ID del Proceso Representante a " + nodos_vecinos.get(i));
                                      	        stub.establecer_representante(nodoId ,  id_representante);
                               		} catch (NotBoundException e){
                                        	e.printStackTrace();
                                	}	 	
                                }
			}
		}
	}
}
