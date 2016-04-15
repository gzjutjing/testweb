package com.test.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by admin on 2016/4/15.
 */
public class TestDomain {
    @NotNull
    private Integer id;
    @NotNull
    @Min(value = 2,message = "{name.min.message}")
    @Max(value = 6,message = "最长长度为6")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
