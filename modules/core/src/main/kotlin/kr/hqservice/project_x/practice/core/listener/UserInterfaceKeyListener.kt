
package kr.hqservice.project_x.practice.core.listener

import kr.hqservice.framework.bukkit.core.listener.Listener
import kr.hqservice.framework.bukkit.core.listener.Subscribe
import kr.hqservice.projectx.practice.core.container.UserInterface
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.player.PlayerToggleSneakEvent


@Listener
class UserInterfaceKeyListener {

    private val sneakingPlayers = mutableSetOf<Player>()

    @Subscribe
    fun onSneak(event: PlayerToggleSneakEvent) {
        if (event.isSneaking) {
            sneakingPlayers.add(event.player)
        } else {
            sneakingPlayers.remove(event.player)
        }
    }

    @Subscribe
    fun onSwapHand(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        event.isCancelled = true
        if (sneakingPlayers.contains(player)) {
            UserInterface(player = player).open(player)
        }
    }
}