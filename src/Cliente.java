import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;
public class Cliente extends JFrame implements ActionListener, KeyListener{
    private static final long serialVersionUID = 1L;

    /* Variáveis para montar ou recebe coisas da interface */
    private JTextArea texto;
    private JTextField txtMsg;
    private JButton btnEnviar;
    private JButton btnSair;
    private JLabel lblHistorico;
    private JLabel lblMsg;
    private JPanel pnlContent;
    private JTextField txtIP;
    private JTextField txtPorta;
    private JTextField txtNome;
    /* --- */

    private Socket socket;
    private OutputStream ou ;
    private Writer ouw;
    private BufferedWriter bfw;

    /* Método construtor */
    public Cliente() throws IOException{

        /* Aqui mostrará a tela inicial onde o cliente deve colocar as informações como nome, ip do servidor e porta para prosseguir */
        JLabel lblMessage = new JLabel("Verificar!");
        txtIP = new JTextField("127.0.0.1");
        txtPorta = new JTextField("12345");
        txtNome = new JTextField("Cliente");
        Object[] texts = {lblMessage, txtIP, txtPorta, txtNome };
        JOptionPane.showMessageDialog(null, texts);
        /* --- */

        /* Aqui é para construir a janela do chat, iremos dividir em partes para ficar mais fácil a compreensão */
        pnlContent = new JPanel(); /* Declaração do painel */

            /* Histórico do chat */
            lblHistorico = new JLabel("Histórico"); /* Título mostrando que é o histórico do chat */
            texto = new JTextArea(10,20); /* Área onde ficará o texto, definindo largura e altura */
            texto.setEditable(false); /* Não deixa o texto ser editável, afinal, é só para ver as mensagens que enviou e recebeu */
            texto.setBackground(new Color(240,240,240)); /* Cor de fundo da área */
            JScrollPane scroll = new JScrollPane(texto); /* Adicionar um scroll no histórico */
            texto.setLineWrap(true); /* Quebra de linha */
            texto.setBorder(BorderFactory.createEtchedBorder(Color.GREEN, Color.GREEN)); /* Borda verde */

            /* Janela de enviar mensagem */
            lblMsg = new JLabel("Mensagem"); /* Título mostrando que é para enviar mensagens */
            txtMsg = new JTextField(20); /* Definindo largura, a mesma que a do histórico para ficar padronizado */
            txtMsg.addKeyListener(this); /* Perceber quando for clicado */
            txtMsg.setBorder(BorderFactory.createEtchedBorder(Color.GREEN, Color.GREEN)); /* Borda verde */
            
            /* Botão sair */
            btnSair = new JButton("Sair"); /* Texto do botão, onde estará escrito sair */
            btnSair.setToolTipText("Sair do Chat"); /* Tooltip que aparecerá caso passe o mouse por cima */
            btnSair.addActionListener(this); /* Perceber quando for clicado */

            /* Botão enviar */
            btnEnviar = new JButton("Enviar"); /* Texto do botão, onde estará escrito enviar */
            btnEnviar.setToolTipText("Enviar Mensagem"); /* Tooltip que aparecerá caso passe o mouse por cima */
            btnEnviar.addActionListener(this);            
            btnEnviar.addKeyListener(this); /* Perceber quando for clicado */
            
            /* Define o content da tela */
            pnlContent.add(lblHistorico);
            pnlContent.add(scroll);
            pnlContent.add(lblMsg);
            pnlContent.add(txtMsg);
            pnlContent.add(btnSair);
            pnlContent.add(btnEnviar);
            pnlContent.setBackground(Color.LIGHT_GRAY);

            /* Define propriedades da tela */
            setTitle(txtNome.getText()); /* Título igual ao nome do usuário */
            setContentPane(pnlContent); /* Conteúdo */
            setLocationRelativeTo(null); /* Não define posição fixa na tela */
            setSize(250,350); /* Tamanho fixo em pixels */
            setResizable(false); /* Não permite mudar o tamanho da tela */
            setVisible(true); /* Aparecer assim que terminar de gerar */
            setDefaultCloseOperation(EXIT_ON_CLOSE); /* Finalizar caso clicar no X */
        /* --- */
    }

    /* Método para conectar-se ao servidor */
    public void conectar() throws IOException{

        socket = new Socket(txtIP.getText(),Integer.parseInt(txtPorta.getText()));
        ou = socket.getOutputStream();
        ouw = new OutputStreamWriter(ou);
        bfw = new BufferedWriter(ouw);
        bfw.write(txtNome.getText()+"\r\n");
        bfw.flush();
    }

    /* Método para enviar mensagem ao servidor */
    public void enviarMensagem(String msg) throws IOException{

        if(msg.equals("Sair")){
            bfw.write("Desconectado \r\n");
            texto.append("Desconectado \r\n");
            ou.close();
            ouw.close();
            bfw.close();
            socket.close();
            System.exit(0); /* Tentamos implementar fechar a tela automaticamente caso escreva sair ou clique no botão sair, mas não deu certo */
        }else{
            bfw.write(msg+"\r\n");
            texto.append( txtNome.getText() + " diz -> " + txtMsg.getText()+"\r\n");
        }
        bfw.flush();
        txtMsg.setText("");
    }

    /* Método para receber mensagens e coloca-las no histórico */
    public void escutar() throws IOException{

        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(inr);
        String msg = "";

        while(!"Sair".equalsIgnoreCase(msg))

            if(bfr.ready()){
                msg = bfr.readLine();
                texto.append(msg+"\r\n");
            }
    }

    /* Método para definir o que será feito quando os botões forem pressionados */
    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            if(e.getActionCommand().equals(btnEnviar.getActionCommand())) /* Botão de enviar */
                enviarMensagem(txtMsg.getText());
            else
            if(e.getActionCommand().equals(btnSair.getActionCommand())) /* Botão de sair */
            {
                enviarMensagem("Sair");
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /* Método para definir o que será feito quando apertar enter enquanto na caixa de texto de mensagem */
    @Override
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            try {
                enviarMensagem(txtMsg.getText());
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    /* Método main */
    public static void main(String []args) throws IOException{

        Cliente app = new Cliente();
        app.conectar();
        app.escutar();
    }
}
