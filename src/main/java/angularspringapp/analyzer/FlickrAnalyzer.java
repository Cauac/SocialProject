package angularspringapp.analyzer;

import angularspringapp.dao.MongoDAO;
import angularspringapp.dao.MongoFlickrDAO;
import angularspringapp.services.UserService;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;

public class FlickrAnalyzer extends MongoDAO {

    @Autowired
    UserService userService;

    public void analyzeActivity() {
        DBCollection collection = database.getCollection(MongoFlickrDAO.ACTIVITY_COLLECTION);
        String map = "function(){" +
                "var owner=this._id.split('_')[0];" +
                "for(var i in this.comment){" +
                "var stat=[];" +
                "stat.push({key:this.comment[i][0],fav:0,com:1});" +
                "emit(owner,{stat:stat});}" +
                "for(var i in this.fave){" +
                "for(var j in this.fave[i]){" +
                "var k=this.fave[i][j];" +
                "var stat=[];" +
                "stat.push({key:k,fav:1,com:0});" +
                "emit(owner,{stat:stat});}}}";
        String reduce = "function(key, values){" +
                "var stat=[], k, i, c,f;" +
                "for (i=0; i < values.length; i++) {" +
                "k = values[i].stat[0].key;" +
                "c = values[i].stat[0].com;" +
                "f = values[i].stat[0].fav;" +
                "var find=false;" +
                "for (j in stat){" +
                "if(stat[j].key==k){" +
                "stat[j].com=stat[j].com+c;" +
                "stat[j].fav=stat[j].fav+f;" +
                "find=true;}}" +
                "if(!find){stat.push({key:k,fav:f,com:c});}}" +
                "return {stat:stat};}}";
        String finalize = "function(key,reduced){" +
                "var values=reduced.stat;" +
                "for (var i=0; i < values.length; i++) {" +
                "for (var j=1; j < values.length; j++) {" +
                "if((values[j].com+values[j].fav)>(values[j-1].com+values[j-1].fav)){" +
                "var t=values[j]; values[j]=values[j-1]; values[j-1]=t;}" +
                "}} reduced.stat=values.slice(0, 5); return reduced;}";

        MapReduceCommand cmd = new MapReduceCommand(collection, map, reduce, "analyzed_activity", MapReduceCommand.OutputType.REPLACE, null);
        cmd.setFinalize(finalize);
        collection.mapReduce(cmd);
    }

    private Iterable<DBObject> photoActivityAggregation(String userId) {
        DBCollection collection = database.getCollection(MongoFlickrDAO.PHOTO_COMMENT_COLLECTION);

        DBObject fields = new BasicDBObject();
        BasicDBList favAndCommnet = new BasicDBList();
        favAndCommnet.add("$favesCount");
        favAndCommnet.add("$commentsCount");
        fields.put("sum", new BasicDBObject("$add", favAndCommnet));
        fields.put("favesCount", 1);
        fields.put("commentsCount", 1);
        fields.put("itemOwner", 1);
        DBObject project = new BasicDBObject("$project", fields);
        DBObject match = new BasicDBObject("$match", new BasicDBObject("itemOwner", userId));
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("sum", -1));
        AggregationOutput output = collection.aggregate(project, match, sort);
        return output.results();
    }

    public void analyzePhotoActivityByUser(String user) {
        String flickrUserId = userService.getFlickrUserId(user);
        DBObject persitenResult = new BasicDBObject("_id", user);
        int i = 1;
        BasicDBList photoList = new BasicDBList();
        for (DBObject photo : photoActivityAggregation(flickrUserId)) {
            if (i > 10) {
                break;
            }
            photoList.add(photo);
            i++;
        }
        persitenResult.put("photos", photoList);
        DBCollection collection = database.getCollection("analyzed_photo_activity");
        collection.update(new BasicDBObject("_id", user), persitenResult, true, false);
    }


}
