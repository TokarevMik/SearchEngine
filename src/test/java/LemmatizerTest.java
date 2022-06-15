import main.Lemmatizer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LemmatizerTest {
    Lemmatizer lemmatizer;
    @Before
    public void createsLemmatizer(){
    lemmatizer = new Lemmatizer();}
    @Test
        public void stringCleanerShoudClean(){  // test stringCleaner
        Assert.assertEquals("тест Тест Test", Lemmatizer.stringCleaner("тест,+*-//^&&*()_11442  Тест - \n @ ..><Test"));
    }
    @Test
    public void stringShouldNotSpecial() throws IOException { //isNotSpecial
        Assert.assertFalse(Lemmatizer.isNotSpecial("на"));
        Assert.assertFalse(Lemmatizer.isNotSpecial("у"));
        Assert.assertFalse(Lemmatizer.isNotSpecial("к"));
    }
    @Test
    public void isCyrillicShoudFalseOnLatin() throws IOException { //isCyrillic
        Assert.assertFalse(Lemmatizer.isCyrillic("PR-менеджер"));
    }
    @Test
    public void lemmataizerShouldCount() throws IOException {
        Map<String, Double> expectationMap = new HashMap<>();
        expectationMap.put("повторный", 1.0); expectationMap.put("некоторый", 1.0); expectationMap.put("появление", 1.0);
        expectationMap.put("постоянно", 1.0); expectationMap.put("постоянный", 1.0); expectationMap.put("некоторые", 1.0);
        expectationMap.put("позволять", 1.0); expectationMap.put("предположить", 1.0); expectationMap.put("северный", 1.0);
        expectationMap.put("район", 1.0); expectationMap.put("кавказ", 1.0); expectationMap.put("осетия", 1.0);
        expectationMap.put("леопард", 2.0); expectationMap.put("обитать", 1.0);
        String checkingString = "повторное появление леопарда в осетии позволяет предположить " +
                "что леопард постоянно обитает в некоторых районах северного кавказа";
        Assert.assertEquals(expectationMap, Lemmatizer.lemCounter(checkingString));
    }

}
