package kr.hqservice.project_x.practice.core.command
import kr.hqservice.framework.command.Command
import kr.hqservice.framework.command.CommandExecutor
import kr.hqservice.projectx.practice.core.container.UserInterface
import org.bukkit.entity.Player

@Command(label = "통합인터페이스")
class UserInterfaceCommand {
    @CommandExecutor(label = "열기", description = "통합 인터페이스를 열어줍니다.")
    fun open(sender: Player) {
        UserInterface(player = sender).open(sender)
    }
}
