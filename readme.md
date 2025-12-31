# 🚀📈 Spring Boot gRPC Stock Trading Platform

Spring Boot application demonstrating all 4 gRPC streaming patterns with live web UIs and MySQL persistence.

## 📋 Features

| Pattern | Description | UI |
|---------|-------------|-----|
| **Unary RPC** | Single stock price lookup | REST API |
| **Server Streaming** | Live stock price updates | 📈 `/home` |
| **Client Streaming** | Bulk order submission | 📋 `/bulk-orders` |
| **Bidirectional Streaming** | Real-time order book | 🔥 `/live-trading` |

## 🏗️ Project Structure

stock-trading-app/

├── pom.xml<br/>
├── README.md<br/>
├── .gitignore<br/>
├── stock-trading-server/ # gRPC Server (port 9090)<br/>
└── stock-trading-client/ # REST Client + UIs (port 8080)<br/>


## 🚀 Quick Start

1. Start MySQL

2. Terminal 1 - Server
   cd stock-trading-server
   mvn spring-boot:run

3. Terminal 2 - Client + UIs
   cd stock-trading-client
   mvn spring-boot:run

4. Open UIs<br/>
   📈 Live stock charts: http://localhost:8080/home <br/>
   📋 Bulk orders: http://localhost:8080/bulk-orders <br/>
   🔥 Live trading: http://localhost:8080/live-trading <br/>


## 🔄 Project Flow

Browser (8080) Client App gRPC Server (9090) MySQL
↓ REST/SSE ↓ gRPC ↓ JPA

getStockPrice("AAPL") ➜ ↓ Fetch ➜ Single response <br/>
subscribeStockPrice() ⬇️ ↓ Stream ➜ Live AAPL prices <br/>
bulkStockOrder() ➜ ⬇️ ↓ Aggregate ➜ Order summary <br/>
liveTrading() ↕️ ⬇️ ↕️ Auto-orders 3s ➜ SSE confirmations <br/>

1. **Unary (request → response)**
2. **Server Streaming**: Browser subscribes → Server streams live AAPL prices → Chart updates
3. **Client Streaming**: Browser sends bulk orders → Server aggregates → Returns summary
4. **Bidirectional**: Browser starts trading → Server auto-sends orders every 3s ↔ Live confirmations

## 🛠️ Tech Stack

- Spring Boot + Spring gRPC
- gRPC + Protobuf
- Thymeleaf + Bootstrap + SSE(Server Sent Events)
- MySQL + Spring Data JPA
- Maven multi-module

## 🙌 Acknowledgments

- **JavaTechie** - gRPC Spring Boot tutorials
- **Perplexity AI** - Real-time debugging assistance
