package kr.hqservice.project_x.practice.core.container

import com.nexomc.nexo.api.NexoItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.hqservice.framework.bukkit.core.coroutine.extension.BukkitAsync
import kr.hqservice.framework.bukkit.core.coroutine.extension.BukkitMain
import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.framework.inventory.button.HQButtonBuilder
import kr.hqservice.framework.inventory.container.HQContainer
import kr.hqservice.project_x.practice.core.command.UserInterfaceTitleCommand.Companion.getTitle
import kr.hqservice.project_x.practice.core.economy.EconomyServiceImpl
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag

//골드 재화 상점

class ItemShopContainer(
    private val player: Player,
    private val furniturePage: Int = 201,
    private val coroutineScope: CoroutineScope,
    private val economyServiceImpl: EconomyServiceImpl
) : HQContainer(54, getTitle(player, furniturePage).colorize()) {

    override fun initialize(inventory: Inventory) {
        val items = getItemsForPage(furniturePage)

        var slotIndex = 0
        for ((_, itemInfo) in items) {
            val (price, itemId) = itemInfo
            addFurnitureButton(price, itemId, slotIndex++)
        }

        val nextPageSlots = listOf(8, 17, 26)
        val prevPageSlots = listOf(35, 44, 53)

        if (furniturePage == 201) {
            (nextPageSlots + prevPageSlots).forEach { addNextPageButton(it) }
        } else {
            nextPageSlots.forEach { addNextPageButton(it) }
            prevPageSlots.forEach { addPreviousPageButton(it) }
        }
    }

    private fun getItemsForPage(page: Int): Map<String, Pair<Int, String>> {
        return when (page) {
            201 -> mapOf(
                "배틀 액스" to (500 to "battleaxe"),
                "클레이모어" to (500 to "claymore"),
                "대거" to (500 to "dagger"),
                "그레이트 액스" to (500 to "greataxe"),
                "그레이트 소드" to (500 to "greatsword"),
                "레이피어" to (500 to "rapier"),
                "시미터" to (500 to "scimitar"),
                "유라쿠모" to (500 to "urakumo"),
                "스트레이트 소드" to (500 to "straightsword"),
                "우치가타나" to (500 to "uchigatana")
            )
            else -> emptyMap()
        }
    }

    private fun addFurnitureButton(
        cost: Int,
        itemId: String,
        slot: Int
    ) {
        val baseItem = NexoItems.itemFromId(itemId)?.build() ?: return
        val meta = baseItem.itemMeta ?: return

        meta.setDisplayName(null)
        val originalLore = meta.lore ?: emptyList()
        meta.lore = originalLore + "§7클릭하여 구매합니다."
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        baseItem.itemMeta = meta

        HQButtonBuilder(baseItem)
            .setClickFunction { event ->
                val clicker = event.getWhoClicked() as Player
                coroutineScope.launch(Dispatchers.BukkitAsync) {
                    val wallet = economyServiceImpl.findWallet(clicker.uniqueId)
                    if (wallet == null) {
                        clicker.sendMessage("§c지갑 정보를 불러오지 못했습니다.")
                        return@launch
                    }
                    val currentGold = wallet.getMoney("gold")
                    if (currentGold < cost) {
                        clicker.sendMessage("§c골드가 부족합니다!")
                        return@launch
                    }
                    if (clicker.inventory.firstEmpty() == -1) {
                        clicker.sendMessage("§c인벤토리에 빈 공간이 없습니다!")
                        return@launch
                    }

                    val success = economyServiceImpl.pushMoney(clicker.uniqueId, "gold", -cost.toLong())
                    if (wallet.getMoney("gold") < cost) {
                        clicker.sendMessage("§c잔액 차감에 실패했습니다.")
                        return@launch
                    }

                    // 동기 명령 실행은 메인 스레드에서 실행
                    coroutineScope.launch(Dispatchers.BukkitMain) {
                        clicker.server.dispatchCommand(clicker.server.consoleSender, "nexo give ${clicker.name} $itemId 1")
                        clicker.sendMessage("§a아이템을 구매했습니다!")
                    }
                }
            }
            .build()
            .setSlot(this, slot)
    }

    private fun addNextPageButton(slot: Int) {
        HQButtonBuilder(Material.MAP)
            .setCustomModelData(1010)
            .setDisplayName("&7버튼을 누르면 다음 페이지로 넘어갑니다.".colorize())
            .setClickFunction {
                player.closeInventory()
                ItemShopContainer(player, furniturePage + 1, coroutineScope, economyServiceImpl).open(player)
            }
            .build()
            .setSlot(this, slot)
    }

    private fun addPreviousPageButton(slot: Int) {
        HQButtonBuilder(Material.MAP)
            .setCustomModelData(1010)
            .setDisplayName("&7버튼을 누르면 이전 페이지로 넘어갑니다.".colorize())
            .setClickFunction {
                player.closeInventory()
                ItemShopContainer(player, furniturePage - 1, coroutineScope, economyServiceImpl).open(player)
            }
            .build()
            .setSlot(this, slot)
    }
}
