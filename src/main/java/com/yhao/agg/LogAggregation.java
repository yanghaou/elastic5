package com.yhao.agg;

import com.yhao.config.TransportClientBean;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * function
 * Author: yang.hao
 * Date: 2017/2/10
 */
@Component
public class LogAggregation {
    @Autowired
    private TransportClientBean transportClientBean;

    String index = "es5-agg";
    String type = "t1";

    public void agg1(){

    }
}
