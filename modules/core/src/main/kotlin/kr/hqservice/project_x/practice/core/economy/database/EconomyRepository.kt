package kr.hqservice.project_x.practice.core.economy.database

import kotlinx.coroutines.Dispatchers
import kr.hqservice.framework.database.extension.findByIdForUpdate
import kr.hqservice.framework.database.repository.Repository
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

// Exposed ORM을 사용할 때는 newSuspendedTransection메소드를 사용함
@Repository
class EconomyRepository {
    suspend fun registerWallet(playerId: UUID) {

        //coroutine -> {} 안의 코드를 실행 할 디스패처를 지정 ( Thread )
        // IO / Default / BukkitMain ( Main ) / BukkitAsync
        // 만약에 밑에서 (Dispatcher.IO)를 지정하지 않으면 상단에서 registerWallet을 호출한 코루틴의 디스패처에서 돌아감
        // 버킷 메인에서 호출했다면 메인 스레드에서 돌아감 코루틴은 병렬로 돌아가기 때문에 메인스레드에 블로킹은 없음
        // runBlocking(이게 동작될 때까지 멈춰라)을 쓰면 멈춤 쓸일 없지만
        //newSuspendedTransaction(Dispatchers.IO)에서 Dispatchers.BukkitMain을 쓰면 완전 동기화돼서 버킷이랑 같이 돎. 데이터베이스 접근할 때는 IO가 좋음.
        //BukkitMain은 어느때 쓰냐면 코루틴으로 돌긴 도는데 엔티티에 대한 뭔가 텔레포트 이런 접근이 있거나 블록을 바꾸거나 월드에 대한 변화를 주거나 할 때는 무조건 메인쓰레드에서 이루어져야 함. 월드란 딱 틱이 맞아야 하기 때문에
        //BukkitAsync는 버킷 안에서 요리같은 걸 할때 요리가 30초가 걸린다 가정하면, 뒤에서 30초를 돌려야 한다. 이때 BukkitAsync를 쓴다. 버킷이랑 틱은 똑같이 돌리는데 완료되었다 하는 알림도 유저한테 보여줘야 할 때
        //Default는 완전 외부적인 동작들. 디스코드랑 통신되는 애를 만들었는데 서버랑 틱을 맞출 필요도 없고, Main이랑 돌리면 메인이 블로킹되거 읽기쓰기 잠깐인 io쓸 필요도 없음
        newSuspendedTransaction(Dispatchers.IO) {
            EconomyEntity.new(playerId) {
                this.walletData = ""
                //object EconomyEntityTable {}의 var walletData = text("wallet_data").nullable().default(null)이면 기본값 지정 안하고, 비워두고 EconomyEntity.new(playerId) {}만 해도 됨
            }
        }
    }
    suspend fun findWallet(playerId: UUID): EconomyEntity? {
        return newSuspendedTransaction(Dispatchers.IO) {
            EconomyEntity.findById(playerId)
        }
    }

    suspend fun findWalletForUpdate(playerId: UUID, updateFunc: (EconomyEntity, Boolean) -> String) {
        newSuspendedTransaction(Dispatchers.IO) {
            var isCreated = false
            val entity =
                EconomyEntity.findByIdForUpdate(playerId) ?: EconomyEntity.new(playerId) {
                    isCreated = true
                    this.walletData = ""
                }
            entity.walletData = updateFunc(entity, isCreated)

        }
    }
}

//suspend fun pushWalletData(wallet: walletImpl, key: String, value: Long) {
//
//        newSuspendedTransaction(Dispatchers.IO) {
//            val map = wallet.getWalletMap().toMutableMap()
//            map[key] = (map[key] ?:0) + value
//            //있으면 원래거에 더해주고 없으면 0+value
//            // "gold:1000, cash:200" 이런 식으로 가져옴
//
//        }