import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class LoginView extends JFrame {
    JLabel title = new JLabel("로그인");

    JTextField id = new JTextField("아이디");
    JPasswordField pw = new JPasswordField("패스워드");

    JButton login_btn = new JButton("로그인");
    JButton join_btn = new JButton("회원가입");

    public static User user;

    static boolean confirm = false;
    static String _id = "";
    static String _pw = "";

    public LoginView() {
        //제목 설정
        super("로그인");

        //레이아웃 설정
        this.setLayout(null);

        //추가
        this.add(title);
        title.setBounds(50, 30, 200, 40);

        this.add(id);
        id.setBounds(50, 80, 200, 40);

        this.add(pw);
        pw.setBounds(50, 140, 200, 40);

        this.add(login_btn);
        login_btn.setBounds(75, 200, 150, 40);
        login_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton b = (JButton) e.getSource();
                if (b.getText().equals("로그인")) {
                    _id=id.getText();
                    _pw=pw.getText();
                    System.out.println("id : " + LoginView._id + ", pw : " + LoginView._pw);
                    try {
                        String myDriver = "org.gjt.mm.mysql.Driver";
                        String myUrl = "jdbc:mysql://localhost/java_chatting";
                        String qid;
                        String qpw;
                        int turn = 0;
                        Class.forName(myDriver);
                        Connection conn = DriverManager.getConnection(myUrl, "root", "apmsetup");
                        String query = "SELECT * FROM user";
                        //String query = "select pw from user where id=" + "'" + LoginView._id.trim() + "'";
                        java.sql.Statement st = conn.createStatement();
                        ResultSet rs = st.executeQuery(query);
                        while (rs.next()) {
                            qid = rs.getString("id");
                            qpw = rs.getString("pw");
                            String name = rs.getString("name");
                            turn = rs.getInt("pid");
                            System.out.format("%s, %s, %s, %s\n", qid, qpw, name, turn);
                            if(qid.equals(_id)&&qpw.equals(_pw)){
                                user = new User(qid, qpw, name, turn);
                                break;
                            }
                        }
                        st.close();
                    } catch (Exception er) {
                        System.err.println("Got an exception! ");
                        System.err.println(er.getMessage());
                    }
                    if(user==null) {
                        JOptionPane.showMessageDialog(null, "일치하는 회원정보가 없습니다.", "경고!", JOptionPane.INFORMATION_MESSAGE);
                        System.out.println("일치하는 회원 정보가 없습니다.");
                        System.exit(0);
                    }else{
                        JOptionPane.showMessageDialog(null, user.name+"님 안녕하세요.", "환영합니다!", JOptionPane.INFORMATION_MESSAGE);
                    }
                    if (user.turn > 1) {
                        user.turn = 2;
                    } else {
                        user.turn = 1;
                    }
                    setVisible(false);
                    MainView view=new MainView(user.id,"127.0.0.1",6464);
                }
            }
        });

        this.add(join_btn);
        join_btn.setBounds(75, 270, 150, 40);


        //프레임 크기 지정
        setSize(300, 400);

        //프레임 보이도록 설정
        setVisible(true);

        //X버튼 눌렀을 때 닫히도록 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}