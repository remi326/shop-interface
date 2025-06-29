package kr.hqservice.projectx.practice.core.container

import kr.hqservice.framework.bukkit.core.extension.colorize
import kr.hqservice.framework.inventory.button.HQButtonBuilder
import kr.hqservice.framework.inventory.container.HQContainer
import kr.hqservice.project_x.practice.core.command.UserInterfaceTitleCommand
import kr.hqservice.project_x.practice.core.container.TransparentButtonFactory
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

//통합인터페이스. Shift+F
class UserInterface(
    private val page: Int = 1,
    private val player: Player
) : HQContainer(54, UserInterfaceTitleCommand.getTitle(player, page).colorize()) {

    override fun initialize(inventory: Inventory) {
        when (page) {
            1 -> renderFirstPage()
            2 -> renderSecondPage()
            3 -> renderThirdPage()
            4 -> renderEncyclopediaPage()
        }
    }

    private fun renderFirstPage() {
        setTransparentMenuButton("캐릭터 정보", listOf(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21))
        setTransparentMenuButton("스킬 정보", listOf(4, 5, 6, 7, 13, 14, 15, 16, 22, 23, 24, 25))
        setTransparentMenuButton("파티", listOf(27, 28, 36, 37, 45, 46))
        setTransparentMenuButton("길드", listOf(29, 30, 38, 39, 47, 48))
        setEncyclopediaButton(listOf(31, 32, 33, 34, 40, 41, 42, 43, 49, 50, 51, 52))
        setTransparentPageButton(2, listOf(8, 17, 26, 35, 44, 53))
    }

    private fun renderSecondPage() {
        setTransparentMenuButton("워프", listOf(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21))
        setTransparentMenuButton("퀘스트", listOf(4, 5, 6, 7, 13, 14, 15, 16, 22, 23, 24, 25))
        setTransparentMenuButton("거래", listOf(27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48))
        setTransparentMenuButton("지도", listOf(31, 32, 33, 34, 40, 41, 42, 43, 49, 50, 51, 52))
        setTransparentPageButton(3, listOf(8, 17, 26))
        setTransparentPageButton(1, listOf(35, 44, 53))
    }

    private fun renderThirdPage() {
        setTransparentMenuButton("캐시샵", listOf(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21))
        setTransparentMenuButton("이벤트 패스", listOf(4, 5, 6, 7, 13, 14, 15, 16, 22, 23, 24, 25))
        setTransparentMenuButton("접속 보상", listOf(27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48))
        setTransparentLinkButton("디스코드", "https://discord.gg/link", listOf(31, 32))
        setTransparentLinkButton("깃북", "https://your.gitbook.io", listOf(33, 34))
        setTransparentLinkButton("공식 홈페이지", "https://homepage.com", listOf(40, 41))
        setTransparentLinkButton("추천 링크", "https://referral.com", listOf(42, 43))
        setTransparentLinkButton("후원하기", "https://donate.com", listOf(49, 50))
        setTransparentLinkButton("고객 지원", "https://support.com", listOf(51, 52))
        setTransparentPageButton(1, listOf(8, 17, 26))
        setTransparentPageButton(2, listOf(35, 44, 53))
    }

    private fun renderEncyclopediaPage() {
        // 도감 내용 구현 가능
        HQButtonBuilder(Material.BARRIER)
            .setDisplayName("&c돌아가기".colorize())
            .setLore(listOf("&7이전 페이지로 돌아갑니다.".colorize()))
            .setClickFunction {
                player.closeInventory()
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(this::class.java), Runnable {
                    UserInterface(1, player).open(player)
                }, 1L)
            }
            .build().setSlot(this, 49)
    }



    private fun setTransparentMenuButton(name: String, slots: List<Int>) {
        TransparentButtonFactory.addTransparentButton(
            container = this,
            name = "&a$name",
            lore = listOf("&7클릭 시 &f$name 메뉴로 이동합니다."),
            slots = slots
        ) { player ->
            player.closeInventory()
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(this::class.java), Runnable {
                if (name == "캐시샵") {
                    val location = org.bukkit.Location(
                        player.world, // 또는 특정 월드 사용 가능: Bukkit.getWorld("world_name")
                        1290.478, 128.0, -183.7
                    )
                    player.teleport(location)
                    player.sendMessage("§a[!] 캐시샵으로 이동합니다.")
                    return@Runnable
                }
//클릭했을 때 다른 페이지로 넘어가도록 임시로 만들어놓음
                object : HQContainer(54, name) {
                    override fun initialize(inventory: Inventory) {
                        HQButtonBuilder(Material.BARRIER)
                            .setDisplayName("&c돌아가기".colorize())
                            .setLore(listOf("&7이전 페이지로 돌아갑니다.".colorize()))
                            .setClickFunction {
                                player.closeInventory()
                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(this::class.java), Runnable {
                                    UserInterface(page, player).open(player)
                                }, 1L)
                            }
                            .build().setSlot(this, 49)
                    }
                }.open(player)
            }, 1L)
        }
    }

    private fun setEncyclopediaButton(slots: List<Int>) {
        TransparentButtonFactory.addTransparentButton(
            container = this,
            name = "&a도감",
            lore = listOf("&7클릭 시 도감 메뉴로 이동합니다."),
            slots = slots
        ) { player ->
            player.closeInventory()
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(this::class.java), Runnable {
                UserInterface(4, player).open(player)
            }, 1L)
        }
    }


    private fun setTransparentPageButton(targetPage: Int, slots: List<Int>) {
        val pageName = when (targetPage) {
            1 -> "1페이지로 이동"
            2 -> "2페이지로 이동"
            3 -> "3페이지로 이동"
            else -> "다른 페이지"
        }
        TransparentButtonFactory.addTransparentButton(
            container = this,
            name = "&b$pageName",
            lore = listOf("&7클릭 시 해당 페이지로 이동합니다."),
            slots = slots
        ) { player ->
            player.closeInventory()
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(this::class.java), Runnable {
                UserInterface(targetPage, player).open(player)
            }, 1L)
        }
    }

    private fun setTransparentLinkButton(name: String, url: String, slots: List<Int>) {
        TransparentButtonFactory.addTransparentButton(
            container = this,
            name = "&6$name",
            lore = listOf("&f외부 링크: &b$url"),
            slots = slots
        ) { player ->
            player.closeInventory()
            player.sendMessage("&e$name 링크: &f$url".colorize())
        }
    }
}