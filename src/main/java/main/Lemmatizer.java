package main;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;

public class Lemmatizer {
    public static String[] stringSplitter(String s) {
        if (!s.isEmpty()) {
            stringCleaner(s);
        }
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

    static boolean isCyrillic(String s) {
        return s.chars()
                .mapToObj(Character.UnicodeBlock::of)
                .anyMatch(b -> b.equals(Character.UnicodeBlock.CYRILLIC));
    }

}
