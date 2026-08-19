package com.substring.agent.backend.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.substring.agent.backend.service.ChatService;

import reactor.core.publisher.Flux;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ChatService chatService;

	@Test
	void chatReturnsCompleteReply() throws Exception {
		when(chatService.chat("hello", "conv-1")).thenReturn("Hi there!");

		mockMvc.perform(post("/chat")
				.header("Conversation-Id", "conv-1")
				.contentType(MediaType.TEXT_PLAIN)
				.content("hello"))
				.andExpect(status().isOk())
				.andExpect(content().string("Hi there!"));
	}

	@Test
	void chatStreamReturnsServerSentEvents() throws Exception {
		when(chatService.stream("hello", "conv-1")).thenReturn(Flux.just("Hello ", "world"));

		MvcResult result = mockMvc.perform(post("/chat/stream")
				.header("Conversation-Id", "conv-1")
				.contentType(MediaType.TEXT_PLAIN)
				.content("hello"))
				.andExpect(request().asyncStarted())
				.andReturn();

		mockMvc.perform(asyncDispatch(result))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Hello")));
	}

}
