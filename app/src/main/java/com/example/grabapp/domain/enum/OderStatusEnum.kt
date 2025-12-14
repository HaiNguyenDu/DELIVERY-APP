enum class OrderStatus(val label: String) {
    FINDING_DRIVER("Đang tìm tài xế"),
    DRIVER_ASSIGNED("Đã có tài xế nhận đơn"),
    DRIVER_EN_ROUTE_PICKUP("Tài xế đang đến điểm lấy"),
    ARRIVED_PICKUP("Tài xế đã đến điểm lấy"),
    PICKUP_ATTEMPT_FAILED("Không liên lạc được người gửi"),
    PICKUP_FAILED("Không lấy được hàng"),
    PACKAGE_PICKED("Đã nhận hàng"),
    EN_ROUTE_DELIVERY("Đang giao hàng"),
    ARRIVED_DELIVERY("Tài xế đã đến điểm giao"),
    DELIVERY_ATTEMPT_FAILED("Không liên lạc được người nhận"),
    DELIVERY_FAILED("Giao thất bại"),
    RETURNING_TO_SENDER("Đang hoàn trả"),
    RETURNED("Đã trả hàng lại"),
    DELIVERED("Giao thành công"),
    DRIVER_ISSUE_REPORTED("Shipper gặp sự cố"),
    REASSIGNING_DRIVER("Đang đổi tài xế"),
    CANCELLED_BY_SENDER("Người gửi hủy"),
    CANCELLED_BY_DRIVER("Tài xế hủy"),
    CANCELLED_NO_DRIVER("Hệ thống không tìm được tài xế"),
    ORDER_CANCELLED("Đơn bị hủy (trạng thái kết thúc)");
    fun isTerminal(): Boolean {
        return this == DELIVERED ||
                this == RETURNED ||
                this == ORDER_CANCELLED ||
                this == CANCELLED_NO_DRIVER ||
                this == CANCELLED_BY_SENDER ||
                this == CANCELLED_BY_DRIVER
    }
}
