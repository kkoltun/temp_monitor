package dev.karolkoltun.tempmonitor.processing

import org.springframework.data.repository.Repository

interface DeviceLocationRepository : Repository<DeviceLocationEntity, Long> {

    fun findByDevice(device: String): DeviceLocation?

}