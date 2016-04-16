package com.test.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by admin on 2016/4/15.
 */
public class TestDomain {
    @NotNull(message = "id不能为空")
    private Integer id;
    @NotNull(message = "name不能为空")
    @Size(min= 2,max = 6,message = "{length.valid}")
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
