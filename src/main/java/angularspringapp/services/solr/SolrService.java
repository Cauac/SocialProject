package angularspringapp.services.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SolrService {

    public void saveDoc(Map fields) {
        SolrServer server = new HttpSolrServer("http://127.0.0.1:8983/solr/");
        try {

            SolrInputDocument doc = new SolrInputDocument();
            for (Object key : fields.keySet()) {
                doc.addField(key.toString(), fields.get(key));
            }

            server.add(doc);
            server.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getCountContactByUserName(String queryString) {
        SolrServer server = new HttpSolrServer("http://127.0.0.1:8983/solr/");
        SolrQuery query = new SolrQuery();
        query.setQuery("username:*" + queryString + "*");
        query.setFields("_id");
        query.setStart(0);
        try {
            QueryResponse response = server.query(query);
            return response.getResults().getNumFound();
        } catch (SolrServerException e) {
            return 0L;
        }
    }

    public List<String> findByUserName(String queryString, int start, int pageSize) {
        List<String> result = new ArrayList<String>();
        SolrServer server = new HttpSolrServer("http://127.0.0.1:8983/solr/");
        SolrQuery query = new SolrQuery();
        query.setQuery("username:*" + queryString + "*");
        query.setFields("_id");
        query.setStart(start);
        query.setRows(pageSize);
        try {
            QueryResponse response = server.query(query);
            SolrDocumentList results = response.getResults();
            for (int i = 0; i < results.size(); ++i) {
                result.add(results.get(i).get("_id").toString());
            }
        } catch (SolrServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return result;
    }
}
