package kr.hqservice.project_x.practice.core.controller

import kr.hqservice.framework.global.core.component.Bean
import kr.hqservice.project_x.practice.core.data.PlayerStatData
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Bean
class StatDataController {
// 플레이어 uuid로 PlayerStatData(cash)를 저장
    private val statDataMap = ConcurrentHashMap<UUID, PlayerStatData>()
// uuid에 해당하는 플레이어의 PlayerStatData(cash)를 가져옴
    fun get(uuid: UUID): PlayerStatData {
        return statDataMap.computeIfAbsent(uuid) {
            PlayerStatData()
        }
    }
// 특정 플레이어(uuid)의 cash값을 amount만큼 증가시킴
    fun add(uuid: UUID, amount: Int) {
        get(uuid).cash += amount
    }
// 플레이어의 cash를 amount값만큼 차감함 금액 보유량에 따라 true/false 반환
    fun subtract(uuid: UUID, amount: Int): Boolean {
        val data = get(uuid)
        return if (data.cash >= amount) {
            data.cash -= amount
            true
        } else false
    }
}