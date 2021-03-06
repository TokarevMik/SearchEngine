package main;

import java.sql.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DBConnection {
    private static Connection connection;
    private static String url = "jdbc:mysql://localhost:3306/search_engine";
    private static String user = "root";
    private static String password = "cuafbu5k3a";

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(url, user, password);
                connection.createStatement().execute("DROP TABLE IF EXISTS page");
                connection.createStatement().execute("DROP TABLE IF EXISTS field");
                connection.createStatement().execute("DROP TABLE IF EXISTS lemma");
                connection.createStatement().execute("DROP TABLE IF EXISTS indexes");
                connection.createStatement().execute("CREATE TABLE page(" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "Path TEXT NOT NULL, " +
                        "Code INT NOT NULL, " +
                        "content MEDIUMTEXT NOT NULL)");
                connection.createStatement().execute("CREATE TABLE field(" +
                        "name TINYTEXT NOT NULL," +
                        "selector TINYTEXT NOT NULL," +
                        "weight FLOAT NOT NULL)");
                connection.createStatement().execute("INSERT INTO field(name, selector,weight)" +
                        "VALUES ('title','title','1.0')," +
                        "('body','body','0.8')");
                connection.createStatement().execute("CREATE TABLE lemma(" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "lemma VARCHAR(255) NOT NULL  unique, " +
                        "frequency INT NOT NULL)");
                connection.createStatement().execute("CREATE TABLE indexes(" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "page_id INT NOT NULL, " +
                        "lemma_id INT NOT NULL, " +
                        "rang FLOAT NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void fullTheDb(String path, int code, String content) throws SQLException {
        String sql = "INSERT INTO page (Path, Code, content) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, path);
        preparedStatement.setInt(2, code);
        preparedStatement.setString(3, content);
        preparedStatement.executeUpdate();
    }

    public static void executeMultiInsert(StringBuffer insertQuery) throws SQLException, SQLSyntaxErrorException {
        String sql = "INSERT INTO lemma(lemma, `frequency`) " +
                "VALUES" + insertQuery.toString() + " ON DUPLICATE KEY UPDATE frequency = frequency + 1;";
        DBConnection.getConnection().createStatement().execute(sql);
    }

    public static int getPageId(String s) throws SQLException {
        int id = 0;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT page.id from page WHERE Path='" + s + "';");
        while (resultSet.next()) {
            id = resultSet.getInt(1);
        }
        return id;
    }

    public static void setIndex(StringBuffer builder) throws SQLException {
        String sql = "INSERT INTO indexes(page_id, lemma_id, rang) " +
                "VALUES" + builder.toString();
        sql = sql.substring(0, sql.length() - 1);
        try {
            DBConnection.getConnection().createStatement().execute(sql);
        } catch (SQLSyntaxErrorException se) {
            se.printStackTrace();
        }
    }

    public static int getLemmaId(String lemma) {
        int id = 0;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT lemma.id from lemma WHERE lemma = '" + lemma + "'");
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static Map<Integer, Integer> searchReq(Set<String> stringSet) {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        try {
            StringBuilder sb = new StringBuilder("SELECT frequency, id from lemma WHERE " +
                    "lemma.frequency<(SELECT (count(page.id)/2) from page) AND ");
            for (String s : stringSet) {
                sb.append("lemma = '").append(s).append("' or ");
            }
            sb.setLength(sb.length() - 3);
            sb.append("ORDER BY frequency ASC;"); //?????????????? ???????? ???? ?????????????? ?? ???????????????? ???? ?????????? ?????? ?? ???????????????? ??????????????
            ResultSet resultSet = DBConnection.getConnection().createStatement().executeQuery(sb.toString());
            while (resultSet.next()) {
                map.put(resultSet.getInt("id"), resultSet.getInt("frequency"));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return map;
    }  // ?????????????? id ???????? ?? ?????????????????????????? ???? ???????????????????? ??????????????,
    public static Set<String> getPagesSet(int idLem) throws SQLException {
        Set<String> pages = new HashSet<>();
        Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT page.Path as Ids FROM page \n" +
                "JOIN indexes ON page.id = indexes.page_id \n" +
                "JOIN lemma ON lemma.id = indexes.lemma_id\n" +
                "WHERE lemma.id = " + idLem + ";");
        while (resultSet.next()) {
            pages.add(resultSet.getString(1));
        }
        return pages;
    }
    public static String getContent(String url) throws SQLException {
        String content = "";
        Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT content FROM page " +
                "WHERE Path = '" + url + "';");
        while (resultSet.next()) {
            content = resultSet.getString(1);
        }
        return content;
    }
    public static Connection getConnection2() {
        if (connection == null) {
            try {
                String password = "cuafbu5k3a";
                String user = "root";
                String url = "jdbc:mysql://localhost:3306/search_engine";
                connection = DriverManager.getConnection(url, user, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    } // ???????????????????? ?????? ???????????? ?? ????????????????????

    public static void closeConnection() throws SQLException {
        connection.close();
    }
}
