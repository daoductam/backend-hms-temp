# PaymentMS (Spring Boot)

Module PaymentMS chuyển từ Node PaymentService sang Spring Boot.

## Build & run

```bash
mvn spring-boot:run
```

App chạy mặc định ở port `8085` với H2 in-memory (đổi sang DB thật trong `application.yml`).

Các endpoint chính:

- `POST /create-link`
- `POST /momo-callback`
- `GET /vnpay-callback`
- `GET /status/{orderId}`
```
