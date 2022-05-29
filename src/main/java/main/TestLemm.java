package main;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import java.io.IOException;

import java.util.*;

public class TestLemm {
    public static void main(String[] args) throws IOException {
        String str = "Повторное появление леопарда в Осетии позволяет предположить, " +
                "что леопард постоянно обитает в некоторых районах Северного Кавказа.";
        str = Lemmatizer.stringCleaner(str);
        str = str.trim();
        str = str.toLowerCase(Locale.ROOT);
        String[] arr = Lemmatizer.stringSplitter(str);
        Map<String, Integer> mapLemm = getLemmSetMethod(arr);
        mapLemm.forEach((k, v) -> System.out.println(k + " - " + v));
    }

    public static Map<String, Integer> getLemmSetMethod(String[] arr) throws IOException {
        Map<String, Integer> lemmMap = new HashMap<>();
        LuceneMorphology russianLuceneMorphology = new RussianLuceneMorphology();
        LuceneMorphology englishLuceneMorphology = new EnglishLuceneMorphology();
        List<String> wordBaseForms;
        for (String st : arr) {
            if (Lemmatizer.isCyrillic(st)) {
                wordBaseForms = russianLuceneMorphology.getNormalForms(st);
                for (String bf : wordBaseForms) {
                    if (Lemmatizer.isNotSpecial(bf)) {
                        if (!lemmMap.containsKey(bf)) {
                            lemmMap.put(bf, 1);
                        } else {
                            int count = lemmMap.get(bf);
                            lemmMap.put(bf, ++count);
                        }
                    }
                }
            } else {
                wordBaseForms = englishLuceneMorphology.getNormalForms(st);
            }
        }
        return lemmMap;
    }
}
