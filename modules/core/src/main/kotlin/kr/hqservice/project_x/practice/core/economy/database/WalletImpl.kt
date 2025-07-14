package kr.hqservice.project_x.practice.core.economy.database
import kr.hqservice.project_x.practice.api.economy.Wallet
import java.util.*


// implementation -> 구현체
class WalletImpl(
    private val ownerId: UUID,
    private val walletMap: Map<String, Long>
) : Wallet {

    override fun getOwner(): UUID {
        return ownerId
    }

    override fun getMoney(key: String): Long {
        return walletMap[key] ?: 0L
    }
    fun getWalletMap() = walletMap
}