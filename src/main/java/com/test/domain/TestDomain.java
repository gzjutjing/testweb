package com.test.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by admin on 2016/4/15.
 */
@Entity
@Table(name = "test", schema = "test")
public class TestDomain implements Serializable {
    @Id
    @GeneratedValue
    @NotNull(message = "id不能为空")
    private Integer id;
    @NotNull(message = "name不能为空")
    @Size(min = 2, max = 6, message = "{length.valid}")
    @Column(name = "name", length = 100, columnDefinition = "描述")
    private String name;
    @Column(name = "testjpa", length = 100, columnDefinition = "columndefinition")
    private String testjpa;

    public String getTestjpa() {
        return testjpa;
    }

    public void setTestjpa(String testjpa) {
        this.testjpa = testjpa;
    }

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
