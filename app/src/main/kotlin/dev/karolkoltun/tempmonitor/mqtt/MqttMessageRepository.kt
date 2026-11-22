package dev.karolkoltun.tempmonitor

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MqttMessageRepository : JpaRepository<MqttMessageEntity, Long>