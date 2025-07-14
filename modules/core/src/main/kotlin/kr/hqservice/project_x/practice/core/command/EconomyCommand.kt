package kr.hqservice.project_x.practice.core.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.hqservice.framework.bukkit.core.coroutine.extension.BukkitAsync
import kr.hqservice.framework.command.Command
import kr.hqservice.framework.command.CommandExecutor
import kr.hqservice.project_x.practice.core.economy.EconomyServiceImpl
import kr.hqservice.project_x.practice.core.economy.database.WalletImpl
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

@Command(label = "돈")
class EconomyCommand(
    private val coroutineScope: CoroutineScope,
    private val economyServiceImpl: EconomyServiceImpl
) {
    @CommandExecutor(
        label = "수표"
    ) fun setCheque(sender: Player, type: String, value: Long) {
        val item = sender.inventory.itemInMainHand
        if (item.type.isAir) return sender.sendMessage("손에 든 아이템이 없습니다.")

        val meta = item.itemMeta!!
        // uppercase x, 한글 안 되고 / 규칙 있음 a~z 사이, 0~9사이, 영어 숫자 -_. 조합 가능
        meta.persistentDataContainer
            .set(NamespacedKey.fromString("start_test:cheque_type")!!, PersistentDataType.STRING, type)
        meta.persistentDataContainer
            .set(NamespacedKey.fromString("start_test:cheque_value")!!, PersistentDataType.LONG, value)

        item.itemMeta = meta

        sender.sendMessage("수표로 지정되었습니다.")
    }
    //아이템도 데이터로 저장 가능, 플레이어에게 뭔가를 넣을때도 persistentData


    @CommandExecutor(
        label = "지급"
    ) fun addMoney(sender: CommandSender, target: Player, type: String, value: Long) {
        coroutineScope.launch(Dispatchers.BukkitAsync) {
            economyServiceImpl.pushMoney(target.uniqueId, type, value)
            sender.sendMessage("${target.name} 에게  $type 가 $value 만큼 지급되었습니다.")
        }

    }

    @CommandExecutor(
        label = "차감"
    )
    fun takeMoney(sender: CommandSender, target: Player, type: String, value: Long) {
        coroutineScope.launch(Dispatchers.BukkitAsync) {
            val wallet = economyServiceImpl.findWallet(target.uniqueId)
            if (wallet == null) {
                sender.sendMessage("${target.name} 님의 지갑 정보가 없습니다.")
                return@launch
            }

            val current = wallet.getMoney(type)
            if (current < value) {
                sender.sendMessage("${target.name} 님의 보유 $type 보다 더 많은 금액입니다. (현재: 보유 $type : $current)")
                return@launch
            }

            // 음수로 넣어서 차감
            economyServiceImpl.pushMoney(target.uniqueId, type, -value)
            sender.sendMessage("${target.name} 님의 $type 가 $value 만큼 차감되었습니다.")
        }
    }

    @CommandExecutor(
        label = "확인"
    ) fun checkMoney(sender: CommandSender, target: Player, type: String?) {
        coroutineScope.launch(Dispatchers.BukkitAsync){
            val wallet = economyServiceImpl.findWallet(target.uniqueId)
            if (wallet == null) {
                sender.sendMessage("${target.name} 님의 지갑 정보가 없습니다.")
            } else {
                if (type == null) {
                    wallet as WalletImpl
                    sender.sendMessage("${target.name} 님의 지갑 전체 정보::")
                    wallet.getWalletMap().forEach { (type, value) ->
                        sender.sendMessage("$type : $value")

                    }
                } else {
                    sender.sendMessage("${target.name} 님의 $type : ${wallet.getMoney(type)}")
                }
            }
        }

    }

}