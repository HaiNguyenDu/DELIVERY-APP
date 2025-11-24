package com.example.grabapp.domain.enum

enum class PackageTypeEnum(val value: String) {
    THOI_TRANG("Thời trang"),
    TRANG_SUC("Trang sức"),
    MY_PHAM("Mỹ phẩm"),
    NUOC_HOA("Nước hoa"),
    THUC_PHAM_KHO("Thực phẩm khô"),
    THUC_PHAM_TUOI("Thực phẩm tươi sống"),
    DO_UONG("Đồ uống"),
    DO_AN_NHAU("Đồ ăn / Đồ nhậu"),
    DIEN_TU("Điện tử"),
    PHU_KIEN_CONG_NGHE("Phụ kiện công nghệ"),
    GIA_DUNG("Đồ gia dụng"),
    NOI_THAT_NHO("Nội thất nhỏ"),
    TAI_LIEU("Tài liệu / Giấy tờ"),
    SACH_VO("Sách vở"),
    DE_VO("Hàng dễ vỡ"),
    HANG_NANG("Hàng nặng"),
    HANG_CUC_LON("Hàng cồng kềnh / Cực lớn"),
    HOA_CHAT("Hóa chất"),
    DE_CHAY_NO("Dễ cháy nổ"),
    THUOC("Thuốc / Dược phẩm"),
    THU_CUNG("Thú cưng"),
    QUA_TANG("Quà tặng"),
    KHAC("Khác"),
    DEFAULT("Mặc định");

    companion object {
        fun getPackageTypeByDisplayValue(text: String): PackageTypeEnum {
            return entries.find { it.value.equals(text, ignoreCase = true) } ?: DEFAULT
        }

        fun getPackageTypeByName(name: String): PackageTypeEnum {
            return try {
                valueOf(name)
            } catch (e: IllegalArgumentException) {
                DEFAULT
            }
        }
    }
}