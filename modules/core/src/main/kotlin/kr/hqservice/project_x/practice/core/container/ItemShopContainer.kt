package kr.hqservice.project_x.practice.core.container
import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.framework.inventory.button.HQButtonBuilder
import kr.hqservice.framework.inventory.container.HQContainer
import kr.hqservice.project_x.practice.core.command.UserInterfaceTitleCommand.Companion.getTitle
import kr.hqservice.project_x.practice.core.controller.StatDataController
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


/**
 * 아이템 상점 GUI 클래스.
 * 무기류 아이템을 캐시로 구매할 수 있는 페이지.
 */

class ItemShopContainer(
    private val player: Player,
    private val furniture_page: Int = 201
) : HQContainer(54, getTitle(player, furniture_page).colorize()), KoinComponent {

    // Koin으로부터 StatDataController 주입 (캐시 관리)
    private val statDataController: StatDataController by inject()

    override fun initialize(inventory: Inventory) {
        val items = getItemsForPage(furniture_page)

        var slotIndex = 0
        for ((displayName, itemInfo) in items) {
            val (material, modelData, price, itemId) = itemInfo
            addFurnitureButton(
                name = displayName,
                material = material,
                cost = price,
                itemId = itemId,
                slot = slotIndex++,
                modelData = modelData
            )
        }
// 페이지 아이템을 순서대로 배치
        val nextPageSlots = listOf(8, 17, 26)
        val prevPageSlots = listOf(35, 44, 53)

        if (furniture_page == 201) {
            val firstPageButtons = nextPageSlots + prevPageSlots
            firstPageButtons.forEach { slot ->
                addNextPageButton(slot)
            }
        } else {
            nextPageSlots.forEach { addNextPageButton(it) }
            prevPageSlots.forEach { addPreviousPageButton(it) }
        }
    }
    /**
     * 현재 페이지에 맞는 아이템 목록 리턴
     */
    private fun getItemsForPage(furniture_page: Int): Map<String, ItemData> {
        return when (furniture_page) {
            201 -> mapOf(
                "배틀 액스" to ItemData(Material.IRON_SWORD, 1000, 500, "battleaxe"),
                "클레이모어" to ItemData(Material.IRON_SWORD, 1001, 500, "claymore"),
                "대거" to ItemData(Material.IRON_SWORD, 1002, 500, "dagger"),
                "그레이트 액스" to ItemData(Material.IRON_SWORD, 1003, 500, "greataxe"),
                "그레이트 소드" to ItemData(Material.IRON_SWORD, 1004, 500, "greatsword"),
                "레이피어" to ItemData(Material.IRON_SWORD, 1005, 500, "rapier"),
                "시미터" to ItemData(Material.IRON_SWORD, 1006, 500, "scimitar"),
                "유라쿠모" to ItemData(Material.IRON_SWORD, 1007, 500, "urakumo"),
                "스트레이트 소드" to ItemData(Material.IRON_SWORD, 1008, 500, "straightsword"),
                "우치가타나" to ItemData(Material.IRON_SWORD, 1009, 500, "uchigatana")
            )
            else -> emptyMap()
        }
    }
    /**
     * 실제 아이템 버튼 생성 및 클릭 이벤트 처리
     */
    private fun addFurnitureButton(
        name: String,
        material: Material,
        cost: Int,
        itemId: String,
        slot: Int,
        modelData: Int
    ) {
        val itemStack = ItemStack(material)
        val meta = itemStack.itemMeta
        if (meta != null) {
            meta.setDisplayName("§e$name (§f$cost 캐시§e)")
            meta.lore = listOf("§7클릭하여 구매합니다.")
            meta.setCustomModelData(modelData)

            //툴팁 숨기기
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

            // 속성 제거 (공격력 등)
            try {
                val emptyModifiers = com.google.common.collect.HashMultimap.create<org.bukkit.attribute.Attribute, org.bukkit.attribute.AttributeModifier>()
                meta.attributeModifiers = emptyModifiers
            } catch (e: Exception) {
                Bukkit.getLogger().warning("속성 제거 실패: ${e.message}")
            }

            itemStack.itemMeta = meta
        }

        HQButtonBuilder(itemStack)
            .setClickFunction { event ->
                val player = event.getWhoClicked() as Player
                val uuid = player.uniqueId

                val data = statDataController.get(uuid)
                if (data.cash >= cost) {
                    if (player.inventory.firstEmpty() != -1) {
                        data.cash -= cost
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nexo give ${player.name} ${itemId} 1")
                        player.sendMessage("§a$name 을(를) 구매했습니다!")
                    } else {
                        player.sendMessage("§c인벤토리에 빈 공간이 없습니다!")
                        player.sendMessage("§7캐시는 차감되지 않았습니다.")
                    }
                } else {
                    player.sendMessage("§c캐시가 부족합니다!")
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
                FurnitureShopContainer(player, furniture_page + 1).open(player)
            }
            .build()
            .setSlot(this, slot)
    }
    /** 이전 페이지 버튼 */
    private fun addPreviousPageButton(slot: Int) {
        HQButtonBuilder(Material.MAP)
            .setCustomModelData(1010)
            .setDisplayName("&7버튼을 누르면 이전 페이지로 넘어갑니다.".colorize())
            .setClickFunction {
                player.closeInventory()
                FurnitureShopContainer(player, furniture_page - 1).open(player)
            }
            .build()
            .setSlot(this, slot)
    }
    /**
     * GUI에 표시할 아이템 정보 데이터 클래스
     */
    data class ItemData(
        val material: Material,
        val modelData: Int,
        val price: Int,
        val itemId: String
    )
}
