# AI Agents using Spring AI & Thymeleaf

A fully functional, interactive AI Agent system built using **Spring Boot 4**, **Spring AI (version 2.0.0)**, and **Thymeleaf**.
It showcases AI Agents that use dynamic function calling (tool execution) to resolve travel queries (flights, hotels, weather) and e-commerce customer-support tasks (order status, cancellation, inventory, store policies).

> **Dual AI provider**: the same app runs on **OpenAI** (`gpt-4o-mini`) *or* **xAI Grok** (`grok-4.6`). Pick the provider with one environment variable (`AI_PROVIDER=openai|grok`). Grok is used through its OpenAI-compatible endpoint, so no OpenAI account is required for the Grok profile.

---

## 🌟 Features

- **Multi-domain AI Agent**:
  - **Travel Assistant**: Search flights, hotels, and retrieve weather forecasts.
  - **Customer Support Agent**: Cancel orders, check order status, view inventory, and answer store-policy questions.
- **Function Calling / Agentic Tools**: Uses Spring AI's `@Tool` annotations to register Java methods as executable agent tools dynamically parsed by the chat model.
- **RAG-style Knowledge Retrieval**: A `PolicyRetrievalTool` answers policy questions (returns/refunds, cancellation, shipping, loyalty, warranty) from a local knowledge base (`resources/knowledge/`) and the agent answers **strictly from the retrieved passages**. Retrieval runs fully locally — no embeddings API required, so it works with any chat provider.
- **Streaming responses (SSE)**: `POST /chat/stream` streams the reply token-by-token; the web UI renders tokens live as they arrive.
- **Conversational Memory**: Utilizes `MessageWindowChatMemory` with custom `Conversation-Id` headers to maintain context across multiple turns.
- **Modern UI**: Styled with Tailwind CSS (v4) featuring light/dark mode, real-time Markdown parsing, streaming chat, chat-history cleaning, and instant suggestion templates.

## 📂 Project Structure

```text
├── README.md                          # Project overview & running instructions
├── .gitignore                         # Project-wide Git ignore rules (ignores .vscode, .env)
├── docs/
│   ├── prompt.md                      # Example evaluation prompts
│   └── understand.excalidraw          # Architectural flow/diagram
└── ai-agent-backend/                  # Spring Boot application
    ├── pom.xml                        # Maven configuration (Spring AI dependencies)
    ├── mvnw / mvnw.cmd                # Maven Wrapper
    └── src/
        ├── main/
        │   ├── java/.../backend/
        │   │   ├── AiAgentBackendApplication.java
        │   │   ├── config/
        │   │   │   └── AiConfig.java  # ChatClient builder, memory & tool registration
        │   │   ├── controller/
        │   │   │   ├── ChatController.java  # POST /chat + POST /chat/stream (SSE)
        │   │   │   └── ViewController.java  # Renders the Thymeleaf web interface
        │   │   ├── model/             # Data objects (Flight, Hotel, User)
        │   │   ├── service/
        │   │   │   └── ChatService.java    # Blocking + streaming ChatClient calls
        │   │   └── tools/             # Custom Agentic Tools
        │   │       ├── FlightTools.java
        │   │       ├── HotelTools.java
        │   │       ├── InventoryTools.java
        │   │       ├── OrderTools.java
        │   │       ├── PolicyRetrievalTool.java   # RAG over the policy knowledge base
        │   │       └── WeatherTools.java
        │   └── resources/
        │       ├── knowledge/*.md     # Store policy documents (knowledge base)
        │       ├── templates/
        │       │   └── chat.html      # Thymeleaf UI template styled with Tailwind CSS
        │       ├── application.yml            # Chooses AI_PROVIDER=openai|grok
        │       ├── application-openai.yml     # OpenAI provider settings
        │       ├── application-grok.yml       # Grok provider settings
        │       └── application.properties
        └── test/                      # Unit + slice tests (tools & controllers)
```

---

## 🛠️ Tech Stack

- **Backend**: Spring Boot, Spring Web, Spring AI, Lombok
- **AI Integration**: OpenAI (`gpt-4o-mini`) or xAI Grok (`grok-4.6`) through the OpenAI-compatible client
- **Streaming**: Project Reactor (`Flux`) + Server-Sent Events
- **Frontend**: Thymeleaf, Tailwind CSS (Browser runtime), Marked.js (Markdown renderer)
- **Language**: Java 21+

