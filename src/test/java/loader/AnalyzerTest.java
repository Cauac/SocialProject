package loader;

import angularspringapp.analyzer.FlickrAnalyzer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/app-config.xml")
public class AnalyzerTest {

    @Autowired
    FlickrAnalyzer flickrAnalyzer;

    @Test
    public void testAnalyzer() {
//        flickrAnalyzer.analyzeActivity();
        flickrAnalyzer.analyzePhotoActivityByUser("anton");
    }
}
