package com.youlai.system.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class WebsocketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSendToAll() throws Exception {
        String message = "Hello!  I'm server. This is a topic message.";

        mockMvc.perform(
                        post("/messages/sendToAll")
                                .param("message", message)
                )
                .andExpect(status().isOk());
    }

    @Test
    void testSendToUser() throws Exception {

        Long userId = 2L;// 系统管理员用户ID=2

        mockMvc.perform(post("/messages/sendToUser/{userId}", userId)
                        .param("message", "Hello!  I'm server. This is a point-to-point message.")
                )
                //.content(JSONUtil.toJsonStr(webSocketMessage))
                //.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}