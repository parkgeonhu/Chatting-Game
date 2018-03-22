import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;


public class Server extends JFrame {

    private Container contentPane=getContentPane();
    private JTextField textField; // 사용할 PORT번호 입력
    private JButton Start; // 서버를 실행시킨 버튼
    JTextArea textArea; // 클라이언트 및 서버 메시지 출력

    private ServerSocket socket; //서버소켓
    private Socket soc; // 연결소켓
    private int Port; // 포트번호

    private Vector vc = new Vector(); // 연결된 사용자를 저장할 벡터

    private int turn=1;
    public int state=3;
    int turnCount=0;
    int winner=0;

    //game
    String question;
    String answer;
    int scorePlayer1=0;
    int scorePlayer2=0;

    public static void main(String[] args){
        Server frame = new Server();
        frame.setVisible(true);
    }

    public Server() {
        init();
    }

    private void init() { // GUI를 구성하는 메소드

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320,420);
        setTitle("서버 프로그램");
        contentPane.setLayout(null);

        JScrollPane js = new JScrollPane();

        textArea = new JTextArea();
        textArea.setColumns(20);
        textArea.setRows(5);
        js.setBounds(0, 0, 264, 254);
        contentPane.add(js);
        js.setViewportView(textArea);

        textField = new JTextField();
        textField.setBounds(98, 264, 154, 37);
        contentPane.add(textField);
        textField.setColumns(10);

        JLabel lblNewLabel = new JLabel("Port Number");
        lblNewLabel.setBounds(12, 264, 98, 37);
        contentPane.add(lblNewLabel);

        Start = new JButton("서버 실행");
        Start.setBounds(0, 325, 264, 37);
        contentPane.add(Start);
        textArea.setEditable(false); // textArea를 사용자가 수정 못하게끔 막는다.

