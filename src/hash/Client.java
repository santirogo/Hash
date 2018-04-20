package hash;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class Client {

    final String HOST = "localhost";

    final int PUERTO = 5000;

    Socket socket;

    ObjectOutputStream oos;
    ObjectInputStream ois;
    
    private static final String ALGO = "AES";
    private static final byte[] keyValue
            = new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};

    public static void main(String[] args) {
        Client client = new Client();
        client.initClient();
    }

    public void initClient() {
        /*ejecuta este metodo para correr el cliente */

        try {

            socket = new Socket(HOST, PUERTO);
            /*conectar a un servidor en localhost con puerto 5000*/

            //creamos el flujo de datos por el que se enviara un mensaje

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            //enviamos el mensaje
            Key key = generateKey();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream("hash.txt");

            String cadena = "";

            BufferedReader br = new BufferedReader(new FileReader("hash.txt"));

            //Lee las líneas de un archivo
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }
                cadena = sb.toString();
            } finally {
                br.close();
            }

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
//            System.out.println("Texto plano: " + cadena);
//            System.out.println("Message Digest: " + hexString.toString());
//            System.out.println("Mensaje a enviar: " + cadena + "!!!" + hexString.toString());
//            
//            String cadenaEnviar = cadena + "!!!" + hexString.toString();
//            
//            oos.writeObject(cadenaEnviar);
            
            //Segundo punto
//            AES aes = new AES();
//            String cifradoAes = aes.encrypt(hexString.toString(), key);
//            System.out.println("Texto Plano: "+cadena);
//            System.out.println("Message Digest: "+hexString.toString());
//            System.out.println("Message Digest Cifrado: "+cifradoAes);
//            System.out.println("Cadena a enviar: "+cadena+"!!!"+cifradoAes);
//
//            String cadenaEnviar = cadena + "!!!" + cifradoAes;
//            
//            oos.writeObject(cadenaEnviar);

            //Tercer punto
            RSA rsa = new RSA();
            //String to int
            
            BigInteger message = new BigInteger(hexString.toString().getBytes());
            //Cifrado
            BigInteger cifrado = rsa.encrypt(message);
            BigInteger descifrado = rsa.decrypt(cifrado);

            System.out.println("Texto Plano: "+cadena);
            System.out.println("Message Digest: "+hexString.toString());
            System.out.println("Message Digest en entero: "+message);
            System.out.println("Message Digest Cifrado: "+cifrado);
            System.out.println("Cadena a enviar: "+cadena+"!!!"+cifrado);
            System.out.println("Descifrado: "+descifrado);
            
            String cadenaEnviar = cadena + "!!!" + cifrado;
            oos.writeObject(cadenaEnviar);
            oos.writeObject(rsa);
            
            
            //cerramos la conexión
            ois.close();
            oos.close();
            socket.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Generate a new encryption key.
     */
    private static Key generateKey() throws Exception {
        return new SecretKeySpec(keyValue, ALGO);
    }
}
