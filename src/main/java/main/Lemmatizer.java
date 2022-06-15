package main;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class Lemmatizer {
    private String titleS;
    private String bodyS;
    private Map<String, Double> rankMap = new HashMap<>();
    private StringBuffer insertQuery = new StringBuffer();

    public Lemmatizer(String titleS, String bodyS) throws IOException {
        this.titleS = titleS;
        this.bodyS = bodyS;
    }

    public Lemmatizer() {
        this.titleS = "";
        this.bodyS = "";
    }
    public Map<String, Double> getRankMap() {
        return rankMap;
    }
    public StringBuffer getInsertQuery() throws IOException {
        lemRank();
        fullTheBuilder(rankMap, insertQuery);
        insertQuery.setLength(insertQuery.length() - 1);
        return insertQuery;
    }

    public static String[] stringSplitter(String s) {
        if (!s.isEmpty()) {
            stringCleaner(s);
        }
        s = s.toLowerCase();
        return s.split(" ");
    }

    public static String stringCleaner(String s) {
        s = s.replaceAll("&nbsp", " ");
        s = s.replaceAll("&copy;'", "");
        s = s.replaceAll("[\\s-\\s]", " ");
        s = s.replaceAll("[^А-Яа-яA-Za-z\\s]", ""); //удаление лишних символов
        s = s.replaceAll("\\s{2,}", " "); // удаление лишних пробелов
        return s;
    }

    public static boolean isNotSpecial(String s) throws IOException {
        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        List<String> wordBaseForms = luceneMorphology.getMorphInfo(s);
        for (String bf : wordBaseForms) {
            if (bf.contains("МЕЖД") || bf.contains("СОЮЗ") || bf.contains("ЧАСТ") || bf.contains("ПРЕДЛ")) {
                return false;
            }
        }
        return true;  //удаление служебных слов
    }

    static public boolean isCyrillic(String s) {
        char[] charArr = s.toCharArray();
        for (char ch: charArr) {
            if(Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.BASIC_LATIN)){
                return false;
            }
        }
        return true;
    }

    public void lemRank() throws IOException {
        Map<String, Double> titleMap = lemCounter(titleS);
        Map<String, Double> bodyMap = lemCounter(bodyS);
        bodyMap.forEach((k, v) -> rankMap.put(k, (0.8 * v)));
        for (Map.Entry<String, Double> entryT : titleMap.entrySet()) {
            String keyT = entryT.getKey();
            Double valueT = entryT.getValue();
            if (rankMap.containsKey(keyT)) {                     //search for words matches
                Double valueNew = rankMap.get(keyT) + valueT;
                rankMap.put(keyT, valueNew);
            } else {
                rankMap.put(keyT, valueT);
            }
        }
    }

    public static Map<String, Double> lemCounter(String s) throws IOException {
        Map<String, Double> countMap = new HashMap<>();
        LuceneMorphology russianLuceneMorphology = new RussianLuceneMorphology();
        LuceneMorphology englishLuceneMorphology = new EnglishLuceneMorphology();
        String[] arr = stringSplitter(s);
        List<String> wordBaseForms;
        for (String st : arr) {
            try {
                if (isCyrillic(st)) {
                    wordBaseForms = russianLuceneMorphology.getNormalForms(st);
                    for (String bf : wordBaseForms) {
                        if (isNotSpecial(bf)) {
                            Double c = countMap.get(bf);
                            if (c == null) {
                                countMap.put(bf, 1.0);
                            } else {
                                countMap.put(bf, ++c);
                            }
                        }
                    }
                } else {
                    wordBaseForms = englishLuceneMorphology.getNormalForms(st);
                for (String bf : wordBaseForms) {
                    Double c = countMap.get(bf);
                    if (c == null) {
                        countMap.put(bf, 1.0);
                    } else {
                        countMap.put(bf, ++c);
                    }
                }
            }
            } catch (WrongCharaterException e) {
                e.printStackTrace();
            }//++
        }
        return countMap;
    }

    public void fullTheBuilder(Map<String, Double> map, StringBuffer insertQuery) {
        map.forEach((k, v) -> insertQuery.append("('" + k + "', '" + v + "'),"));
    }  // Map  лемм и значение встречаемости

    public void queryForIndex(int id) throws SQLException {
        StringBuffer queryForIndex = new StringBuffer();
        rankMap.forEach((k, v) -> queryForIndex.append("('" + id + "', '" + DBConnection.getLemmaId(k) + "', '" + v + "'),"));
        DBConnection.setIndex(queryForIndex);
    }

}
