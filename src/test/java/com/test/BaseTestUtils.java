package com.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 工具类
 * Created by admin on 2016/4/11.
 */
public class BaseTestUtils {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<String> test(String[][] arr) {
        return Arrays.stream(arr).flatMap(Arrays::stream).collect(Collectors.toList());
    }

    public String op(Optional<String> s) {
        return s.orElse(null);
    }
}
