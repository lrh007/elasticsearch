package com.lrh.es.utils;

import com.lrh.es.bean.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析网页
 */
public class HtmlParseUtil {
//    public static void main(String[] args) throws IOException {
//        parseJD("心里").forEach(System.out::println);
//    }

    /**
     * 解析京东搜索页面
     * @param keyword
     * @return
     * @throws IOException
     */
    public static List<Content> parseJD(String keyword) throws IOException {
        keyword = URLEncoder.encode(keyword,"UTF-8"); //支持中文编码
        String url = "https://search.jd.com/Search?keyword="+keyword;
        //解析网页
        Document document = Jsoup.parse(new URL(url), 30000);
        //获取所有的div元素
        Element div = document.getElementById("J_goodsList");
        //获取所有li元素
        Elements lis = div.getElementsByTag("li");
        List<Content> contentList = new ArrayList<>();
        //获取元素中的内容，这里el就是每一个li标签了
        for(Element li:lis){
            String img = li.getElementsByTag("img").eq(0).attr("src");  //图片
            String price = li.getElementsByClass("p-price").eq(0).text(); //价格
            String title = li.getElementsByClass("p-name").eq(0).text(); //标题
            contentList.add(new Content(title,img,price));
        }
        return contentList;
    }
}
