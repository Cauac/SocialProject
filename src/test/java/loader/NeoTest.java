package loader;

import angularspringapp.services.neo.NeoService;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/app-config.xml")
public class NeoTest {

    @Autowired
    NeoService neoService;

    @Test
    public void main() throws URISyntaxException, IOException, ParseException {
//        for (String id : neoService.findPossibleFriends("109746769@N03")) {
//            System.out.println(id);
//        }
    }

}
