##Integrantes:
 *  Jose David Tello Villalobos - 201473085-3
 *  Obriel Elias Muga Molina  - 201473005-5

##Estrategia utilizada para resolver el problema:

El problema esta compuesto en dos partes: 
 * En la primera parte se inician los procesos y se estipulan sus procesos vecinos, donde al final se inicia el proceso representante el cual envia un mensaje explorador a sus vecinos (esto, invocando un metodo del proceso vecino, a traves de RMI). Luego estos envian un mensaje explorador a sus vecinos excepto
  al proceso origen que envio el mensaje explorador, por lo cual cada vez que un proceso recibe un mensaje explorador se aumenta en uno el contador "n" que posee cada proceso, y el primer proceso que le envia un mensaje Explorador, se establece como "origen".

  Cuando el contador "n" de un proceso es igual a su numero de vecinos este envia un mensaje de ECO a su proceso origen. Finalmente, cuando llegan todos los mensajes de ECO al proceso representante se termina el algoritmo de elección. 

 * En la segunda parte, el proceso representante informa a los demas procesos que ahora es el representante. Además muestra por consola la llave publica que obtiene del servidor en 10.10.2.214, el texto cifrado que obtiene del archivo y el texto descifrado que obtuvo del servidor. Finalmente envía , de la misma forma que informo que es representante, el mensaje descifrado que se obtuvo del servidor externo (donde los demás procesos replican el mensaje descifrado).  


##Explicación del funcionamiento de nuestra solución:

Primero se inician los procesos 2,3 y 4 (cada uno en un consola diferente en la máquina 10.10.2.228):
```
$ java Proceso 2 1-3-4 false
$ java Proceso 3 2 false
$ java Proceso 4 2 false
```
Luego se inicia el proceso 1 representante y se señala el nombre del archivo a descifrar y la ip del servidor:
```
$ java Proceso 1 2 true cifrado_grupo_2.txt 10.10.2.214
```
Así (el Proceso) 1 envía un mensaje de exploracion a 2, donde 2 establece 1 como Proceso origen y envía un mensaje explorador a 3, el cual le responde con un ECO ya que solo tiene de vecino a 2. Lo mismo pasa con el proceso 4 (se envía un mensaje explorador y se recibe un ECO). Al haber recibido todos los mensajes ECO, 2 envía un ECO a 1 lo cual termina el Algoritmo de Elección. 

Luego el Proceso 1 informa cual es la ID del proceso Representante a los demás procesos, obtiene la Llave publica del servidor (la cuál se usa para descifrar el texto cifrado en un archivo .txt) y luego de haber mostrado por consola cuál era el texto descifrado, se envía un mensaje con el texto descifrado a los demás procesos de la forma más eficiente. 
