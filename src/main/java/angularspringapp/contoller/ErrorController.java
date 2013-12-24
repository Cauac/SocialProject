package angularspringapp.contoller;

import angularspringapp.services.jms.Browser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Map;

@Controller
@RequestMapping(value = "/errors")
public class ErrorController {

//    @Autowired
//    Browser browser;

    @RequestMapping(value = "/getErrors.json", method = RequestMethod.GET)
    public
    @ResponseBody
    BasicDBList getErrorsList() {
        BasicDBList result = new BasicDBList();
//        Collection<Map> maps = browser.viewErrors();
//        for (Map m : maps) {
//            result.add(new BasicDBObject(m));
//        }
        return result;
    }
}
