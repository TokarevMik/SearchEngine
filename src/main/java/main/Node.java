package main;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Node {
    private static Map<String, String> titleMap = new HashMap<>();
    private String url;
    private String path; // адрес для бд
    private static String domain;
    private String title;
    String bodyText = "";
    private Integer statusCode;
    private String contentOfPage = "";

    public Node(String url) {
        this.url = url;
    }

    public Node(String url, String domain) {
        this.url = url;
        Node.domain = domain;
    }

    private Collection<Node> nodes = new HashSet<>();

    public Collection<Node> getChildren() {
        return nodes;
    }

    public void getParseNode() {
        try {
            Thread.sleep(200);
            Connection.Response response = Jsoup.connect(url).timeout(0).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com").maxBodySize(0).execute();
            statusCode = response.statusCode(); //статус ответа
            Document doc = response.parse();
            contentOfPage = doc.html();//содержимое страницы
            title = doc.title();
            Element content = doc.body();
            bodyText = content.text(); //
            Elements links = content.getElementsByTag("a");
            if (url.equals(domain)) {
                path = domain;
            } else {
                if (url.contains(domain)) {
                    path = url.replace(domain, "");
                    titleMap.put(path, title);
                }
            }
            for (Element link : links) {
                String linkHref = link.attr("href");
                Pattern pattern = Pattern.compile(domain + "/[\\w,\\D]+(.html)?/$");
                Matcher matcher = pattern.matcher(linkHref);


//                Pattern pattern2 = Pattern.compile("^(/[\\w,-,_])+(.html)?/$");
                Pattern pattern2 = Pattern.compile("^(/[\\w,-,_]+)+(.html)?$");
                Matcher matcher2 = pattern2.matcher(linkHref);

                Pattern pattern3 = Pattern.compile("^/[\\w,-,_]+(.html)?/$");
                Matcher matcher3 = pattern3.matcher(linkHref);
                if (matcher.matches()) {
                    nodes.add(new Node(linkHref));   // добавление дочерней ссылки в список , но уровнем не ниже 1 от родительской
                }
                if (matcher2.matches()) {
                    linkHref = domain.concat(linkHref);
                    nodes.add(new Node(linkHref)); // ссылка типа "/****/"
                }
                if (matcher3.matches()) {
                    linkHref = domain.concat(linkHref);
                    nodes.add(new Node(linkHref)); // ссылка типа "/****/"
                }
            }
        } catch (HttpStatusException se) {
            path = url.replace(domain, "");
            contentOfPage = "";
            statusCode = se.getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTitle(String url) {
        return titleMap.get(url);
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getBodyText() {
        return bodyText;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getContentOfPage() {
        return contentOfPage;
    }

    public String getUrl() {
        return url;
    }
}
