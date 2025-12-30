package com.learning.stock_trading_client.service;

import com.learning.OrderSummary;
import com.learning.StockOrder;
import com.learning.StockTradingServiceGrpc;
import com.learning.stock_trading_client.dtos.OrderSummaryDTO;
import com.learning.stock_trading_client.dtos.StockOrderDTO;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class StockClientServiceWithUI {

    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceStub stockServiceStub;

    public OrderSummaryDTO sendBulkOrders(List<StockOrderDTO> ordersDTO) {
        CountDownLatch latch = new CountDownLatch(1);
        final OrderSummaryDTO[] resultHolder = new OrderSummaryDTO[1];

        StreamObserver<OrderSummary> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(OrderSummary summary) {
                // Convert Protobuf OrderSummary to DTO
                resultHolder[0] = new OrderSummaryDTO();
                resultHolder[0].setTotalOrders(summary.getTotalOrders());
                resultHolder[0].setSuccessCount(summary.getSuccessCount());
                resultHolder[0].setTotalAmount(summary.getTotalAmount());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };

        StreamObserver<StockOrder> requestObserver = stockServiceStub.bulkStockOrder(responseObserver);

        // Convert the DTOs to Protobuf messages and send them
        for (StockOrderDTO orderDTO : ordersDTO) {
            StockOrder order = StockOrder.newBuilder()
                    .setOrderId(orderDTO.getOrderId())
                    .setStockSymbol(orderDTO.getStockSymbol())
                    .setOrderType(orderDTO.getOrderType())
                    .setPrice(orderDTO.getPrice())
                    .setQuantity(orderDTO.getQuantity())
                    .build();

            requestObserver.onNext(order);
        }

        requestObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resultHolder[0]; // Return DTO to the controller
    }


}
