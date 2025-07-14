package kr.hqservice.project_x.practice.core.command

import kotlinx.coroutines.CoroutineScope
import kr.hqservice.framework.command.ArgumentLabel
import kr.hqservice.framework.command.Command
import kr.hqservice.framework.command.CommandExecutor
import kr.hqservice.project_x.practice.core.container.FurnitureShopContainer
import kr.hqservice.project_x.practice.core.economy.EconomyServiceImpl
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

// targetName으로 플레이어 이름 획득
@Command(label = "furnitureshop")
class FurnitureShopCommand(
    private val coroutineScope: CoroutineScope,
    private val economyServiceImpl: EconomyServiceImpl
) {

    @CommandExecutor(label = "open", description = "가구 상점을 엽니다.")
    fun open(sender: CommandSender, @ArgumentLabel("플레이어") targetName: String) {
        val target = Bukkit.getPlayerExact(targetName)

        if (target == null || !target.isOnline) {
            sender.sendMessage("§c해당 플레이어는 접속 중이 아닙니다.")
            return
        }

        FurnitureShopContainer(target, 101, coroutineScope, economyServiceImpl).open(target)
        sender.sendMessage("§a${target.name}의 가구 상점을 열었습니다.")
    }
}

