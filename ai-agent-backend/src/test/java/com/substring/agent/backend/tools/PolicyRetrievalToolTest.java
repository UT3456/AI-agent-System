package com.substring.agent.backend.tools;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PolicyRetrievalToolTest {

	private final PolicyRetrievalTool policyRetrievalTool = new PolicyRetrievalTool();

	@Test
	void retrievesTheMostRelevantPolicyForAQuery() {
		String result = policyRetrievalTool.searchStorePolicy("how many days do I have to return an item");

		assertThat(result)
				.contains("RETURNS & REFUNDS POLICY")
				.contains("returns-and-refunds.md")
				.contains("30 days");
	}

	@Test
	void retrievesCancellationPolicy() {
		String result = policyRetrievalTool.searchStorePolicy("can I cancel my order before it ships");

		assertThat(result)
				.contains("ORDER CANCELLATION POLICY")
				.contains("cancellation-policy.md");
	}

	@Test
	void returnsMessageWhenNothingMatches() {
		assertThat(policyRetrievalTool.searchStorePolicy("quantum zxcvbar clearly unrelated"))
				.contains("No policy documents found");
	}

	@Test
	void listsAvailablePolicyTopics() {
		assertThat(policyRetrievalTool.listPolicyTopics())
				.contains("RETURNS & REFUNDS POLICY", "WARRANTY & SUPPORT POLICY", "LOYALTY PROGRAM");
	}

}
