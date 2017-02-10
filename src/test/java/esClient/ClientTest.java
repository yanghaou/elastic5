package esClient;

import com.yhao.StartApp;
import com.yhao.config.TransportClientBean;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkIndexByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * function
 * Author: yang.hao
 * Date: 2017/2/9
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(StartApp.class)
public class ClientTest {
    @Autowired
    private TransportClientBean transportClientBean;

    String index = "es5-agg";
    String type = "t1";
    String json = "{" +
            "\"user\":\"kimchy\"," +
            "\"postDate\":\"2013-01-30\"," +
            "\"location\":\"beijing\"," +
            "\"message\":\"trying out Elasticsearch\"" +
            "}";

    @Test
    public void agg1(){
        String startTime = "2017-1-1 10:00:00";
        String endTime = "2017-1-1 15:00:00";
        QueryBuilder q1 = QueryBuilders.rangeQuery("insertTime").gte(startTime).lte(endTime);

        //单个聚合
        TermsAggregationBuilder t1 = AggregationBuilders.terms("by_ip").field("ip.keyword").size(Integer.MAX_VALUE);
        TermsAggregationBuilder t2 = AggregationBuilders.terms("by_location").field("location.keyword").size(Integer.MAX_VALUE);

        //复合聚合
        TermsAggregationBuilder t3 = t1.subAggregation(t2);

        SearchResponse searchResponse = transportClientBean.getClient()
                .prepareSearch(index).setTypes(type)
                .setQuery(q1)
                .addAggregation(t3)
                .get();

        Map<String,Aggregation> aggMap = searchResponse.getAggregations().asMap();

        StringTerms ipAgg = (StringTerms) aggMap.get("by_ip");
        /*Iterator<Bucket> ipBuck = ipAgg.getBuckets().iterator();
        while (ipBuck.hasNext()){
            Bucket bucket = ipBuck.next();

            //ip
            String ip = bucket.getKey().toString();
            //记录数
            long ipCount = bucket.getDocCount();

            System.out.println("ip = "+ip+",ipCount = "+ipCount);

            //获取所有子聚合
            Map subAggMap = bucket.getAggregations().asMap();
            StringTerms locationAgg = (StringTerms) subAggMap.get("by_location");
            Iterator<Bucket> locationBuck = locationAgg.getBuckets().iterator();
            while (locationBuck.hasNext()){
                Bucket bucket1 = locationBuck.next();
                //ip
                String location = bucket1.getKey().toString();
                //记录数
                long locationCount = bucket.getDocCount();
                System.out.println("location ="+location+",count = "+locationCount);
            }
        }*/
    }

    @Test
    public void test1(){
        //index
        IndexResponse indexResponse = transportClientBean.getClient()
                .prepareIndex(index,type,"1")
                .setSource(json)
                .get();
        RestStatus status = indexResponse.status();
        System.out.println("index ="+status);
    }

    @Test
    public void test2(){
       //get
        GetResponse response = transportClientBean.getClient()
                .prepareGet(index,type,"1")
                .get();
    }

    @Test
    public void test3(){
       //delete
        DeleteResponse response = transportClientBean.getClient()
                .prepareDelete(index,type,"1")
                .get();
    }

    @Test
    public void test4(){
        //delete by query
        QueryBuilder q1 = QueryBuilders.typeQuery(type);
        QueryBuilder q2 = QueryBuilders.matchQuery("location","beijing");
        BulkIndexByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(transportClientBean.getClient())
                        .filter(QueryBuilders.boolQuery().must(q1).must(q2))
                        .source(index).get();

        long deleted = response.getDeleted();

        //
        /*DeleteByQueryAction.INSTANCE.newRequestBuilder(transportClientBean.getClient())
                .filter(QueryBuilders.matchQuery("gender", "male"))
                .source("persons")
                .execute(new ActionListener<BulkIndexByScrollResponse>() {
                    @Override
                    public void onResponse(BulkIndexByScrollResponse response) {
                        long deleted = response.getDeleted();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        // Handle the exception
                        System.out.println(e);
                    }
                });*/
    }

    String json1 = "{" +
            "\"user\":\"mm\"," +
            "\"postDate\":\"2017-01-30\"" +
            "}";
    @Test
    public void test5(){

        //update
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(index).type(type).id("1").doc(json1);
        try {
            UpdateResponse updateResponse = transportClientBean.getClient().update(updateRequest).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test6(){
        //prepare update
        transportClientBean.getClient().prepareUpdate(index, type, "1")
                .setDoc(json1)
                .get();
    }

    @Test
    public void test7(){
        //upsert
        IndexRequest indexRequest = new IndexRequest(index,type,"1").source(json1);
        UpdateRequest updateRequest = new UpdateRequest(index,type,"1").doc(json).upsert(indexRequest);
        try {
            transportClientBean.getClient().update(updateRequest).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
