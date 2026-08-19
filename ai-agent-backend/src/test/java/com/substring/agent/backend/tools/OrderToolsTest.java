package com.substring.agent.backend.tools;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OrderToolsTest {

	private final OrderTools orderTools = new OrderTools();

	@Test
	void returnsStatusForKnownOrder() {
		assertThat(orderTools.getOrderStatus("1042")).contains("Shipped");
	}

	@Test
	void returnsNotFoundForUnknownOrder() {
		assertThat(orderTools.getOrderStatus("9999")).contains("Order not found");
	}

	@Test
	void cancellationUpdatesTheOrderStatus() {
		String reply = orderTools.cancelOrder("1043");

		assertThat(reply).contains("has been cancelled");
		assertThat(orderTools.getOrderStatus("1043")).isEqualTo("Cancelled");
	}

	@Test
	void cannotCancelUnknownOrder() {
		assertThat(orderTools.cancelOrder("9999")).contains("Order not found");
	}

	@Test
	void countsRegisteredOrders() {
		assertThat(orderTools.getOrderCount()).isEqualTo(4);
	}

}
