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
import org.bukkit.inventory.ItemStack

//캐시 재화 가구 상점

class FurnitureShopContainer(
    private val player: Player,
    private val furniturePage: Int = 101, // 페이지 번호 101부터 시작하여 UserInterface와의 충돌 방지
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

        // 페이지 전환 버튼 배치
        val nextPageSlots = listOf(8, 17, 26)
        val prevPageSlots = listOf(35, 44, 53)
        // 첫 페이지에서는 양쪽에 다음 페이지 버튼만 표시
        if (furniturePage == 101) {
            (nextPageSlots + prevPageSlots).forEach { addNextPageButton(it) }
        } else {
            nextPageSlots.forEach { addNextPageButton(it) }
            prevPageSlots.forEach { addPreviousPageButton(it) }
        }
    }

    private fun getItemsForPage(page: Int): Map<String, Pair<Int, String>> {
        return when (page) {
            101 -> mapOf(
                "정원 벤치" to (500 to "garden_arch"),
                "정원 덤불" to (200 to "garden_bush"),
                "정원 울타리" to (200 to "garden_fence"),
                "정원 기둥" to (400 to "garden_pillar"),
                "정원 받침대" to (400 to "garden_stand"),
                "정원 벤치2" to (400 to "garden_bench"),
                "정원 가로등" to (400 to "garden_lamppost"),
                "정원 덩굴장식" to (400 to "garden_trellis"),
                "정원 의자" to (400 to "garden_chair"),
                "정원 탁자" to (400 to "garden_table"),
                "정원 분수대" to (1000 to "garden_fountain"),
                "정원 나무" to (400 to "garden_tree"),
                "꽃 피는 정원 나무" to (400 to "flowering_garden_tree"),
                "망가진 정원 기둥" to (400 to "ruined_garden_pillar"),
                "정원 수레" to (400 to "garden_cart"),
                "정원 도구" to (400 to "garden_tools"),
                "물뿌리개" to (400 to "watering_can"),
                "연금술 작업대" to (400 to "alchemy_station"),
                "포션 스탠드" to (400 to "brewing_stand"),
                "포션 작업대" to (400 to "brewing_table"),
                "약초 벽 선반" to (400 to "herb_wall_rack"),
                "포션 선반" to (400 to "potion_shelf")
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
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_ARMOR_TRIM,
            ItemFlag.HIDE_DYE)

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
                    val currentCash = wallet.getMoney("cash")
                    if (currentCash < cost) {
                        clicker.sendMessage("§c캐시가 부족합니다!")
                        return@launch
                    }
                    if (clicker.inventory.firstEmpty() == -1) {
                        clicker.sendMessage("§c인벤토리에 빈 공간이 없습니다!")
                        return@launch
                    }

                    val success = economyServiceImpl.pushMoney(clicker.uniqueId, "cash", -cost.toLong())
                    if (wallet.getMoney("cash") < cost) {
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


    /** 다음 페이지 버튼 */
    private fun addNextPageButton(slot: Int) {
        HQButtonBuilder(Material.MAP)
            .setCustomModelData(1010)
            .setDisplayName("&7버튼을 누르면 다음 페이지로 넘어갑니다.".colorize())
            .setClickFunction {
                player.closeInventory()
                FurnitureShopContainer(player, furniturePage + 1, coroutineScope, economyServiceImpl).open(player)

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
                FurnitureShopContainer(player, furniturePage - 1, coroutineScope, economyServiceImpl).open(player)

            }
            .build()
            .setSlot(this, slot)
    }
}