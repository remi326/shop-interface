package kr.hqservice.project_x.practice.core.economy

import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.project_x.practice.api.economy.EconomyService
import kr.hqservice.project_x.practice.api.economy.Wallet
import kr.hqservice.project_x.practice.core.economy.database.EconomyRepository
import kr.hqservice.project_x.practice.core.economy.database.WalletImpl
import java.util.*

@Service
class EconomyServiceImpl(
    private val economyRepository: EconomyRepository
) : EconomyService {
    override suspend fun findWallet(playerId: UUID): Wallet? {
        val entity = economyRepository.findWallet(playerId) ?: return null
        // gold:1000,
        // cash:200,
        // point:300
        val walletDataes = entity.walletData.split(",")
        val walletMap = walletDataes.associate {
            val args = it.split(":")
            args[0] to args[1].toLong()
                //키 밸류인 맵으로 바꾸란 것
        }
        return WalletImpl(playerId, walletMap)
    }
    suspend fun pushMoney(playerId: UUID, moneyType: String, value: Long){
        economyRepository.findWalletForUpdate(playerId) { wallet, isCreated ->
            if (isCreated) {
                "$moneyType:$value"
            } else {
                val walletDataes = wallet.walletData.split(",")
                val walletMap = walletDataes.associate {
                    val args = it.split(":")
                    args[0] to args[1].toLong()
                    //키 밸류인 맵으로 바꾸란 것
                }
                val wrap =  WalletImpl(playerId, walletMap)

                val map = wrap.getWalletMap().toMutableMap()
                map[moneyType] = (map[moneyType] ?: 0) + value
                // Map<String, Long> <----- 원래 맵을 가져옴
                // -> List<Map.Entry<String, Long>>  <--- entries로 바꾸면 이렇게 됨
                // -> Mapping -> "key:value" -> List<String> <------- 매핑을 하면 이렇게 List<String>으로 됨
                // -> joinToString -> "key:value,key:value,key:value"
                //1. 맵을 먼저 엔트리 리스트로 변환 2. 그 다음에 "키:밸류"를 가진 스트링으로 맵핑 3.

                map
                    .entries
                    .map { it.key + ":" + it.value }
                    .joinToString(",")
            //List<String의 사이사이에 컴마를 끼워서 스트링으로 바꿔라 >
            }

        }


        //economyRepository.pushWalletData(playerId, walletData)
    }
    suspend fun create(playerId: UUID) {
        findWallet(playerId) ?: economyRepository.registerWallet(playerId)
    }
}