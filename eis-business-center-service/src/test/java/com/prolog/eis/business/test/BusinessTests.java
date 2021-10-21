package com.prolog.eis.business.test;

import com.prolog.eis.bc.BusinessApplication;
import com.prolog.eis.bc.service.inbound.InboundDispatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessApplication.class)
public class BusinessTests {
    @Autowired
    private InboundDispatch inboundDispatch;

    @Test
    public void test1() throws Exception {
        inboundDispatch.inboundSchedule();
    }
}
