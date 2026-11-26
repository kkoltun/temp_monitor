package dev.karolkoltun.tempmonitor.synoptic

import dev.karolkoltun.tempmonitor.common.SynopticResponseEntity
import org.springframework.data.repository.CrudRepository

interface SynopticRepository : CrudRepository<SynopticResponseEntity, Long>