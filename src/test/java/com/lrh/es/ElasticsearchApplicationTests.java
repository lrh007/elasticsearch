package com.lrh.es;

import com.alibaba.fastjson.JSON;
import com.lrh.es.bean.User;
import com.lrh.es.service.ContentService;
import net.minidev.json.JSONObject;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ElasticsearchApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    /**
     * 创建索引
     */
    @Test
    void createIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("idea_index");
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());
    }

    /**
     * 获取索引
     */
    @Test
    void testIndexExist() throws IOException {
        GetIndexRequest request = new GetIndexRequest("idea_index");
        boolean exist = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exist);

    }
    @Test
    void getIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("idea_index");
        GetIndexResponse indexResponse = client.indices().get(request, RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
    }

    /**
     * 删除索引
     * @throws IOException
     */
    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("idea_index");
        AcknowledgedResponse  response = client.indices().delete(request,RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());

    }

    /**
     * 添加文档
     */
    @Test
    void addDocument() throws IOException {
        User user = new User("狂神说",22);
        //创建请求
        IndexRequest indexRequest = new IndexRequest("idea_index");
        //规则 put /idea_index/_doc/1
        indexRequest.id("1");
        indexRequest.timeout("1s");
        //将数据放入到请求中 json
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求,获取相应结果
        IndexResponse indexResponse = client.index(indexRequest,RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status());
    }

    /**
     * 获取文档，判断文档是否存在, get /idea_index/_doc/1
     */
    @Test
    void testIsExists() throws IOException {
       GetRequest request = new GetRequest("idea_index","1");
       boolean exists = client.exists(request,RequestOptions.DEFAULT);
       System.out.println(exists);

    }

    /**
     * 获取文档信息
     */
    @Test
    void getDocument() throws IOException {
        GetRequest request = new GetRequest("idea_index","1");
        GetResponse response = client.get(request,RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
        System.out.println(response.toString());
    }
    /**
     * 更新文档
     */
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("idea_index","1");
        request.timeout("1s");
        User user = new User("狂神说java",18);
        request.doc(JSON.toJSONString(user),XContentType.JSON);
        UpdateResponse response = client.update(request,RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }
    /**
     * 删除文档
     */
    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("idea_index","1");
        request.timeout("1s");
        DeleteResponse response = client.delete(request,RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 批量插入文档
     */
    @Test
    void testBulkDocument() throws IOException {
        BulkRequest request = new BulkRequest();
        request.timeout("2s");
        List<User> userList = new ArrayList<>();
        userList.add(new User("kuangshen1",3));
        userList.add(new User("kuangshen2",3));
        userList.add(new User("kuangshen3",3));
        userList.add(new User("lrh1",3));
        userList.add(new User("lrh2",3));
        userList.add(new User("lrh3",3));
        //批量处理请求
        for (int i = 0; i < userList.size(); i++) {
            //批量更新和批量删除，就在这里修改对应的请求即可
            request.add(
                    new IndexRequest("idea_index")
                    .id(""+(i+1)).source(JSON.toJSONString(userList.get(i)),XContentType.JSON));
        }

        BulkResponse responses = client.bulk(request,RequestOptions.DEFAULT);
        System.out.println(responses);

    }

    /**
     * 复杂查询
     * SearchRequest 搜索请求
     * SearchSourceBuilder 条件构造
     */
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("idea_index");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //精确匹配
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("name","lrh1");
        sourceBuilder.query(queryBuilder);
        sourceBuilder.timeout(new TimeValue(10, TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest,RequestOptions.DEFAULT);
        System.out.println(response);

    }
}
