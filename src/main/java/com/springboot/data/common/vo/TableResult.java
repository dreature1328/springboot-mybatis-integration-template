package com.springboot.data.common.vo;

import java.util.List;

public class TableResult {

    private Integer total;
    // 分页查询的list结果
    private List<?> items;

    public TableResult() {
    }

    public TableResult(Integer total, List<?> items) {
        this.total = total;
        this.items = items;
    }

    public TableResult(Long total, List<?> items) {
        this.total = total.intValue();
        this.items = items;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<?> getItems() {
        return items;
    }

    public void setItems(List<?> items) {
        this.items = items;
    }
}
