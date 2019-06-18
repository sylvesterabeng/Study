package com.mycompany.app.Training;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class webServer {

    public static void main(String[] args) throws IOException {

        //setting up the socket
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true) {
            try {
                Socket s = serverSocket.accept();  // Wait for a client to connect
                new ClientHandler(s);  // Handle the client in a separate thread
            } catch (Exception x) {
                System.out.println(x);
            }
        }
    }
}
    class ClientHandler extends Thread {
        private Socket socket;  // The accepted socket from the Webserver

        // Start the thread in the constructor
        public ClientHandler(Socket s) {
            socket = s;
            start();
        }

        // Read the HTTP request, respond, and close the connection
        public void run() {
            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream())); // Open connections to the socket
                PrintStream out = new PrintStream(new BufferedOutputStream(
                        socket.getOutputStream()));

                String s = in.readLine();
                System.out.println(s);  // Log the request(GET etc)
                String filename="";
                StringTokenizer st = new StringTokenizer(s);

                // Parse the filename from the GET command
                try { if ((st.hasMoreElements() && st.nextToken().equalsIgnoreCase("GET")))
                    filename = st.nextToken();

                    // Append http://localhost:8080/ with "http://localhost:8080/index.html"
                    if (filename.endsWith("/"))
                        filename += "index.html";

                    // Remove leading / from url
                    // And attempt to read something.html instead of /something.html
                    while (filename.indexOf("/") == 0)
                        filename = filename.substring(1);

                    // Replace "/" with "\" in path for PC-based servers
                    filename = filename.replace('/', File.separator.charAt(0));

                    // Open the file
                    InputStream f = new FileInputStream(filename);

                    // Telling the browser the content type and how to handle it
                    String mimeType = "text/plain";
                    if (filename.endsWith(".html") || filename.endsWith(".htm"))
                        mimeType = "text/html";
                    if (filename.endsWith(".css"))
                        mimeType = "text/css";
                    else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg"))
                        mimeType = "image/jpeg";
                    else if (filename.endsWith(".gif"))
                        mimeType = "image/gif";
                    else if (filename.endsWith(".class"))
                        mimeType = "application/octet-stream";
                    out.print("HTTP/1.0 200 OK\r\n" +
                            "Content-type: " + mimeType + "\r\n\r\n");

                        // Send file contents to client, then close the connection
                        byte[] a = new byte[filename.length()];
                        int n;
                        while ((n = f.read(a)) > 0)
                            out.write(a, 0, n);
                        out.close();

                    } catch (FileNotFoundException x) { //custom 404 not found
                    out.print("HTTP/1.0 404 Not Found\r\n" +
                            "Content-type: " + "text/html text/css" + "\r\n\r\n");

                    byte[] a = new byte[filename.length()];
                    int n;

                    filename = "four.html";
                    InputStream f = new FileInputStream(filename);
                    while ((n = f.read(a)) > 0)
                        out.write(a, 0, n);

                        out.close();
                    }

            } catch (IOException x) {
                System.out.println(x);
            }
        }
    }
