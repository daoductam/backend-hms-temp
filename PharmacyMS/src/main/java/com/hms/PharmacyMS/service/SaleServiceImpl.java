package com.hms.PharmacyMS.service;

import com.hms.PharmacyMS.clients.PaymentClient;
import com.hms.PharmacyMS.dto.*;
import com.hms.PharmacyMS.entity.Sale;
import com.hms.PharmacyMS.exception.ErrorCode;
import com.hms.PharmacyMS.exception.HmsException;
import com.hms.PharmacyMS.repository.SaleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService{
    private final SaleRepository saleRepository;
    private final SaleItemService saleItemService;
    private final MedicineInventoryService medicineInventoryService;
    private final PaymentClient paymentClient;

    @Override
    @Transactional
    public SaleResponseDTO createSale(SaleRequest dto) {
        if (dto.getPrescriptionId()!=null && saleRepository.existsByPrescriptionId(dto.getPrescriptionId())) {
            throw new HmsException(ErrorCode.SALE_ALREADY_EXISTS);
        }
        for (SaleItemDTO saleItem : dto.getSaleItems()) {
            saleItem.setBatchNo(
                    medicineInventoryService.sellStock(saleItem.getMedicineId(),
                            saleItem.getQuantity()));

        }
        Sale sale = Sale.builder()
                .id(null)
                .prescriptionId(dto.getPrescriptionId())
                .buyerName(dto.getBuyerName())
                .buyerContact(dto.getBuyerContact())
                .saleDate(LocalDateTime.now())
                .totalAmount(dto.getTotalAmount())
                .status("PENDING")
                .build();
        sale = saleRepository.save(sale);
        saleItemService.createSaleItems(sale.getId(), dto.getSaleItems());
        // 3. Gọi PaymentMS để lấy link thanh toán
        String paymentUrl = "";
        try {
            PaymentRequestDTO payReq = new PaymentRequestDTO();
            payReq.setSaleId(sale.getId());
            payReq.setAmount(BigDecimal.valueOf(sale.getTotalAmount()));
            payReq.setProviderName("momo"); // Mặc định hoặc lấy từ request
            payReq.setReturnUrl("http://localhost:3000/payment-success"); // Frontend URL

            PaymentLinkResponse payRes = paymentClient.createPayment(payReq);
            paymentUrl = payRes.getPayUrl();
        } catch (Exception e) {
            e.printStackTrace();
            // Có thể log lỗi nhưng không throw exception để giữ đơn hàng đã tạo
            paymentUrl = "ERROR_GENERATING_LINK";
        }

        // 4. Trả về kết quả cho Frontend
        return SaleResponseDTO.builder()
                .saleId(sale.getId())
                .status(sale.getStatus())
                .paymentUrl(paymentUrl) // Frontend sẽ redirect user theo link này
                .build();
    }

    // Hàm cập nhật trạng thái (Dành cho Internal API)
    @Override
    public void confirmPaymentSuccess(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new HmsException(ErrorCode.SALE_NOT_FOUND));

        if (!"PAID".equals(sale.getStatus())) {
            sale.setStatus("PAID");
            saleRepository.save(sale);
            System.out.println("Sale " + saleId + " confirmed as PAID.");
        }
    }

    @Override
    public void updateSale(SaleDTO dto) {
        Sale sale =
                saleRepository.findById(dto.getId())
                        .orElseThrow(() -> new HmsException(ErrorCode.SALE_NOT_FOUND));
        sale.setSaleDate(dto.getSaleDate());
        sale.setTotalAmount(dto.getTotalAmount());
        saleRepository.save(sale);
    }

    @Override
    public SaleDTO getSale(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new HmsException(ErrorCode.SALE_NOT_FOUND)).toDTO();
    }

    @Override
    public SaleDTO getSaleByPrescriptionId(Long prescriptionId) {
        return saleRepository.findByPrescriptionId(prescriptionId)
                .orElseThrow(() -> new HmsException(ErrorCode.SALE_NOT_FOUND)).toDTO();

    }

    @Override
    public List<SaleDTO> getAllSales() {
        return saleRepository.findAll().stream().map(Sale::toDTO).toList();
    }
}
