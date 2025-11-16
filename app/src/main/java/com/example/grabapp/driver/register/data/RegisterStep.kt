package com.example.grabapp.driver.register.data

import androidx.fragment.app.Fragment
import com.example.grabapp.driver.register.avatar.AvatarFragment
import com.example.grabapp.driver.register.bank_account.BankAccountFragment
import com.example.grabapp.driver.register.driving_license.DrivingLicenseFragment
import com.example.grabapp.driver.register.emergency_contact.EmergencyContactFragment
import com.example.grabapp.driver.register.identification.IdentificationCardFragment
import com.example.grabapp.driver.register.vehicle_confirm.VehicleConfirmFragment
import com.example.grabapp.driver.register.vehicle_insurance.VehicleInsuranceFragment
import com.example.grabapp.driver.register.vehicle_registration.VehicleRegistrationFragment

enum class RegisterStep(
    val createFragment: () -> Fragment
) {
    AVATAR({ AvatarFragment() }),
    IDENTIFICATION_CARD({ IdentificationCardFragment() }),
    DRIVING_LICENSE({ DrivingLicenseFragment() }),
    EMERGENCY_CONTACT({ EmergencyContactFragment() }),
    BANK_ACCOUNT({ BankAccountFragment() }),
    VEHICLE_CONFIRM({ VehicleConfirmFragment() }),
    VEHICLE_REGISTRATION({ VehicleRegistrationFragment() }),
    VEHICLE_INSURANCE({ VehicleInsuranceFragment() });

    fun getNextStep(): RegisterStep? {
        return entries.getOrNull(ordinal + 1)
    }

    fun getPreviousStep(): RegisterStep? {
        return entries.getOrNull(ordinal - 1)
    }

    fun isFirstStep(): Boolean = ordinal == 0
    fun isLastStep(): Boolean = ordinal == entries.size - 1
}