        Start.addActionListener(new ActionListener() { // 익명 클래스로 클릭 이벤트 처리

            @Override
            public void actionPerformed(ActionEvent e) {

                if (textField.getText().equals("") || textField.getText().length()==0)// textField에 값이 들어있지 않을때
                {
                    textField.setText("포트번호를 입력해주세요");
                    textField.requestFocus(); // 포커스를 다시 textField에 넣어준다
                }

                else {
                    try{
                        Port = Integer.parseInt(textField.getText()); // 숫자로 입력하지 않으면 에러 발생 포트를 열수 없다.
                        server_start(); // 사용자가 제대로된 포트번호를 넣었을때 서버 실행을위헤 메소드 호출
                    }catch(Exception er){
                        //사용자가 숫자로 입력하지 않았을시에는 재입력을 요구한다
                        textField.setText("숫자로 입력해주세요");
                        textField.requestFocus(); // 포커스를 다시 textField에 넣어준다
                    }
                }// else 문 끝
            }
        }); // 클릭 이벤트 익명클래스 끝
    }

    private void server_start() {
        try {
            socket = new ServerSocket(Port); // 서버가 포트 여는부분
            Start.setText("서버실행중");
            Start.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
            textField.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다

            if(socket!=null) // socket 이 정상적으로 열렸을때
            {
                Connection();
            }

        } catch (IOException e) {
            textArea.append("소켓이 이미 사용중입니다...\n");

        }

    }

    private void Connection() {
        Thread th = new Thread(new Runnable() { // 사용자 접속을 받을 스레드
            @Override
            public void run() {
                while (true) { // 사용자 접속을 계속해서 받기 위해 while문
                    try {
                        textArea.append("사용자 접속 대기중...\n");
                        soc = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
                        textArea.append("사용자 접속!!\n");

                        UserInfo user = new UserInfo(soc, vc); // 연결된 소켓 정보는 금방 사라지므로, user 클래스 형태로 객체 생성
                        // 매개변수로 현재 연결된 소켓과, 벡터를 담아둔다
                        vc.add(user); // 해당 벡터에 사용자 객체를 추가
                        user.start(); // 만든 객체의 스레드 실행
                    } catch (IOException e) {
                        textArea.append("!!!! accept 에러 발생... !!!!\n");
                    }
                }

            }
        });

        th.start();

    }


    class UserInfo extends Thread {
        private InputStream is;
        private OutputStream os;
        private DataInputStream dis;
        private DataOutputStream dos;

        private Socket user_socket;
        private Vector user_vc;

        private String Nickname = "";


        public UserInfo(Socket soc, Vector vc) // 생성자메소드
        {
            // 매개변수로 넘어온 자료 저장
            this.user_socket = soc;
            this.user_vc = vc;

            User_network();

        }

        public void User_network() {
            try {
                is = user_socket.getInputStream();
                dis = new DataInputStream(is);
                os = user_socket.getOutputStream();
                dos = new DataOutputStream(os);

                Nickname = dis.readUTF(); // 사용자의 닉네임 받는부분
                textArea.append("접속자 ID " +Nickname+"\n");

                //send_Message("정상 접속 되었습니다"); // 연결된 사용자에게 정상접속을 알림

            } catch (Exception e) {
                textArea.append("스트림 셋팅 에러\n");
            }

        }

        public void InMessage(String str) {
            //state=1 질문, 2=답, 3=예외
            textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
            //턴 한 번 바꿔주기
            if(state==3 && turn==1){
                System.out.println("플레이어1가 질문을 합니다.");
                question=str;
                state=2;
                turn=2;
            }else if(state==2&&turn==2){
                System.out.println("플레이어2가 답을 합니다.");
                answer=str;
                if(question.equals(answer)){
                    scorePlayer2++;
                }else{
                    System.out.println("틀렸습니다.");
                }
                state=1;
                turn=2;
            }else if(state==1&&turn==2){
                System.out.println("플레이어2가 질문을 합니다.");
                question=str;
                state=2;
                turn=1;
            }else if(state==2 && turn==1){
                System.out.println("플레이어1이 답을 합니다.");
                answer=str;
                if(question.equals(answer)){
                    scorePlayer1++;
                }else{
                    System.out.println("틀렸습니다.");
                }
                state=1;
                turn=1;
            }
            else if(state==1&&turn==1){
                System.out.println("플레이어1가 질문을 합니다.");
                question=str;
                state=2;
                turn=2;
            }else{
                System.out.println("엥!");
                state=3;
                turn=1;
            }
            turnCount++;
            if(turnCount==6){
                if(scorePlayer1>scorePlayer2){
                    JOptionPane.showMessageDialog(null, "플레이어1이 이겼습니다.", "결과!", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                } else if(scorePlayer1<scorePlayer2){
                    JOptionPane.showMessageDialog(null, "플레이어2가 이겼습니다.", "결과!", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }else{
                    JOptionPane.showMessageDialog(null, "무승부", "결과!", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            }
            System.out.println("player1 score : "+scorePlayer1+", player2 score : "+scorePlayer2);
            System.out.println("turnCount : "+turnCount);
            // 사용자 메세지 처리
            broad_cast(str);
        }

        public void broad_cast(String str) {
            if(turnCount==10){

            }
            for (int i = 0; i < user_vc.size(); i++) {
                UserInfo imsi = (UserInfo) user_vc.elementAt(i);
                imsi.send_Message(Nickname+":"+str+":"+turn+":"+turnCount+":"+winner);
            }
        }

        public void send_Message(String str) {
            try {
                dos.writeUTF(str);
            }
            catch (IOException e) {
                textArea.append("메시지 송신 에러 발생\n");
            }
        }

        public void run() // 스레드 정의
        {
            while (true) {
                try {

                    // 사용자에게 받는 메세지
                    String msg = dis.readUTF();
                    InMessage(msg);

                }
                catch (IOException e)
                {
                    try {
                        dos.close();
                        dis.close();
                        user_socket.close();
                        vc.removeElement( this ); // 에러가난 현재 객체를 벡터에서 지운다
                        textArea.append(vc.size() +" : 현재 벡터에 담겨진 사용자 수\n");
                        textArea.append("사용자 접속 끊어짐 자원 반납\n");
                        break;

                    } catch (Exception ee) {

                    }// catch문 끝
                }// 바깥 catch문끝

            }
        }// run메소드 끝

    } // 내부 userinfo클래스끝


}