---

## ⚙️ Configuration & Setup

- **Prerequisites**: Java 21+ and an API key from **either** provider.

- **For OpenAI** (default):
  ```bash
  export AI_PROVIDER=openai                  # default provider
  export OPENAI_API_KEY="your-openai-api-key"
  export OPENAI_CHAT_MODEL="gpt-4o-mini"
  ```

- **For Grok (xAI)**:
  ```bash
  export AI_PROVIDER=grok                     # switch to Grok
  export GROK_API_KEY="your-xai-api-key"      # create at https://console.x.ai/
  export GROK_CHAT_MODEL="grok-4.6"
  ```

The `AI_PROVIDER` value activates the matching Spring profile
(`application-openai.yml` / `application-grok.yml`) via
`spring.profiles.active=${AI_PROVIDER:openai}`. Grok speaks the
OpenAI-compatible protocol, so the same Spring AI OpenAI client is pointed at
`https://api.x.ai/v1` — no OpenAI account is needed.

---

## 🚀 Running the Application

Navigate to the `ai-agent-backend` directory and start the server:

```bash
cd ai-agent-backend
AI_PROVIDER=grok ./mvnw spring-boot:run   # or AI_PROVIDER=openai
```

Once the application starts successfully, open your browser and navigate to:
👉 **[http://localhost:8081/](http://localhost:8081/)**

### Chat API

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/chat` | `POST` | Non-streaming reply. Body: plain-text message. Header: `Conversation-Id`. |
| `/chat/stream` | `POST` | Streaming reply (Server-Sent Events). Same body and header as above. |

---

## 🤖 Registered Agent Tools

The agent can call the following Java tools automatically depending on your input:

| Tool Class | Method Name | Description |
| :--- | :--- | :--- |
| `FlightTools` | `searchFlight` | Searches flights by source, destination, and date. |
| `HotelTools` | `searchHotel` | Finds hotels matching a city and max budget per night. |
| `WeatherTools` | `getForecast` | Retrieves weather conditions (temp, status) for a city. |
| `OrderTools` | `getOrderStatus` | Finds order dispatch status by its numeric ID (e.g. `1042`). |
| `OrderTools` | `cancelOrder` | Cancels an active order using the order ID. |
| `OrderTools` | `getOrderCount` | Fetches the total count of mock orders. |
| `InventoryTools` | `checkStock` | Checks item availability by product name. |
| `InventoryTools` | `getAllProductsInStock` | Returns all available products in stock. |
| `PolicyRetrievalTool` | `searchStorePolicy` | Searches the policy knowledge base (returns, cancellation, shipping, loyalty, warranty). |
| `PolicyRetrievalTool` | `listPolicyTopics` | Lists the policy topics available in the knowledge base. |

---

## 💡 Example Prompt for Testing

Try pasting this prompt into the Chat UI to watch the agent perform multi-tool reasoning:

> *"Plan a trip from Delhi to Goa on 2026-08-21. My total budget for flight + one night hotel is ₹7000. What should I book, and what's the weather going to be like?"*

The agent will:
1. Search flights from Delhi to Goa on `2026-08-21`.
2. Check for hotels in Goa.
3. Fetch the weather forecast for Goa on `2026-08-21`.
4. Calculate options that fit within your budget limit of `₹7000` and return a tailored plan!

More examples:

> "What is the status of order 1042? Can you cancel order 1043?"

> "Which products are in stock?"

> "What is your return and refund policy?" (exercises the RAG policy tool)

---

## ✅ Tests

```bash
cd ai-agent-backend
./mvnw test
```

Runs 21 unit and slice tests covering the agent tools (flights, hotels,
orders, inventory, policy retrieval) and both chat endpoints (blocking + SSE).
- **Streaming responses (SSE)**: `POST /chat/stream` streams the reply token-by-token; the web UI renders tokens live as they arrive.
- **Conversational Memory**: Utilizes `MessageWindowChatMemory` with custom `Conversation-Id` headers to maintain context across multiple turns.
- **Modern UI**: Styled with Tailwind CSS (v4) featuring light/dark mode, real-time Markdown parsing, streaming chat, chat-history cleaning, and instant suggestion templates.

---
