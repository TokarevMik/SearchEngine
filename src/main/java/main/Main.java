package main;
import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        DBConnection.getConnection();
        String url = "http://www.playback.ru/" ;
//        String url = "https://skillbox.ru";
        String domain = url;
        Node root = new Node(url,domain);
        ParseNode task = new ParseNode(root);
        task.invoke();
        DBConnection.closeConnection();
    }

}
