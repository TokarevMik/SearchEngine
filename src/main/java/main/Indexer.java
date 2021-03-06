package main;

import java.util.*;
import java.util.stream.Collectors;

public class Indexer {
    Map<Integer, Integer> mapLemm;

    public Indexer(Map<Integer, Integer> mapLemm) {
        this.mapLemm = mapLemm;
    }
    public Set<String> getPages() {
        List<Integer> listOfId = new LinkedList(mapLemm.keySet()); //список id лем для поиска
        Set<String> pages = new LinkedHashSet<>();
        try {
            pages = DBConnection.getPagesSet(listOfId.get(0)); //список страниц с леммой с самой низкой частотой встреч-и
            for (Integer id : listOfId) {
                pages = setCollection(pages, DBConnection.getPagesSet(id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pages;
    }

    public static Set setCollection(Set<String> count, Set<String> st) {  // удаление страниц не содержащих какую либо из лемм
        Set<Object> items = new HashSet<>(st.stream().filter(count::contains)
                .collect(Collectors.toSet()));
        return items;
    }

}
