package com.example.grabapp.model

import android.net.Uri

class PackageInfo(
    private val weight: Int,
    private val size: SizeEnum,
    private val typePackage: PackageType,
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

    fun getTypePackage(): PackageType {
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
