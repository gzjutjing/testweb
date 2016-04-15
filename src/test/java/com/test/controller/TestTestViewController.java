package com.test.controller;

import com.test.domain.TestDomain;
import com.test.service.ITestService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/4/15.
 */
public class TestTestViewController {
    @Test
    public void testIndex() throws Exception {
        TestViewController testViewController = new TestViewController();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(testViewController).build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.view().name("index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("test"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        Assert.assertNotNull(mvcResult);
    }

    @Autowired
    ITestService testService;

    @Test
    public void TestMock() throws Exception {
        TestViewController testViewController = new TestViewController();

        List<TestDomain> dataList = getList(20);
        ITestService testService = Mockito.mock(ITestService.class);
        Mockito.when(testService.getMockTestList(20)).thenReturn(dataList);

        testViewController.setTestService(testService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(testViewController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/mock"))
                .andExpect(MockMvcResultMatchers.view().name("mock1"))
                .andExpect(MockMvcResultMatchers.model().attribute("mockList", dataList)).andDo(MockMvcResultHandlers.print());
    }

    private List<TestDomain> getList(int size) {
        List<TestDomain> l = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TestDomain t = new TestDomain();
            t.setId(i);
            t.setName("name=" + i);
            l.add(t);
        }
        return l;
    }
}
