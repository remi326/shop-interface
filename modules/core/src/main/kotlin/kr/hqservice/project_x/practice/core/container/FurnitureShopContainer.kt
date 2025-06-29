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
 * 가구 상점 GUI 컨테이너
 * - HQContainer를 상속하여 54칸 GUI 생성
 * - furniture_page를 101부터 시작하여 다른 GUI와 충돌 방지
 * - Koin을 통해 StatDataController 주입
 */

class FurnitureShopContainer(
    private val player: Player,
    private val furniture_page: Int = 101 // 페이지 번호 101부터 시작하여 UserInterface와의 충돌 방지
) : HQContainer(54, getTitle(player, furniture_page).colorize()), KoinComponent {

    private val statDataController: StatDataController by inject()

    override fun initialize(inventory: Inventory) {
        val items = getItemsForPage(furniture_page)
        // 페이지 아이템을 순서대로 배치
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
// 페이지 전환 버튼 배치
        val nextPageSlots = listOf(8, 17, 26)
        val prevPageSlots = listOf(35, 44, 53)
        // 첫 페이지에서는 양쪽에 다음 페이지 버튼만 표시
        if (furniture_page == 101) {
            val firstPageButtons = nextPageSlots + prevPageSlots
            firstPageButtons.forEach { slot ->
                addNextPageButton(slot)
            }
        } else {
            // 그 외 페이지에서는 양방향 네비게이션
            nextPageSlots.forEach { addNextPageButton(it) }
            prevPageSlots.forEach { addPreviousPageButton(it) }
        }
    }

    /**
     * 페이지별 아이템 설정
     * - key: GUI에 표시될 이름
     * - value: Material, 모델 데이터, 가격, Nexo itemId
     */
    private fun getItemsForPage(furniture_page: Int): Map<String, ItemData> {
        return when (furniture_page) {
            101 -> mapOf(
                "정원 아치" to ItemData(Material.LEATHER_HORSE_ARMOR, 5301, 500, "garden_arch"),
                "정원 덤불" to ItemData(Material.LEATHER_HORSE_ARMOR, 5302, 200, "garden_bush"),
                "정원 울타리" to ItemData(Material.LEATHER_HORSE_ARMOR, 5303, 200, "garden_fence"),
                "이오니아 기둥" to ItemData(Material.LEATHER_HORSE_ARMOR, 5304, 400, "garden_pillar"),
                "이오니아 기둥 조각" to ItemData(Material.LEATHER_HORSE_ARMOR, 5305, 400, "garden_stand"),
                "정원 벤치" to ItemData(Material.LEATHER_HORSE_ARMOR, 5306, 400, "garden_bench"),
                "정원 가로등" to ItemData(Material.LEATHER_HORSE_ARMOR, 5307, 400, "garden_lamppost"),
                "정원 트레일" to ItemData(Material.LEATHER_HORSE_ARMOR, 5308, 400, "garden_trellis"),
                "정원 의자" to ItemData(Material.LEATHER_HORSE_ARMOR, 5309, 400, "garden_chair"),
                "정원 테이블" to ItemData(Material.LEATHER_HORSE_ARMOR, 5310, 400, "garden_table"),
                "정원 분수" to ItemData(Material.LEATHER_HORSE_ARMOR, 5311, 1000, "garden_fountain"),
                "정원 나무" to ItemData(Material.LEATHER_HORSE_ARMOR, 5312, 400, "garden_tree"),
                "꽃 핀 정원 나무" to ItemData(Material.LEATHER_HORSE_ARMOR, 5313, 400, "flowering_garden_tree"),
                "부숴진 이오니아 기둥" to ItemData(Material.LEATHER_HORSE_ARMOR, 5314, 400, "ruined_garden_pillar"),
                "정원용 수레" to ItemData(Material.LEATHER_HORSE_ARMOR, 5315, 400, "garden_cart"),
                "정원 툴" to ItemData(Material.LEATHER_HORSE_ARMOR, 5316, 400, "garden_tools"),
                "정원 물뿌리개" to ItemData(Material.LEATHER_HORSE_ARMOR, 5317, 400, "watering_can"),
                "연금술 테이블" to ItemData(Material.PAPER, 1034, 400, "alchemy_station"),
                "연금술 장치" to ItemData(Material.PAPER, 1035, 400, "brewing_stand"),
                "증류대" to ItemData(Material.PAPER, 1036, 400, "brewing_table"),
                "연금술사의 벽걸이" to ItemData(Material.PAPER, 1037, 400, "herb_wall_rack"),
                "포션 선반" to ItemData(Material.PAPER, 1038, 400, "potion_shelf")
            )
            else -> emptyMap()
        }
    }

    /**
     * 가구 아이템 버튼 추가
     * - 커스텀 모델 및 툴팁 제거 포함
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
                        // 공간 있을 경우만 구매 진행
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