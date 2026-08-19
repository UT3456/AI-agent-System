package com.substring.agent.backend.tools;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FlightToolsTest {

	private final FlightTools flightTools = new FlightTools();

	@Test
	void findsFlightsForMatchingRouteAndDate() {
		String result = flightTools.searchFlight("Delhi", "Goa", "2026-08-21");

		assertThat(result)
				.contains("IndiGo", "Air India", "SpiceJet")
				.contains("₹");
	}

	@Test
	void returnsMessageWhenNoRouteMatches() {
		String result = flightTools.searchFlight("Delhi", "Tokyo", "2026-08-21");

		assertThat(result).contains("No flights found");
	}

}
