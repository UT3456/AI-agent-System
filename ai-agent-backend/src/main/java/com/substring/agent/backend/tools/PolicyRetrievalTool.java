package com.substring.agent.backend.tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Knowledge-retrieval (RAG) tool that searches the store's policy documents.
 *
 * The documents live in {@code classpath:knowledge/*.md} and are matched with a
 * lightweight term-overlap scoring function (title matches count more than body
 * matches). This keeps retrieval fully local and offline - no embedding service
 * or extra API key is required, only the chat model (Grok) needs a key.
 */
@Component
public class PolicyRetrievalTool {

	private static final Logger log = LoggerFactory.getLogger(PolicyRetrievalTool.class);

	private static final Pattern TOKEN_SPLIT = Pattern.compile("[^\\p{L}\\p{N}]+");

	private static final int MAX_RESULTS = 3;

	private record PolicyDocument(String title, String source, String content) {
	}

	private final List<PolicyDocument> documents;

	public PolicyRetrievalTool() {
		this.documents = loadDocuments();
		log.info("Loaded {} policy documents into the knowledge base.", documents.size());
	}

	@Tool(name = "searchStorePolicy", description = "Search the store's official policy documents covering returns & refunds, order cancellation, shipping & delivery, loyalty program and warranty & support. Use this whenever a customer asks about policies, eligibility, time frames or steps to follow. Returns the most relevant passages verbatim with their source.")
	public String searchStorePolicy(String query) {
		List<PolicyDocument> hits = rankDocuments(query);
		if (hits.isEmpty()) {
			return "No policy documents found matching: " + query;
		}
		return hits.stream()
				.map(doc -> "[Source: " + doc.source() + "]\n" + doc.content())
				.collect(Collectors.joining("\n\n---\n\n"));
	}

	@Tool(name = "listPolicyTopics", description = "List the policy topics available in the store's knowledge base, e.g. returns & refunds, order cancellation, shipping & delivery, loyalty program, warranty & support.")
	public String listPolicyTopics() {
		if (documents.isEmpty()) {
			return "No policy topics available.";
		}
		return documents.stream().map(PolicyDocument::title).collect(Collectors.joining(", "));
	}

	private List<PolicyDocument> rankDocuments(String query) {
		List<String> queryTerms = tokenize(query);
		if (queryTerms.isEmpty()) {
			return List.of();
		}
		return documents.stream()
				.map(doc -> Map.entry(doc, score(doc, queryTerms)))
				.filter(entry -> entry.getValue() > 0)
				.sorted(Comparator.comparingInt((Map.Entry<PolicyDocument, Integer> entry) -> entry.getValue()).reversed())
				.limit(MAX_RESULTS)
				.map(Map.Entry::getKey)
				.toList();
	}

	private int score(PolicyDocument doc, List<String> queryTerms) {
		String title = doc.title().toLowerCase(Locale.ROOT);
		String body = doc.content().toLowerCase(Locale.ROOT);
		int score = 0;
		for (String term : queryTerms) {
			if (title.contains(term)) {
				score += 3;
			}
			if (body.contains(term)) {
				score += 1;
			}
		}
		return score;
	}

	private List<String> tokenize(String text) {
		return TOKEN_SPLIT.splitAsStream(text.toLowerCase(Locale.ROOT))
				.filter(token -> token.length() > 1)
				.toList();
	}

	private List<PolicyDocument> loadDocuments() {
		List<PolicyDocument> loaded = new ArrayList<>();
		try {
			Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:knowledge/*.md");
			for (Resource resource : resources) {
				try (InputStream in = resource.getInputStream()) {
					String content = new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
					if (content.isEmpty()) {
						continue;
					}
					String source = String.valueOf(resource.getFilename());
					String title = content.lines().findFirst().orElse(source).trim();
					loaded.add(new PolicyDocument(title, source, content));
				}
			}
		} catch (IOException ex) {
			log.warn("Could not load knowledge documents: {}", ex.getMessage());
		}
		return loaded;
	}

}

