/**
 * Copyright (c) 2014, wylipengming@jd.com|shouli1990@gmail.com. All rights reserved.
 *
 */
package com.bit.lsm.simple;

import io.searchbox.client.JestResult;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

/**
 * <b>项目名</b>： lsm <br>
 * <b>包名称</b>： com.bit.lsm.simple <br>
 * <b>类名称</b>： ESTest <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:wylipengming@jd.com">李朋明</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>：2015/2/5 14:04<br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class ESTest {
    private JestHttpClient client = ESFactory.getClient();

    /**
     * indexing
     *
     * @param indexName
     */
    public void index(String indexName) {
        try {
            // drop
            DeleteIndex dIndex = new DeleteIndex(new DeleteIndex.Builder(
                    indexName));
            client.execute(dIndex);
            // create
            CreateIndex cIndex = new CreateIndex(new CreateIndex.Builder(
                    indexName));
            client.execute(cIndex);
            // add doc
            for (int i = 0; i < 1000; i++) {

                User user = new User();
                user.setId(new Long(i));
                user.setName("huang fox " + i);
                user.setAge(i % 100);
                Index index = new Index.Builder(user).index(indexName)
                        .type(indexName).build();
                client.execute(index);
            }
            //
            client.shutdownClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * getting by id
     *
     * @param indexName
     * @param query
     */
    public void get(String indexName, String query) {
        Get get = new Get.Builder(indexName, query).build();
        try {
            JestResult rs = client.execute(get);
            System.out.println(rs.getJsonString());
            //
            client.shutdownClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * query
     *
     * @param query
     */
    public void search(String query) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.queryString(query));
        searchSourceBuilder.field("name");
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .build();
        try {
            JestResult rs = client.execute(search);
            System.out.println(rs.getJsonString());
            //
            client.shutdownClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void indexBulk(String indexName) {
        try {
            // drop
            DeleteIndex dIndex = new DeleteIndex(new DeleteIndex.Builder(
                    indexName));
            client.execute(dIndex);
            // create
            CreateIndex cIndex = new CreateIndex(new CreateIndex.Builder(
                    indexName));
            client.execute(cIndex);
            // add doc
            Bulk.Builder bulkBuilder = new Bulk.Builder();
            for (int i = 0; i < 1000; i++) {

                User user = new User();
                user.setId(new Long(i));
                user.setName("huang fox " + i);
                user.setAge(i % 100);
                Index index = new Index.Builder(user).index(indexName)
                        .type(indexName).build();
                bulkBuilder.addAction(index);
            }
            client.execute(bulkBuilder.build());
            //
            client.shutdownClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ESTest t = new ESTest();
        String indexName = "user";
        // indexing
        t.index(indexName);
        // getting by id
        t.get(indexName, "2");
        // query
        String query = "fox";
        t.search(query);
    }
}
