package com.test.domain;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by jingjiong on 2017/1/23.
 */
@Entity
@Table(name = "test1", schema = "test")
public class Test1 implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "test1", length = 100, columnDefinition = "cc")
    private String test1;

    public String getTest1() {
        return test1;
    }

    public void setTest1(String test1) {
        this.test1 = test1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
