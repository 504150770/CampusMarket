package com.yjq.programmer.dto;

import java.util.List;

public class PageDTO<T> {

    /**
     * 当前页码
     */
    protected Integer page;

    /**
     * 每页条数
     */
    protected Integer size;

    /**
     * 总条数
     */
    protected Long total;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 搜索内容
     */
    private String searchContent;

    /**
     * 搜索方式
     */
    private String searchType;

    protected List<T> list;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getTotalPage() {
        return  Integer.valueOf(String.valueOf((total-1)/size+1));
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageDto{");
        sb.append("page=").append(page);
        sb.append(", size=").append(size);
        sb.append(", total=").append(total);
        sb.append(", list=").append(list);
        sb.append(", totalPage=").append(totalPage);
        sb.append(", searchContent=").append(searchContent);
        sb.append(", searchType=").append(searchType);
        sb.append('}');
        return sb.toString();
    }
}
