package com.learning.stock_trading_client.controller;

import com.google.protobuf.util.JsonFormat;
import com.learning.StockOrder;
import com.learning.StockTradingServiceGrpc;
import com.learning.TradeStatus;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/live")
public class LiveTradingController {

    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceStub stockServiceStub;

    // Add these fields to controller
    private final AtomicReference<StockOrder> lastOrder = new AtomicReference<>();

    @GetMapping(value = "/trading/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter startLiveTrading(@PathVariable String userId) {
        SseEmitter emitter = new SseEmitter();

        StreamObserver<TradeStatus> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(TradeStatus status) {
                try {
//                    emitter.send(SseEmitter.event().data(status.toString()));

//                    // ✅ FIX: Convert protobuf to JSON
//                    JsonFormat.Printer printer = JsonFormat.printer().includingDefaultValueFields();
//                    String jsonStatus = printer.print(status);
//                    emitter.send(SseEmitter.event().data(jsonStatus));

                    StockOrder order = lastOrder.get();
                    Map<String, Object> richResponse = new HashMap<>();
                    richResponse.put("orderId", status.getOrderId());
                    richResponse.put("stockSymbol", order != null ? order.getStockSymbol() : "AAPL");
                    richResponse.put("quantity", order != null ? order.getQuantity() : 0);
                    richResponse.put("price", order != null ? order.getPrice() : 0.0);
                    richResponse.put("orderType", order != null ? order.getOrderType() : "BUY");
                    richResponse.put("status", status.getStatus());
                    richResponse.put("message", status.getMessage());
                    richResponse.put("timestamp", status.getTimestamp());

                    emitter.send(SseEmitter.event().data(new ObjectMapper().writeValueAsString(richResponse)));

                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
            @Override public void onError(Throwable t) { emitter.completeWithError(t); }
            @Override public void onCompleted() { emitter.complete(); }
        };

        StreamObserver<StockOrder> requestObserver = stockServiceStub.liveTrading(responseObserver);

        // Auto-send sample orders every 3 seconds
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            StockOrder order = StockOrder.newBuilder()
                    .setOrderId("LIVE-" + System.currentTimeMillis())
                    .setStockSymbol("AAPL")
                    .setQuantity(new Random().nextInt(100))
                    .setPrice(150 + new Random().nextDouble() * 50)
                    .setOrderType("BUY")
                    .build();

            lastOrder.set(order);  // Track for response
            requestObserver.onNext(order);
        }, 0, 3, TimeUnit.SECONDS);

        emitter.onCompletion(scheduler::shutdown);
        emitter.onTimeout(scheduler::shutdown);
        emitter.onError((e) -> { requestObserver.onError(e); scheduler.shutdown(); });

        return emitter;
    }
}

