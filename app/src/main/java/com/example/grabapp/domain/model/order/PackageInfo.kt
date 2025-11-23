package com.example.grabapp.domain.model.order

import android.net.Uri
import com.example.grabapp.domain.enum.PackageTypeEnum
import com.example.grabapp.domain.enum.SizeEnum

class PackageInfo(
    private val weight: Int,
    private val size: SizeEnum,
    private val typePackage: PackageTypeEnum,
    private val imageUri: Uri?
) {
    fun setWeight(weight: Int) {
        this.weight
    }

    fun setSize(size: SizeEnum) {
        this.size
    }

    fun setTypePackage(typePackage: String) {
        this.typePackage
    }

    fun getTypePackage(): PackageTypeEnum {
        return typePackage
    }

    fun getWeight(): Int {
        return weight
    }

    fun getSize(): SizeEnum {
        return size
    }

    fun getImageUri(): Uri? {
        return imageUri
    }

    override fun toString(): String {
        return "${size.name}-$weight kg-${typePackage.value}"
    }
}
