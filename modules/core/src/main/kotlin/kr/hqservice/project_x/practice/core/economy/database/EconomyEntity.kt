package kr.hqservice.project_x.practice.core.economy.database

import kr.hqservice.framework.database.component.Table
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

class EconomyEntity(id: EntityID<UUID>) : UUIDEntity(id){
    //
    companion object : UUIDEntityClass<EconomyEntity>(EconomyEntityTable)

    //by -> val a = 10하면 a에 대한 변수 생성을 class EconomyEntity가 처리함
    //      var b = 20해도 마찬가지
    var walletData by EconomyEntityTable.walletData
    //반면 by는 생성권한이 뒤에 있는 object EconmyEntityTable에 있다
    //위에 있는 EconomyEntity가 안한다
    //규칙)데이터베이스에 데이터가 있기 때문에 db를 불러오는 녀석이 만들어서 필드를 넘겨줘야 값이 들어가기 때문
    //var 변수명 by EconomyEntityTable.walletData

}
// 테이블은 어노테이션 달아줘야 됨
// 테이블은 하나만 있어야 됨 데이터베이스에 같은 이름의 두개의 테이블이 존재하면 안 되니 object 사용
// var 변경 가능
// val 변경 불가능

//UUIDEntityClass

@Table
object EconomyEntityTable : UUIDTable("user_wallet") {
    var walletData = text("wallet_data")
}
//var walletData = text("wallet_data")의 경우에는 컬럼명:wallet_data, 타입:TEXT
//var walletD = varchar("a",32)처럼 선언하면 db에서 컬럼명/데이터 유형(타입)/길이&설정에서 a, varchar, 32와 같이 들어감
//기본값이 없다면 var walletData = text("wallet_data").nullable()
//기본값이 있다면 var walletData = text("wallet_data").default("a")
//예시) var b = integer("aa").default(10).nullable()
