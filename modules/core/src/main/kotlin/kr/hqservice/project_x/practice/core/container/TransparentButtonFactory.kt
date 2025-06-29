package kr.hqservice.project_x.practice.core.container

import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.framework.inventory.button.HQButtonBuilder
import kr.hqservice.framework.inventory.container.HQContainer
import org.bukkit.Material
import org.bukkit.entity.Player

object TransparentButtonFactory {

    fun addTransparentButton(
        container: HQContainer,
        name: String,
        lore: List<String>,
        slots: List<Int>,
        onClick: (Player) -> Unit
    ) {
        HQButtonBuilder(Material.MAP)
            .setCustomModelData(1010)
            .setDisplayName(name.colorize())
            .setLore(lore.map { it.colorize() })
            .setClickFunction { event ->
                val player = event.getWhoClicked() as Player
                onClick(player)
            }
            .build()
            .setSlot(container, *slots.toIntArray())
    }
}