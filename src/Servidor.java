import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Servidor extends Thread {

    private static ArrayList<BufferedWriter> clientes;
    private static ServerSocket server;
    private String nome;
    private Socket con;
    private InputStream in;
    private InputStreamReader inr;
    private BufferedReader bfr;

    /**
     * Método construtor
     * 
     * @param con do tipo Socket
     */
    public Servidor(Socket con) {
        this.con = con;
        try {
            in = con.getInputStream();
            inr = new InputStreamReader(in);
            bfr = new BufferedReader(inr);
        } catch (IOException e) {
            System.err.println("Erro ao criar streams de entrada: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método run
     */
    public void run() {
        try {
            String msg;
            OutputStream ou = this.con.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou);
            BufferedWriter bfw = new BufferedWriter(ouw);
            clientes.add(bfw);
            nome = msg = bfr.readLine();

            while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
                msg = bfr.readLine();
                sendToAll(bfw, msg);
                System.out.println(msg);
            }
        } catch (IOException e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /***
     * Método usado para enviar mensagem para todos os clientes conectados
     * 
     * @param bwSaida do tipo BufferedWriter
     * @param msg     do tipo String
     * @throws IOException
     */
    public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
        BufferedWriter bw;

        for (BufferedWriter cliente : clientes) {
            bw = (BufferedWriter) cliente;
            if (!(bwSaida == bw)) {
                bw.write(nome + " -> " + msg + "\r\n");
                bw.flush();
            }
        }
    }

    /***
     * Método main
     * 
     * @param args
     */
    public static void main(String[] args) {
        clientes = new ArrayList<BufferedWriter>();

        try {
            int porta = Integer.parseInt(JOptionPane.showInputDialog("Porta do servidor:"));
            server = new ServerSocket(porta);
            System.out.println("Servidor ativo na porta: " + porta);

            while (true) {
                System.out.println("Aguardando conexão...");
                Socket con = server.accept();
                System.out.println("Cliente conectado...");
                Thread t = new Servidor(con);
                t.start();
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
