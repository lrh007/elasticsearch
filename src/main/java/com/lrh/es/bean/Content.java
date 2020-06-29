package com.lrh.es.bean;

public class Content {
    /**
     * 标题
     */
    private String title;
    /**
     * 图片
     */
    private String img;
    /**
     * 价格
     */
    private String price;

    public Content() {
    }

    public Content(String title, String img, String price) {
        this.title = title;
        this.img = img;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Content{" +
                "title='" + title + '\'' +
                ", img='" + img + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
