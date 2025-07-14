package kr.hqservice.project_x.practice.api.economy

import java.util.UUID

interface Wallet {
    fun getOwner(): UUID
    fun getMoney(key: String): Long
}