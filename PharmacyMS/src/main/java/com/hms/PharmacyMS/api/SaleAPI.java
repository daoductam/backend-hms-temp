package com.hms.PharmacyMS.api;

import com.hms.PharmacyMS.dto.*;
import com.hms.PharmacyMS.entity.Sale;
import com.hms.PharmacyMS.service.SaleItemService;
import com.hms.PharmacyMS.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/pharmacy/sales")
@RequiredArgsConstructor
public class SaleAPI {
    private final SaleService saleService;
    private final SaleItemService saleItemService;

    @PostMapping("/create")
    public ResponseEntity<SaleResponseDTO> createSale(@RequestBody SaleRequest dto) {
        return new ResponseEntity<>(saleService.createSale(dto), HttpStatus.CREATED);
    }

    // API xác nhận thanh toán (Internal - Chỉ PaymentMS gọi)
    // URL: /pharmacy/sales/internal/confirm?saleId=123
    @PostMapping("/internal/confirm")
    public ResponseEntity<Void> confirmPayment(@RequestParam Long saleId) {
        saleService.confirmPaymentSuccess(saleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public  ResponseEntity<ResponseDTO> updateSale(@RequestBody SaleDTO dto) {
        saleService.updateSale(dto);
        return new ResponseEntity<>(new ResponseDTO("Sale updated successfull"), HttpStatus.OK);
    }

    @GetMapping("/getSaleItems/{saleId}")
    public  ResponseEntity<List<SaleItemDTO>> getSaleItems(@PathVariable Long saleId) {
        List<SaleItemDTO> saleItems = saleItemService.getSaleItemBySaleId(saleId);
        return new ResponseEntity<>(saleItems, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<SaleDTO> getSale(@PathVariable Long id) {
        SaleDTO sale = saleService.getSale(id);
        return new ResponseEntity<>(sale, HttpStatus.OK);

    }

    @GetMapping("/getAll")
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        List<SaleDTO> sales = saleService.getAllSales();
        return new ResponseEntity<>(sales , HttpStatus.OK);
    }

}
