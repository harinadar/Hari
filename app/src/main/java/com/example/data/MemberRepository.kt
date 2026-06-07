package com.example.data

import kotlinx.coroutines.flow.Flow

class MemberRepository(private val memberDao: MemberDao) {
    val allMembers: Flow<List<Member>> = memberDao.getAllMembers()

    suspend fun insert(member: Member): Long {
        return memberDao.insertMember(member)
    }

    suspend fun delete(member: Member) {
        memberDao.deleteMember(member)
    }

    suspend fun deleteById(memberId: Int) {
        memberDao.deleteMemberById(memberId)
    }
}
