package dev.karolkoltun.tempmonitor.processing

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "device_location")
class DeviceLocationEntity(
    @Id @GeneratedValue(strategy = IDENTITY) var id: Long? = null,
    var device: String,
    var location: String
)