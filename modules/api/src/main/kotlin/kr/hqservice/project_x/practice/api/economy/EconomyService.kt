package kr.hqservice.project_x.practice.api.economy

import java.util.*

interface EconomyService {

    suspend fun findWallet(playerId: UUID): Wallet?

    //코루틴 전용 호출어는 suspend.
    //suspend는 코루틴 아니면 호출안됨
    //지갑 -> 데이터베이스 안에 있음(메인스레드가 데이터베이스까지 왔다갔다하기까지 기다리면 락이 걸림 서버가 데이터를 불러올 때마다 뚝 끊김 마크는 모든 동작이 main에 묶여있음)

}