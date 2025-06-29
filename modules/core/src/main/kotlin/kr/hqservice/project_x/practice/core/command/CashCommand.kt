package kr.hqservice.project_x.practice.core.command

import kr.hqservice.framework.command.ArgumentLabel
import kr.hqservice.framework.command.Command
import kr.hqservice.framework.command.CommandExecutor
import kr.hqservice.project_x.practice.core.controller.StatDataController
import org.bukkit.entity.Player

//캐시 지급용
@Command(label = "캐시")
class CashCommand(
    private val statDataController: StatDataController
) {

    @CommandExecutor(label = "지급", description = "캐시를 지급합니다.")
    fun giveCash(sender: Player, @ArgumentLabel("금액") amount: Int) {
        statDataController.add(sender.uniqueId, amount)
        sender.sendMessage("§a$amount 캐시를 지급받았습니다.")
    }
}
