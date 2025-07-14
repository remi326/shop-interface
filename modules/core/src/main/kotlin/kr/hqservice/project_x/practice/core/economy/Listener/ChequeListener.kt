package kr.hqservice.project_x.practice.core.economy.Listener

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.hqservice.framework.bukkit.core.coroutine.extension.BukkitAsync
import kr.hqservice.framework.bukkit.core.listener.Listener
import kr.hqservice.framework.bukkit.core.listener.Subscribe
import kr.hqservice.project_x.practice.core.economy.EconomyServiceImpl
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType

@Listener
class ChequeListener(
    private val coroutineScope: CoroutineScope,
    private val economyServiceImpl: EconomyServiceImpl
) {

    @Subscribe
    fun onInteractItem(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            val player = event.player
            val item = player.inventory.itemInMainHand
            if (item.type.isAir) return

            val meta = item.itemMeta!!
            val pdc = meta.persistentDataContainer
            val typeKey = NamespacedKey.fromString("start_test:cheque_type")!!
            val valueKey = NamespacedKey.fromString("start_test:cheque_value")!!
            if (!pdc.has(typeKey, PersistentDataType.STRING)) return

            event.isCancelled = true
            val type = pdc.get(typeKey, PersistentDataType.STRING)!!
            val value = pdc.get(valueKey, PersistentDataType.LONG)!!

            coroutineScope.launch(Dispatchers.BukkitAsync) {
                economyServiceImpl.pushMoney(player.uniqueId, type, value)
                player.sendMessage("$type 의 수치가 $value 만큼 올랐습니다.")
                player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.7f)
                item.amount--
            }
        }
    }
}