package org.worklog.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("display_name")
    val displayName: String,
    val email: String,
    val phone: String,
    val gender: String,
    @SerialName("date_of_birth")
    val dateOfBirth: String,
    @SerialName("marital_status")
    val maritalStatus: String,
    // Contact Details (next of kin)
    @SerialName("next_of_kin_name")
    val nextOfKinName: String? = null,
    @SerialName("next_of_kin_relationship")
    val nextOfKinRelationship: String? = null,
    @SerialName("next_of_kin_phone")
    val nextOfKinPhone: String? = null,
    // Bank Details
    @SerialName("bank_acc_name")
    val bankAccName: String? = null,
    @SerialName("bank_acc_number")
    val bankAccNumber: String? = null,
    @SerialName("bank_routing_number")
    val bankRoutingNumber: String? = null,
    @SerialName("bank_address")
    val bankAddress: String? = null,
    // Employee Declaration
    @SerialName("declaration_signed")
    val declarationSigned: Boolean = false,
    @SerialName("declaration_signed_at")
    val declarationSignedAt: String? = null,
    // RTW / Documents
    @SerialName("ni_number")
    val niNumber: String? = null,
    @SerialName("passport_number")
    val passportNumber: String? = null,
    @SerialName("passport_expiry")
    val passportExpiry: String? = null,
    @SerialName("visa_number")
    val visaNumber: String? = null,
    @SerialName("visa_expiry")
    val visaExpiry: String? = null,
    @SerialName("rtw_status")
    val rtwStatus: String? = null,
    @SerialName("rtw_expiry")
    val rtwExpiry: String? = null,
    // Equality / Diversity / Inclusion
    val ethnicity: String? = null,
    val nationality: String? = null,
    val disability: String? = null,
    val religion: String? = null,
    @SerialName("sexual_orientation")
    val sexualOrientation: String? = null,
    // Vetting
    @SerialName("dbs_check_date")
    val dbsCheckDate: String? = null,
    @SerialName("dbs_certificate_number")
    val dbsCertificateNumber: String? = null,
    @SerialName("reference1_name")
    val reference1Name: String? = null,
    @SerialName("reference1_contact")
    val reference1Contact: String? = null,
    @SerialName("reference2_name")
    val reference2Name: String? = null,
    @SerialName("reference2_contact")
    val reference2Contact: String? = null
)
