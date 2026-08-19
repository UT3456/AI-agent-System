package com.substring.agent.backend.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.substring.agent.backend.tools.FlightTools;
import com.substring.agent.backend.tools.HotelTools;
import com.substring.agent.backend.tools.InventoryTools;
import com.substring.agent.backend.tools.OrderTools;
import com.substring.agent.backend.tools.PolicyRetrievalTool;
import com.substring.agent.backend.tools.WeatherTools;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AiConfig {

        private final OrderTools tools;
        private final InventoryTools inventoryTools;
        private final FlightTools flightTools;
        private final HotelTools hotelTools;
        private final WeatherTools weatherTools;
        private final PolicyRetrievalTool policyRetrievalTool;

        @Bean
        public ChatMemory chatMemory() {
                return MessageWindowChatMemory
                                .builder()
                                .maxMessages(10)
                                .build();
        }

        @Bean
        public ChatClient chatClient(
                        ChatClient.Builder builder) {
                return builder
                                .defaultSystem(
                                                """
                                                                  You are a helpful travel planning agent and a customer support agent for an online store.
                                                                  Use the available tools to find flights, hotels, and weather information for users.
                                                                  When given a budget, suggest options that fit within it and explain your reasoning.

                                                                  For any customer support question about store policies (returns & refunds, order
                                                                  cancellation, shipping & delivery, loyalty program, warranty & support), first call
                                                                  the searchStorePolicy tool and answer strictly from the retrieved passages. If the
                                                                  retrieved documents do not cover the question, say you don't know instead of guessing.

                                                                  For order questions use the order tools (getOrderStatus, cancelOrder, getOrderCount)
                                                                  and for product questions use the inventory tools (checkStock, getAllProductsInStock).
                                                                """)
                                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory()).build())
                                .defaultTools(tools, inventoryTools, flightTools, hotelTools, weatherTools,
                                                policyRetrievalTool)
                                .build();
        }

}
