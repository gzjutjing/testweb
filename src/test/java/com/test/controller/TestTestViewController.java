package com.test.controller;

import com.test.domain.TestDomain;
import com.test.service.ITestService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.velocity.VelocityLayoutView;
import org.springframework.web.servlet.view.velocity.VelocityView;
import org.thymeleaf.spring4.view.ThymeleafView;

import javax.xml.stream.events.Characters;
import java.util.ArrayList;
import java.util.LinkedList;
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

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(testViewController).setSingleView(new InternalResourceView("/mock1.html")).build();
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

    @Test
    public void testMockpath() throws Exception {
        TestDomain testDomain = new TestDomain();
        testDomain.setId(1);
        testDomain.setName("1");

        ITestService testServiceMock = Mockito.mock(ITestService.class);
        Mockito.when(testServiceMock.getById("12345")).thenReturn(testDomain);

        TestViewController t=new TestViewController();
        t.setTestService(testServiceMock);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(t)
                .setSingleView(new InternalResourceView("mock2.html")).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/mock/12345"))
                .andExpect(MockMvcResultMatchers.view().name("mock2"))
                .andExpect(MockMvcResultMatchers.model().attribute("id", "12345"))
                .andDo(MockMvcResultHandlers.print());
        Mockito.verify(testServiceMock,Mockito.atLeastOnce()).getById("12345");
    }

    @Test
    public void testValidTest() throws Exception {

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new TestViewController()).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/validTest").param("id","1233").param("name","name").characterEncoding("utf-8").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().string("ok"));
        mockMvc.perform(MockMvcRequestBuilders.get("/validTest").param("id","1233").param("name","n").characterEncoding("utf-8").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().string("长度必须在2和6之间"));

    }
}
