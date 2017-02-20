package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Created by 건후 on 2015-11-23.
 */
public class Test extends JFrame{

    Container contentPane=getContentPane();
    Font font = new Font("arian", Font.BOLD, 20);
    JTextArea textArea=new JTextArea();
    JTextField input=new JTextField();
    JScrollPane scrollPane=new JScrollPane();
    JLabel todo=new JLabel("입력해야 할 거 : ");
    JLabel leftTurn=new JLabel("남은 턴 : 4회");
    JLabel score=new JLabel("점수 : 3점");
    JLabel time=new JLabel("시간 : 4초");
    JButton btnSendButton = new JButton("전송");

    public Test(){
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
    public static void main(String[] args) {
        new Test();
    }


    class EnterKeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            // TODO Auto-generated method stub
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                //전송해야할 거
                textArea.append(input.getText()+"\n");
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
