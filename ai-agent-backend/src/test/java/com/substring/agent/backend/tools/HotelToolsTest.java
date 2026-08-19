package com.substring.agent.backend.tools;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HotelToolsTest {

	private final HotelTools hotelTools = new HotelTools();

	@Test
	void findsHotelsWithinBudget() {
		String result = hotelTools.searchHotel("Goa", 2500);

		assertThat(result)
				.contains("Sea Breeze Resort", "Budget Stay Inn")
				.doesNotContain("Palm Grove Villas");
	}

	@Test
	void returnsMessageWhenNothingFitsTheBudget() {
		String result = hotelTools.searchHotel("Goa", 1000);

		assertThat(result).contains("No hotels found");
	}

}
