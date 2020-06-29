package com.lrh.es.service;

import com.alibaba.fastjson.JSON;
import com.lrh.es.bean.Content;
import com.lrh.es.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ContentService {

    @Autowired
    RestHighLevelClient restHighLevelClient;
    private static final String INDEX = "jd_goods_index";



    /**
     * 解析数据放入es中
     * @param keyword
     * @return
     */
    public Boolean parseContent(String keyword){
        try {
            List<Content> contentList = HtmlParseUtil.parseJD(keyword);
            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.timeout("20m");
            //判断索引是否存在，不存在就创建
            GetIndexRequest getIndexRequest = new GetIndexRequest(INDEX);
            boolean exists = restHighLevelClient.indices().exists(getIndexRequest,RequestOptions.DEFAULT);
            boolean b = false;
            if(!exists){
                System.out.println("索引："+INDEX+" 不存在，重新创建");
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX);
                CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                b = createIndexResponse.isAcknowledged();
            }else{
                System.out.println("索引："+INDEX+" 存在");
                b = true;
            }
            //索引创建成功后添加数据
            if(b){
                for (Content content : contentList) {
                    bulkRequest.add(new IndexRequest(INDEX).source(JSON.toJSONString(content), XContentType.JSON));
                }
                BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest,RequestOptions.DEFAULT);
                return !bulkResponse.hasFailures();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Map<String,Object>> searchPage(String keyword, int pageNo, int pageSize){

        if(pageNo <= 1){
            pageNo = 1;
        }

        //条件搜索
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);
        //高亮显示关键字
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        highlightBuilder.requireFieldMatch(false);  //多个高亮显示
        searchSourceBuilder.highlighter(highlightBuilder);

        //精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title",keyword);
        //模糊匹配
//        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title",keyword);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchSourceBuilder.query(termQueryBuilder);
        //执行搜索
        searchRequest.source(searchSourceBuilder);
        try {
           SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
           List<Map<String,Object>> resultList = new ArrayList<>();
           //解析结果
           for(SearchHit hit:searchResponse.getHits().getHits()){
               //获取高亮字段
               Map<String,HighlightField> highlightFields = hit.getHighlightFields();
               HighlightField title = highlightFields.get("title");
               Map<String,Object> sourceAsMap = hit.getSourceAsMap(); //原来的字段
               //解析高亮字段，将原来的字段替换即可
               if(title!=null){
                   Text[] fragments = title.fragments();
                   StringBuilder new_title = new StringBuilder();
                   for (Text fragment : fragments) {
                       new_title.append(fragment);
                   }
                   sourceAsMap.put("title",new_title.toString());
               }
               resultList.add(sourceAsMap);
           }
           return resultList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
