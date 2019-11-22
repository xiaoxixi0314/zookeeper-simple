package com.github.xiaoxixi.lock;

import com.github.xiaoxixi.App;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=App.class)
@TestPropertySource({"classpath:/application.properties"})
public class BaseTest {
}
