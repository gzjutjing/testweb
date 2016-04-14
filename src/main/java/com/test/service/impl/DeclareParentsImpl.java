package com.test.service.impl;

import com.test.service.IDeclareParentsTest;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2016/4/13.
 */
@Service
public class DeclareParentsImpl implements IDeclareParentsTest {

    @Override
    public void showMe() {
        System.out.println("-----------------this is DeclareParents");
    }
}
