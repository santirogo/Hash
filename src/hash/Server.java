/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hash;
import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author andres
 */
public class Server {
    final int PUERTO=5000;

    ServerSocket serverSocket;

    Socket socket;

    String mensajeRecibido;
    
    private static final String ALGO = "AES";
    private static final byte[] keyValue
            = new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};

    //SERVIDOR

    public static void main(String[] args) {
        Server server = new Server();
        server.initServer();
    }
    
    public void initServer(){


        try {

            serverSocket = new ServerSocket(PUERTO );/* crea socket servidor que escuchara en puerto 5000*/

            socket=new Socket();

            System.out.println("Esperando una conexión:");

            socket = serverSocket.accept();
            //Inicia el socket, ahora esta esperando una conexión por parte del cliente

            System.out.println("Un cliente se ha conectado.");

            //Canales de entrada y salida de datos
            
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("Confirmando conexion al cliente....");

            //Recepcion de mensaje

            String cadenaRecibida = (String) ois.readObject();
            String cadenaPartida [] = cadenaRecibida.split("!!!");
            
            PrintWriter writer = new PrintWriter("textoPlano.txt");
            writer.println(cadenaPartida[0]);
            writer.close();
            
            Key key = generateKey();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream("textoPlano.txt");
            
            byte[] dataBytes = new byte[1024];

            //Realiza hash en bytes
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            };
            byte[] mdbytes = md.digest();

            //convert the byte to hex format method 2
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < mdbytes.length; i++) {
                hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
            }
            
            //Primer punto
//            System.out.println("Texto plano: "+cadenaPartida[0]);
//            System.out.println("Hash recibido: "+cadenaPartida[1]);
//            System.out.println("Hash calculado: "+hexString.toString());
//            
//            if (cadenaPartida[1].equals(hexString.toString())) {
//                System.out.println("Las cadenas son iguales! :D");
//            }else{
//                System.out.println("Las cadenas no son iguales D:");
//            }

            //Segundo punto
//            AES aes = new AES();
//            String descifradoAes = aes.decrypt(cadenaPartida[1], key);
//            
//            System.out.println("Texto plano: "+cadenaPartida[0]);
//            System.out.println("Hash recibido (cifrado): "+cadenaPartida[1]);
//            System.out.println("Hash descifrado: "+descifradoAes);
//            System.out.println("Hash calculado: "+hexString.toString());
//            
//            if (descifradoAes.equals(hexString.toString())) {
//                System.out.println("Las cadenas son iguales! :D");
//            }else{
//                System.out.println("Las cadenas no son iguales D:");
//            }
            
            
            //Tercer punto
            RSA rsa = (RSA) ois.readObject();
            
            
            BigInteger x = new BigInteger(cadenaPartida[1]);
            System.out.println(x);
            BigInteger descifrado = rsa.decrypt(x);
            System.out.println("pasooo");
            String s = new String(descifrado.toByteArray());
            
            System.out.println("Texto Plano: "+cadenaPartida[0]);
            System.out.println("Message Digest: "+hexString.toString());
            System.out.println("Message Digest recibido: "+cadenaPartida[1]);
            System.out.println("Message Digest descifrado: "+descifrado);
            System.out.println("Message Digest en string: "+s);
            
            
            System.out.println("Cerrando conexión...");

            ois.close();
            oos.close();
            serverSocket.close();//Aqui se cierra la conexión con el cliente

        }catch(Exception e ){
            System.out.println("Error: "+e.getMessage());
        }
    }
    
    /**
     * Generate a new encryption key.
     */
    private static Key generateKey() throws Exception {
        return new SecretKeySpec(keyValue, ALGO);
    }
}