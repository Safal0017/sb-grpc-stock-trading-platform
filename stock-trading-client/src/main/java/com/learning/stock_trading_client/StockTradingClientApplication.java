package com.learning.stock_trading_client;

import com.learning.stock_trading_client.service.StockClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockTradingClientApplication implements CommandLineRunner {

	private StockClientService stockClientService;

	public StockTradingClientApplication(StockClientService stockClientService) {
		this.stockClientService = stockClientService;
	}

	public static void main(String[] args) {
		SpringApplication.run(StockTradingClientApplication.class, args);
	}

//	Unary Request Response
//	@Override
//	public void run(String... args) throws Exception {
//		System.out.println("Grpc client response : "+stockClientService.getStockPrice("AAPL"));
//	}

	@Override
	public void run(String... args) throws Exception {
		// stockClientService.subscribeStockPrice("AMZN");
		// stockClientService.placeBulkOrders(); //Client Streaming
		stockClientService.startLiveTrading(); //Bidirectional Streaming.
	}
}
