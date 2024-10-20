package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class InventoryService {

    @Autowired
    InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode){
        log.info("Inventory Method is called");
        List<Inventory> inventorys = inventoryRepository.findBySkuCodeIn(skuCode);
        return inventorys.stream().map(this::maptoInventoryResponse).toList();
    }

    public InventoryResponse maptoInventoryResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .isPresent(inventory.getQuantity()>0)
                .build();
    }
}
