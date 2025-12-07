package com.hms.PharmacyMS.service;

import com.hms.PharmacyMS.dto.SaleDTO;
import com.hms.PharmacyMS.dto.SaleRequest;
import com.hms.PharmacyMS.dto.SaleResponseDTO;

import java.util.List;

public interface SaleService {
    SaleResponseDTO createSale(SaleRequest dto);
    void updateSale(SaleDTO dto);
    SaleDTO getSale(Long id);
    SaleDTO getSaleByPrescriptionId(Long prescriptionId)    ;
    List<SaleDTO> getAllSales();
    void confirmPaymentSuccess(Long saleId);
}
