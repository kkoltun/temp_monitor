package dev.karolkoltun.tempmonitor.processing

import org.springframework.data.jpa.repository.JpaRepository

interface ReadingRepository : JpaRepository<ReadingEntity, Long>