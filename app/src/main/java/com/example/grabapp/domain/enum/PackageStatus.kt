package com.example.grabapp.domain.enum

enum class PackageStatus(val label:String) {

    WAITING_FOR_PICKUP("Chờ lấy hàng"),
    PICKED_UP("Đã lấy hàng"),

    PICKUP_ATTEMPT_FAILED("Không thể liên lạc người gửi"),
    PICKUP_FAILED("Lấy hàng thất bại"),

    WAITING_FOR_DELIVERY("Chờ giao hàng"),
    DELIVERY_IN_PROGRESS("Đang giao hàng"),
    DELIVERY_ATTEMPT_FAILED("Không thể liên lạc người nhận"),
    DELIVERY_FAILED("Giao hàng thất bại"),

    DELIVERED("Giao hàng thành công"),

    RETURNING("Đang hoàn trả"),
    RETURNED("Hoàn trả thành công"),

    CANCELLED("Đơn hàng đã bị hủy");

    companion object {

        private val allowedTransitions: Map<PackageStatus, Set<PackageStatus>> = mapOf(
            WAITING_FOR_PICKUP to setOf(
                PICKED_UP,
                PICKUP_ATTEMPT_FAILED,
                PICKUP_FAILED,
                CANCELLED
            ),

            PICKED_UP to setOf(
                WAITING_FOR_DELIVERY,
                DELIVERY_IN_PROGRESS,
                RETURNING,
                CANCELLED
            ),

            WAITING_FOR_DELIVERY to setOf(
                DELIVERY_IN_PROGRESS,
                DELIVERY_ATTEMPT_FAILED,
                DELIVERY_FAILED,
                CANCELLED
            ),

            DELIVERY_IN_PROGRESS to setOf(
                DELIVERED,
                DELIVERY_ATTEMPT_FAILED,
                DELIVERY_FAILED,
                RETURNING,
                CANCELLED
            ),

            DELIVERY_ATTEMPT_FAILED to setOf(
                DELIVERY_IN_PROGRESS,
                DELIVERY_FAILED,
                RETURNING,
                CANCELLED
            ),

            DELIVERY_FAILED to setOf(
                RETURNING,
                CANCELLED
            ),

            DELIVERED to emptySet(),   // terminal

            RETURNING to setOf(
                RETURNED,
                CANCELLED
            ),

            RETURNED to emptySet(),    // terminal

            PICKUP_ATTEMPT_FAILED to setOf(
                PICKUP_FAILED,
                CANCELLED
            ),

            PICKUP_FAILED to setOf(
                RETURNING,
                CANCELLED
            ),

            CANCELLED to emptySet()    // terminal
        )

        // Hàm kiểm tra có được phép chuyển hay không
        fun canTransition(from: PackageStatus, to: PackageStatus): Boolean {
            return allowedTransitions[from]?.contains(to) ?: false
        }

        // Hàm kiểm tra trạng thái đã kết thúc hay chưa (DELIVERED / RETURNED / CANCELLED)
        fun isTerminal(status: PackageStatus): Boolean {
            return allowedTransitions[status].isNullOrEmpty()
        }
    }
}