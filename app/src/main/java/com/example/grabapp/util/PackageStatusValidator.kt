package com.example.grabapp.util

import com.example.grabapp.model.PackageStatus

object PackageStatusValidator {
    private val validTransitions: Map<PackageStatus, Set<PackageStatus>> = mapOf(
        PackageStatus.WAITING_FOR_PICKUP to setOf(
            PackageStatus.PICKED_UP,
            PackageStatus.PICKUP_ATTEMPT_FAILED,
            PackageStatus.PICKUP_FAILED,
            PackageStatus.CANCELLED
        ),
        PackageStatus.PICKED_UP to setOf(
            PackageStatus.WAITING_FOR_DELIVERY,
            PackageStatus.DELIVERY_IN_PROGRESS,
            PackageStatus.RETURNING,
            PackageStatus.CANCELLED
        ),
        PackageStatus.WAITING_FOR_DELIVERY to setOf(
            PackageStatus.DELIVERY_IN_PROGRESS,
            PackageStatus.DELIVERY_ATTEMPT_FAILED,
            PackageStatus.DELIVERY_FAILED,
            PackageStatus.CANCELLED
        ),
        PackageStatus.DELIVERY_IN_PROGRESS to setOf(
            PackageStatus.DELIVERED,
            PackageStatus.DELIVERY_ATTEMPT_FAILED,
            PackageStatus.DELIVERY_FAILED,
            PackageStatus.RETURNING,
            PackageStatus.CANCELLED
        ),
        PackageStatus.DELIVERY_ATTEMPT_FAILED to setOf(
            PackageStatus.DELIVERY_IN_PROGRESS,
            PackageStatus.DELIVERY_FAILED,
            PackageStatus.RETURNING,
            PackageStatus.CANCELLED
        ),
        PackageStatus.DELIVERY_FAILED to setOf(
            PackageStatus.RETURNING,
            PackageStatus.CANCELLED
        ),
        PackageStatus.DELIVERED to emptySet(), // terminal
        PackageStatus.RETURNING to setOf(
            PackageStatus.RETURNED,
            PackageStatus.CANCELLED
        ),
        PackageStatus.RETURNED to emptySet(), // terminal
        PackageStatus.PICKUP_ATTEMPT_FAILED to setOf(
            PackageStatus.PICKUP_FAILED,
            PackageStatus.CANCELLED
        ),
        PackageStatus.PICKUP_FAILED to setOf(
            PackageStatus.RETURNING,
            PackageStatus.CANCELLED
        ),
        PackageStatus.CANCELLED to emptySet() // terminal
    )

    fun isValidTransition(from: PackageStatus, to: PackageStatus): Boolean {
        return validTransitions[from]?.contains(to) ?: false
    }

    fun getValidNextStatuses(current: PackageStatus): Set<PackageStatus> {
        return validTransitions[current] ?: emptySet()
    }
}
