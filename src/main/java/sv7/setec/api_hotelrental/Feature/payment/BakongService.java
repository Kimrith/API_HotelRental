package sv7.setec.api_hotelrental.Feature.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sv7.setec.api_hotelrental.Config.BakongConfig;
import sv7.setec.api_hotelrental.Feature.payment.Dtos.BakongCheckTransactionDto;
import sv7.setec.api_hotelrental.Feature.payment.Dtos.BakongQrResponseDto;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class BakongService {

    private final BakongConfig bakongConfig;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Generates a dynamic EMVCo-compliant KHQR payload string and calculates its MD5 hash.
     */
    public BakongQrResponseDto generateDynamicKhqr(BigDecimal amount, String currency, String billNumber) {
        String resolvedCurrency = (currency != null && currency.equalsIgnoreCase("KHR")) ? "KHR" : "USD";
        String currencyCode = resolvedCurrency.equals("KHR") ? "116" : "840";
        String amountFormatted = amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();

        StringBuilder sb = new StringBuilder();
        // Tag 00: Payload Format Indicator (01)
        sb.append(formatTag("00", "01"));
        // Tag 01: Point of Initiation Method (12 = Dynamic with Amount)
        sb.append(formatTag("01", "12"));

        // Tag 29: Merchant Account Info (Bakong Account)
        String bakongId = bakongConfig.getBakongId();
        String acquiringId = bakongConfig.getAcquiringId() != null ? bakongConfig.getAcquiringId() : bakongId;
        String sub29 = formatTag("00", bakongId) + formatTag("01", acquiringId);
        sb.append(formatTag("29", sub29));

        // Tag 52: Merchant Category Code (5999)
        sb.append(formatTag("52", "5999"));
        // Tag 53: Transaction Currency (840 = USD, 116 = KHR)
        sb.append(formatTag("53", currencyCode));
        // Tag 54: Transaction Amount
        sb.append(formatTag("54", amountFormatted));
        // Tag 58: Country Code (KH)
        sb.append(formatTag("58", "KH"));
        // Tag 59: Merchant Name
        sb.append(formatTag("59", bakongConfig.getMerchantName()));
        // Tag 60: Merchant City
        sb.append(formatTag("60", bakongConfig.getMerchantCity()));

        // Tag 62: Additional Data (Optional Bill Number)
        if (billNumber != null && !billNumber.trim().isEmpty()) {
            String sub62 = formatTag("01", billNumber.trim());
            sb.append(formatTag("62", sub62));
        }

        // Tag 63: CRC16 Checksum
        String rawForCrc = sb.toString() + "6304";
        String crc = calculateCRC16(rawForCrc);
        String fullQr = rawForCrc + crc;

        // Calculate MD5 of the full QR string
        String md5 = calculateMD5(fullQr);

        return BakongQrResponseDto.builder()
                .qrString(fullQr)
                .md5(md5)
                .amount(amount)
                .currency(resolvedCurrency)
                .bakongId(bakongId)
                .merchantName(bakongConfig.getMerchantName())
                .merchantCity(bakongConfig.getMerchantCity())
                .build();
    }

    /**
     * Checks if a KHQR transaction is paid by verifying its MD5 with NBC Bakong API.
     */
    public BakongCheckTransactionDto checkTransactionByMd5(String md5) {
        if (md5 == null || md5.trim().isEmpty()) {
            return BakongCheckTransactionDto.builder()
                    .isPaid(false)
                    .responseCode(1)
                    .responseMessage("MD5 is required")
                    .build();
        }

        final String cleanMd5 = md5.trim().toLowerCase();
        final String endpoint = bakongConfig.getApiBaseUrl() + "/v1/check_transaction_by_md5";

        try {
            String requestJson = objectMapper.writeValueAsString(java.util.Map.of("md5", cleanMd5));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Authorization", "Bearer " + bakongConfig.getToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(12))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            log.info("[BakongService] check_transaction_by_md5 status: {}, body: {}", response.statusCode(), response.body());

            JsonNode root = objectMapper.readTree(response.body());
            int responseCode = root.path("responseCode").asInt(-1);
            String responseMessage = root.path("responseMessage").asText("Unknown response from Bakong");
            JsonNode dataNode = root.path("data");

            // responseCode == 0 means successfully paid on Bakong
            boolean isPaid = (responseCode == 0 && response.statusCode() == 200);

            return BakongCheckTransactionDto.builder()
                    .isPaid(isPaid)
                    .responseCode(responseCode)
                    .responseMessage(responseMessage)
                    .data(dataNode.isNull() ? null : dataNode)
                    .build();

        } catch (Exception e) {
            log.error("[BakongService] Error calling Bakong check_transaction_by_md5: {}", e.getMessage(), e);
            return BakongCheckTransactionDto.builder()
                    .isPaid(false)
                    .responseCode(500)
                    .responseMessage("Failed to connect to Bakong API: " + e.getMessage())
                    .build();
        }
    }

    private static String formatTag(String tag, String value) {
        if (value == null || value.isEmpty()) return "";
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return String.format("%02d%02d%s", Integer.parseInt(tag), bytes.length, value);
    }

    private static String calculateCRC16(String str) {
        int crc = 0xFFFF;
        int polynomial = 0x1021;
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i)) & 1) == 1;
                boolean c15 = ((crc >> 15) & 1) == 1;
                crc <<= 1;
                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
            }
        }
        crc &= 0xFFFF;
        return String.format("%04X", crc);
    }

    private static String calculateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}