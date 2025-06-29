package kr.hqservice.project_x.practice.core.command

import kr.hqservice.framework.command.ArgumentLabel
import kr.hqservice.framework.command.Command
import kr.hqservice.framework.command.CommandExecutor
import org.bukkit.entity.Player
import java.util.*

@Command(label = "창고이름변경")
class UserInterfaceTitleCommand {
//페이지 번호별로 다른 창고 제목 저장
    companion object {
        private val playerTitleMap = mutableMapOf<Pair<UUID, Int>, String>()

        fun getTitle(player: Player, page: Int): String {
            return playerTitleMap[player.uniqueId to page]
                ?: when (page) {
                    1 -> "&fÑÆÆÆÆꐐ"
                    2 -> "&fÑÆÆÆÆê"
                    3 -> "&fÑÆÆÆÆ¶"
                    4 -> "&fÑÆÆÆÆ◙Æ∆"
                    101 -> "&fÑÆÆÆÆÏ"
                    201 -> "&fÑÆÆÆÆÏ"
                    else -> "📦 창고"
                }
        }
    }

    @CommandExecutor(label = "설정", description = "페이지별 창고 UI 이름을 설정합니다.")
    fun setTitle(sender: Player, @ArgumentLabel("페이지") page: Int, @ArgumentLabel("이름") name: String) {
        playerTitleMap[sender.uniqueId to page] = name
        sender.sendMessage("§a${page}페이지의 창고 UI 제목이 §f$name §a(으)로 설정되었습니다.")
    }
}
