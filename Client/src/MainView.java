import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;

public class MainView extends JFrame {

    private String id;
    private String ip;
    private int port;

    private Socket socket; // 연결소켓
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;

    Container contentPane=getContentPane();
    Font font = new Font("arian", Font.BOLD, 20);
    JTextArea textArea=new JTextArea();
    JTextField input=new JTextField();
    JScrollPane scrollPane=new JScrollPane();
    public JLabel todo=new JLabel("입력해야 할 거 : ");
    JLabel leftTurn=new JLabel("남은 턴 : 4회");
    JLabel score=new JLabel("점수 : 3점");
    JLabel time=new JLabel("시간 : 4초");
    JButton btnSendButton = new JButton("전송");

    public MainView(String id, String ip, int port)// 생성자
    {
        this.id = id;
        this.ip = ip;
        this.port = port;
        init();
        start();
        network();
    }

    public void network() {
        // 서버에 접속
        try {
            socket = new Socket(ip, port);
            if (socket != null) // socket이 null값이 아닐때 즉! 연결되었을때
            {
                Connection(); // 연결 메소드를 호출
            }
        } catch (UnknownHostException e) {

        } catch (IOException e) {
            textArea.append("소켓 접속 에러!!\n");
        }

    }

    public void Connection() { // 실직 적인 메소드 연결부분

        try { // 스트림 설정
            is = socket.getInputStream();
            dis = new DataInputStream(is);

            os = socket.getOutputStream();
            dos = new DataOutputStream(os);

        } catch (IOException e) {
            textArea.append("스트림 설정 에러!!\n");
        }

        send_Message(id); // 정상적으로 연결되면 나의 id를 전송

        Thread th = new Thread(new Runnable() { // 스레드를 돌려서 서버로부터 메세지를 수신

            @Override
            public void run() {
                while (true) {
                    try {
                        //[0]닉네임, [1]받은 메세지, [2]user turn, [3]turncount, [4]winner
	        //예전에는 이렇게 했다면 이번에는 java json으로 주고 받아서 메시지 객체를 만드는 것이 어떨까?
                        String msg = dis.readUTF(); // 메세지를 수신한다
                        String splitMsg[]=msg.split(":");
                        if(splitMsg.length>2){
                            if(splitMsg[4].equals("1") && splitMsg[3].equals("10")){
                                JOptionPane.showMessageDialog(null, "플레이어1이 이겼습니다.", "결과!", JOptionPane.INFORMATION_MESSAGE);
                                System.exit(0);
                            }
                            else if(splitMsg[4].equals("2") && splitMsg[3].equals("10")){
                                JOptionPane.showMessageDialog(null, "플레이어2가 이겼습니다.", "결과!", JOptionPane.INFORMATION_MESSAGE);
                                System.exit(0);
                            }
                            textArea.append(splitMsg[0]+" : "+splitMsg[1] + "\n");
                            todo.setText("해야할 거 : "+splitMsg[1]);
                            if(splitMsg[2].equals(String.valueOf(LoginView.user.turn))){
                                input.setEditable(true);
                            } else{
                                input.setEditable(false);
                        }
                        }
                        System.out.println(splitMsg[splitMsg.length-1]);
                        //수정함 11/23
                    } catch (IOException e) {
                        textArea.append("게임 종료!!\n");
                        // 서버와 소켓 통신에 문제가 생겼을 경우 소켓을 닫는다
                        try {
                            os.close();
                            is.close();
                            dos.close();
                            dis.close();
                            socket.close();
                            break; // 에러 발생하면 while문 종료
                        } catch (IOException e1) {

                        }

                    }
                } // while문 끝

            }// run메소드 끝
        });

        th.start();

    }

    public void send_Message(String str) { // 서버로 메세지를 보내는 메소드
        try {
            dos.writeUTF(str);
        } catch (IOException e) {
            textArea.append("메세지 송신 에러!!\n");
        }
    }

    public void init() { // 화면구성 메소드
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,860);
        contentPane.setLayout(null);
        contentPane.setSize(1000,800);

        //라벨 추가 시작
        leftTurn.setBounds(120,0,120,40);
        leftTurn.setFont(font);
        contentPane.add(leftTurn);

        score.setBounds(250+210,0,120,40);
        score.setFont(font);
        contentPane.add(score);

        time.setBounds(370+360,0,120,40);
        time.setFont(font);
        contentPane.add(time);

        textArea.setEnabled(false);
        textArea.setFont(font);
        scrollPane.setBounds(0,50,1000,650);
        scrollPane.setViewportView(textArea);
        contentPane.add(scrollPane);

        todo.setFont(font);
        todo.setBounds(0,700,1000,50);
        contentPane.add(todo);


        input.setBounds(0,750,900,50);
        input.setLocation(0,750);
        contentPane.add(input);
        input.addKeyListener(new EnterKeyListener());
        input.requestFocus();
        input.setFocusable(true);

        btnSendButton.setBounds(920,750,70,50);
        btnSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton b = (JButton) e.getSource();
                if (b.getText().equals("전송")){
                    //전송해야할 거
                }
            }
        });
        contentPane.add(btnSendButton);
        setVisible(true);
    }

    public void start() { // 액션이벤트 지정 메소드

        btnSendButton.addActionListener(new Myaction()); // 내부클래스로 액션 리스너를 상속받은 클래스로
        // 지정

    }

    class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
    {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == btnSendButton) // 액션 이벤트가 sendBtn일때
            {

                send_Message(input.getText());
                input.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
                input.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다

            }

        }

    }
    class EnterKeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            // TODO Auto-generated method stub
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                //전송해야할 거
                send_Message(input.getText());
                input.setText("");
                input.requestFocus();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void keyTyped(KeyEvent e) {
            // TODO Auto-generated method stub

        }
    }

}
