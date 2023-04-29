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

     /* Método construtor, parâmetro Socket para poder pegar as informações do mesmo */
    public Servidor(Socket con) {
        this.con = con;
        try {
            in = con.getInputStream(); /* Pegar InputStream */
            inr = new InputStreamReader(in); /* Pegar InputStreamReader */
            bfr = new BufferedReader(inr); /* Pegar BufferedRead */
            /* Isso para poder enviar as mensagens para os clientes no futuro */
        } catch (IOException e) {
            System.err.println("Erro ao criar streams de entrada: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            /* --- */
            String msg; 
            OutputStream ou = this.con.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou); /* O autor da mensagem */
            BufferedWriter bfw = new BufferedWriter(ouw);
            clientes.add(bfw); /* Adicionando o escritor da mensagem a lista de clientes */
            nome = msg = bfr.readLine();
            /* Essa parte é a que recebe as mensagens dos clientes */
            /* --- */

            while (!"Sair".equalsIgnoreCase(msg) && msg != null) { /* Se a mensagem NãO for igual a Sair ou nula, então percorre o while */
                msg = bfr.readLine();
                sendToAll(bfw, msg); /* Método criado para compartilhar a mensagens com os outros clientes conectados */
                System.out.println(msg); /* Printar no terminal a mensagem */
            }
        } catch (IOException e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
            e.printStackTrace();
        } finally { /* Caso o método run vá finalizar, então... */
            try {
                con.close(); /* Feche o Socket, ou seja, a conexão */
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

     /* Método usado para enviar mensagem para todos os clientes conectados */
    public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
        BufferedWriter bw;

        for (BufferedWriter cliente : clientes) { /* Para todo cliente conectado na lista de clientes... */
            bw = (BufferedWriter) cliente;
            if (!(bwSaida == bw)) { /* Se o cliente selecionado da lista não for igual ao autor da mensagem, então replique a mensagem para ele */
                bw.write(nome + " -> " + msg + "\r\n"); /* Define o texto a ser enviado */
                bw.flush(); /* Envia o texto */
            }
        }
    }

     /* Método main */
    public static void main(String[] args) {
        clientes = new ArrayList<BufferedWriter>(); /* Lista de clientes conectados */

        try {
            /* Aqui abre uma janela de input para colocar a porta que o servidor irá utilizar */
            int porta = Integer.parseInt(JOptionPane.showInputDialog("Porta do servidor:"));
            server = new ServerSocket(porta);
            System.out.println("Servidor ativo na porta: " + porta);

            while (true) {
                System.out.println("Aguardando conexão...");
                Socket con = server.accept();
                System.out.println("Cliente conectado...");
                Thread t = new Servidor(con);
                t.start(); /* Basicamente, é uma thread por cliente */
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
