# Example prompts for testing the AI agent.

## Travel planning (multi-tool)
```
Plan a trip from Delhi to Goa on 2026-08-21. My total budget for flight + one night hotel is ₹7000. What should I book, and what's the weather going to be like?
```

## Customer support (orders + inventory)
```
What is the status of order 1042? Can you cancel order 1043?
```

```
Which products are in stock right now?
```

## Store policies (RAG knowledge retrieval)
```
What is your return and refund policy?
```

```
Can I cancel my order after it has been shipped?
```

## Provider setup
- OpenAI: `AI_PROVIDER=openai`, `OPENAI_API_KEY=...`
- Grok (xAI): `AI_PROVIDER=grok`, `GROK_API_KEY=...` (create at https://console.x.ai/)

